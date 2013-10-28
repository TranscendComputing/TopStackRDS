/**
 * 
 */
package com.transcend.rds.worker;



import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.RestoreDBInstanceToPointInTimeActionMessage.RestoreDBInstanceToPointInTimeActionRequestMessage;
import com.transcend.rds.message.RestoreDBInstanceToPointInTimeActionMessage.RestoreDBInstanceToPointInTimeActionResultMessage;

/**
 * @author tdhite
 */
public class RestoreDBInstanceToPointInTimeActionWorker extends 
		AbstractWorker<RestoreDBInstanceToPointInTimeActionRequestMessage, RestoreDBInstanceToPointInTimeActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(RestoreDBInstanceToPointInTimeActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public RestoreDBInstanceToPointInTimeActionResultMessage doWork(
           RestoreDBInstanceToPointInTimeActionRequestMessage req) throws Exception {
       logger.debug("Performing work for RestoreDBInstanceToPointInTimeAction.");
       return super.doWork(req, getSession());
   }

	/**
	 * restoreDBInstanceToPointInTime *****************************************
	 * Users can restore to any point in time before the latestRestorableTime
	 * for up to backupRetentionPeriod days. The target database is created from
	 * the source database restore point with the same configuration as the
	 * original source database, except that the new RDS instance is created
	 * with the default security group. Request: SourceDBInstanceIdentifier(R)
	 * The identifier of the source DB Instance from which to restore
	 * TargetDBInstanceIdentifier(R) The name of the new database instance to be
	 * created. RestoreTime UseLatestRestorableTime DBInstanceClass
	 * AvailabilityZone Port MultiAZ AutoMinorVersionUpgrade Response: Instance
	 * record giving details of instance that was restored/created Exceptions:
	 * InvalidDBInstanceState DBInstanceAlreadyExists DBInstanceNotFound
	 * PointInTimeRestoreNotEnabled . InstanceQuotaExceeded
	 * InsufficientDBInstanceCapacity StorageQuotaExceeded Processing 1.
	 * Validate and insert the newInstance record 2. call the BackupManager to
	 * restore to this new instance 3. Select and return the new instance
	 * details to the user
	 */
	@Override
	protected RestoreDBInstanceToPointInTimeActionResultMessage doWork0(RestoreDBInstanceToPointInTimeActionRequestMessage req,
			ServiceRequestContext context) throws Exception {

		// TODO before starting to work on this action, enable CreateDBInstance
		// to spin up an instance that periodically makes a full backup
		// It is also required for DBInstance to write events when backup is
		// started and finished
		// Event msg before backup: Backing up database instance
		// Event msg after backup: Finished backup
		// Possibly another event exists after restoring to a point in time =>
		// check this against AWS first

		/*
		 * if(instance.getSourceDbinstanceId() != null){ throw
		 * RDSQueryFaults.InvalidDBInstanceState
		 * ("Cannot create a snapshot because the DB Instance is a read replica."
		 * ); }
		 */

		throw RDSQueryFaults
		.InvalidAction("RestoreDBInstanceToPointInTime is not supported in this version of Transcend RDS.");
		
	}
}
