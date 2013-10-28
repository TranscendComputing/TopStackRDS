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
import com.transcend.rds.message.DescribeReservedDBInstancesOfferingsActionMessage.DescribeReservedDBInstancesOfferingsActionRequestMessage;
import com.transcend.rds.message.DescribeReservedDBInstancesOfferingsActionMessage.DescribeReservedDBInstancesOfferingsActionResultMessage;

public class DescribeReservedDBInstancesOfferingsActionWorker extends 
		AbstractWorker<DescribeReservedDBInstancesOfferingsActionRequestMessage, DescribeReservedDBInstancesOfferingsActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DescribeReservedDBInstancesOfferingsActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DescribeReservedDBInstancesOfferingsActionResultMessage doWork(
           DescribeReservedDBInstancesOfferingsActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DescribeReservedDBInstancesOfferingsAction.");
       return super.doWork(req, getSession());
   }



	@Override
	protected DescribeReservedDBInstancesOfferingsActionResultMessage doWork0(DescribeReservedDBInstancesOfferingsActionRequestMessage req,
			ServiceRequestContext context) throws Exception {
		throw RDSQueryFaults
				.InvalidAction("DescribeReservedDBInstancesOfferings is not supported in this version of Transcend RDS.");
	}
}
