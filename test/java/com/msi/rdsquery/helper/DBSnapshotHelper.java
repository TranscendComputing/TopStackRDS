package com.msi.rdsquery.helper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.CreateDBSnapshotRequest;
import com.amazonaws.services.rds.model.DBSnapshot;
import com.amazonaws.services.rds.model.DeleteDBSnapshotRequest;
import com.amazonaws.services.rds.model.DescribeDBSnapshotsRequest;
import com.amazonaws.services.rds.model.DescribeDBSnapshotsResult;
import com.msi.tough.core.Appctx;
import com.msi.tough.helper.AbstractHelper;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.workflow.WorkflowSubmitter;

@Component
@Scope("prototype")
public class DBSnapshotHelper extends AbstractHelper<String> {
    private final static Logger logger = Appctx
            .getLogger(DBSnapshotHelper.class.getName());
    
    @Autowired
    private RunningDBInstanceHelper dbInstHelper;
    
    @Autowired
    private AmazonRDSClient rdsClient;
    
    private static final int MAX_WAIT_SECS = 300;
    private static final int WAIT_SECS = 15;

    /**
    *
    */
   public DBSnapshotHelper() {
       super();
   }
  
    /**
     * Create a group.
     *
     * @param instName
     * @throws InterruptedException 
     */
    public void createDBSnapshot(String snapshotName) throws InterruptedException {
    	createDBSnapshot(snapshotName, null);
    }
    
    
    /**
     * Create a group.
     *
     * @param instName
     * @throws InterruptedException 
     */
    public void createDBSnapshot(String snapshotName, String instName) throws InterruptedException {
    	//Creating new DBInstance if necesary
    	if(instName == null) {
        	instName = snapshotName + "-baseInst";
        	instName = dbInstHelper.getOrCreateDBInstance(instName);
        	logger.debug("Waiting for DBInstance to be created...");
        	boolean res = dbInstHelper.waitForDBInst(instName);
        	if (res == false)
        		throw RDSQueryFaults.DBInstanceNotFound();
    	}
    	
        final CreateDBSnapshotRequest request = new CreateDBSnapshotRequest();
        request.withDBInstanceIdentifier(instName);
        request.withDBSnapshotIdentifier(snapshotName);
    	rdsClient.createDBSnapshot(request);
    	addEntity(snapshotName);
    }
    
    /**
     * Return name of available DBSnapshot, if none, create a new one
     * instName, the name of a valid DBInstance in 'available' state
     * @throws InterruptedException
     */   
    public String getOrCreateDBSnapshot(String snapshotName) throws InterruptedException {
    	return getOrCreateDBSnapshot(snapshotName, null);    	
    }
    /**
     * Return name of available DBSnapshot, if none, create a new one
     * instName, the name of a valid DBInstance in 'available' state
     * @throws InterruptedException
     */
    public String getOrCreateDBSnapshot(String snapshotName, String instName) throws InterruptedException {
    	Collection<String> existing = getExistingEntities();
        final DescribeDBSnapshotsRequest request = new DescribeDBSnapshotsRequest();
    	DescribeDBSnapshotsResult snapshots;    	
    	//Checking if snapshot already available
    	for(String nextSnapshot : existing) {
    		try {
    			request.withDBInstanceIdentifier(nextSnapshot);
    			snapshots = rdsClient.describeDBSnapshots(request);
    			if(snapshots.getDBSnapshots().get(0).getStatus().equals("available"))
            		return nextSnapshot;
    		}
        	catch(Exception e) {
        			logger.debug("Snapshot " + nextSnapshot + " not found");
        	}
    	}
    	
    	logger.debug("Snapshot not found, creating a new one");
    	//Creating new DBInstance if necesary
    	if(instName == null) {
        	instName = snapshotName + "-baseInst";
        	instName = dbInstHelper.getOrCreateDBInstance(instName);
        	logger.debug("Waiting for DBInstance to be created...");
        	boolean res = dbInstHelper.waitForDBInst(instName);
        	if (res == false)
        		throw RDSQueryFaults.DBInstanceNotFound();
    	}
    	createDBSnapshot(snapshotName, instName);
    	return snapshotName;
    }
    
    /**
     * Wait for DBSnapshot to be in available state
     *
     * @throws InterruptedException
     */
    public boolean waitForDBSnapshot(String snapName) throws InterruptedException {
    	DescribeDBSnapshotsRequest request = new DescribeDBSnapshotsRequest();
    	DescribeDBSnapshotsResult result;
    	DBSnapshot dbSnapshot;    	
    	
        request.withDBSnapshotIdentifier(snapName);
        for (int count = 0; count < MAX_WAIT_SECS; count += WAIT_SECS) {
            try {
            	
                result = rdsClient.describeDBSnapshots(request);
                if (result.getDBSnapshots().size() == 0){
                		logger.debug("No snapshot found");
                    	return false;
                }
                else {
                	dbSnapshot = result.getDBSnapshots().get(0);
                	if(dbSnapshot.getStatus().equals("available"))
                    	return true;
                }
            	
            	logger.debug("Time passed: " + count + " seconds");
                
            } catch (Exception e) {
            	logger.debug("There was an error: " + e.toString() + " Snapshot " + snapName + " not initialized");
            	return false;
            }
            Thread.sleep(1000 * WAIT_SECS);
        }
        logger.debug("WaitForDBSnapshot timed out");
        return false;
    }
    

    /**
     * Find a DBSnapshot (via describe).
     *
     * @param instName
     */
    public DBSnapshot findDBInst(String instName) {
        final DescribeDBSnapshotsRequest request = new DescribeDBSnapshotsRequest();
        request.withDBSnapshotIdentifier(instName);
        DescribeDBSnapshotsResult result =
        		rdsClient.describeDBSnapshots(request);
        if (result.getDBSnapshots().size() > 0) {
            return result.getDBSnapshots().get(0);
        }
        return null;
    }
    
    /**
     * Construct a delete snapshot request.
     *
     * @param userName
     * @return
     */
    private DeleteDBSnapshotRequest deleteDBSnapshotRequest(String name) {
        final DeleteDBSnapshotRequest request = new DeleteDBSnapshotRequest();
        request.withDBSnapshotIdentifier(name);
        return request;
    }

    /**
     * Delete an snapshot with the given name.
     *
     * @param name
     */
    public void deleteDBSnapshot(String name) throws Exception {
    	try {
    		DeleteDBSnapshotRequest request = deleteDBSnapshotRequest(name);
    		rdsClient.deleteDBSnapshot(request);
    	}
    	catch (Exception e) {
    		logger.debug("Snapshot " + name + " does not exist");
    	}
        removeEntity(name);
    }


	@Override
	public String entityName() {
		return "DBSnapshot";
	}


	@Override
	public void create(String identifier) throws Exception {
		createDBSnapshot(identifier);
	}


	@Override
	public void delete(String identifier) throws Exception {
		deleteDBSnapshot(identifier);
	}


	@Override
	public void setWorkflowSubmitter(WorkflowSubmitter submitter) {		
	}
}
