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
import com.transcend.rds.message.DescribeReservedDBInstancesActionMessage.DescribeReservedDBInstancesActionRequestMessage;
import com.transcend.rds.message.DescribeReservedDBInstancesActionMessage.DescribeReservedDBInstancesActionResultMessage;

public class DescribeReservedDBInstancesActionWorker extends
		AbstractWorker<DescribeReservedDBInstancesActionRequestMessage, DescribeReservedDBInstancesActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DescribeReservedDBInstancesActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DescribeReservedDBInstancesActionResultMessage doWork(
           DescribeReservedDBInstancesActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DescribeReservedDBInstancesAction.");
       return super.doWork(req, getSession());
   }
	
	@Override
	protected DescribeReservedDBInstancesActionResultMessage doWork0(DescribeReservedDBInstancesActionRequestMessage req,
			ServiceRequestContext context) throws Exception {
		throw RDSQueryFaults
				.InvalidAction("DescribeReservedDBInstances is not supported in this version of Transcend RDS.");

	}
}
