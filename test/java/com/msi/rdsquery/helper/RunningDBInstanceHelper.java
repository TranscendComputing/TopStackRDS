package com.msi.rdsquery.helper;

import java.util.Collection;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.msi.tough.core.Appctx;
import com.msi.tough.helper.AbstractHelper;
import com.msi.tough.workflow.WorkflowSubmitter;

@Component
@Scope("prototype")
public class RunningDBInstanceHelper extends AbstractHelper<String> {
    private final static Logger logger = Appctx
            .getLogger(RunningDBInstanceHelper.class.getName());

    @Autowired
    private AmazonRDSClient rdsClient;

    
    private static final int MAX_WAIT_SECS = 300;
    private static final int WAIT_SECS = 15;

    /**
    *
    */
   public RunningDBInstanceHelper() {
       super();
   }

   
    /**
     * Construct a minimal valid DBInstance request.
     *
     * @param instName
     * @return
     */
    public CreateDBInstanceRequest createDBInstRequest(String instName) {
        final CreateDBInstanceRequest request = new CreateDBInstanceRequest();
        request.setAllocatedStorage(5);
        request.setDBInstanceClass("db.m1.medium");
        request.setDBInstanceIdentifier(instName);
        request.setEngine("MySQL");
        request.setMasterUsername("userName");
        request.setMasterUserPassword("myPassword");
        request.withDBName("dbName");
        return request;
    }

    /**
     * Create a group.
     *
     * @param instName
     */
    public void createDBInstance(String instName) {
    	CreateDBInstanceRequest request = createDBInstRequest(instName);
    	rdsClient.createDBInstance(request);
    	addEntity(instName);
    }
    
    /**
     * Return name of available dbInstance, if none, create a new one
     *
     * @throws InterruptedException
     */
    public String getOrCreateDBInstance(String instName) throws InterruptedException {
    	Collection<String> existing = getExistingEntities();
        final DescribeDBInstancesRequest request = new DescribeDBInstancesRequest();
    	DescribeDBInstancesResult dbInst;
    	//Checking if instance already available
    	for(String nextInstanceName : existing) {
    		try {
    			request.withDBInstanceIdentifier(nextInstanceName);
    			dbInst = rdsClient.describeDBInstances(request);
    			if(dbInst.getDBInstances().get(0).getDBInstanceStatus().equals("available"))
            		return nextInstanceName;
    		}
        	
        	catch(Exception e) {
        			logger.debug("Instance " + nextInstanceName + " not found");
        	}
    	}
    	
    	//No instance found, create a new one
    	createDBInstance(instName);
    	return instName;
    }
    
    /**
     * Wait for DBInstance to be in available state
     *
     * @throws InterruptedException
     */
    public boolean waitForDBInst(String instName) throws InterruptedException {
    	return waitForDBInst(instName, MAX_WAIT_SECS);
    }
    /**
     * Wait for DBInstance to be in available state
     *
     * @throws InterruptedException
     */
    public boolean waitForDBInst(String instName, int maxWait) throws InterruptedException {
    	DescribeDBInstancesRequest request = new DescribeDBInstancesRequest();
    	DescribeDBInstancesResult result;
    	DBInstance dbInst;
        request.withDBInstanceIdentifier(instName);
        for (int count = 0; count < maxWait; count += WAIT_SECS) {
            try {
            	
                result = rdsClient.describeDBInstances(request);
                if (result.getDBInstances().size() == 0)
                    	return false;
                else {
                    dbInst = result.getDBInstances().get(0);
                	if(dbInst.getDBInstanceStatus().equals("available"))
                    	return true;
                }
            	
            	logger.debug("Time passed: " + count + " seconds");
                
            } catch (Exception e) {
                return false;
            }
            Thread.sleep(1000 * WAIT_SECS);
        }
        logger.debug("Instance creation");
        return false;
    }
    

    /**
     * Find a dbInstance (via describe).
     *
     * @param instName
     */
    public DBInstance findDBInst(String instName) {
        final DescribeDBInstancesRequest request = new DescribeDBInstancesRequest();
        request.withDBInstanceIdentifier(instName);
        DescribeDBInstancesResult result =
        		rdsClient.describeDBInstances(request);
        if (result.getDBInstances().size() > 0) {
            return result.getDBInstances().get(0);
        }
        return null;
    }
    
    /**
     * Construct a delete instance request.
     *
     * @param userName
     * @return
     */
    private DeleteDBInstanceRequest deleteDBInstanceRequest(String name) {
        final DeleteDBInstanceRequest request = new DeleteDBInstanceRequest();
        request.withDBInstanceIdentifier(name);
        request.withSkipFinalSnapshot(true);
        return request;
    }
    
     /**
      * Delete a created instance
      */
    public void deleteSingleInstance(String name) throws Exception {
    	final DeleteDBInstanceRequest request = deleteDBInstanceRequest(name);
    	rdsClient.deleteDBInstance(request);
        removeEntity(name);
    }

	@Override
	public String entityName() {
		return "RunningDBInstance";
	}


	@Override
	public void create(String identifier) throws Exception {
		createDBInstance(identifier);		
	}


	@Override
	public void delete(String identifier) throws Exception {
		deleteSingleInstance(identifier);
	}


	@Override
	public void setWorkflowSubmitter(WorkflowSubmitter submitter) {	
	}
}
