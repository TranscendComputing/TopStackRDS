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
import com.transcend.rds.message.PurchaseReservedDBInstancesOfferingActionMessage.PurchaseReservedDBInstancesOfferingActionRequestMessage;
import com.transcend.rds.message.PurchaseReservedDBInstancesOfferingActionMessage.PurchaseReservedDBInstancesOfferingActionResultMessage;

/**
 * @author tdhite
 */
public class PurchaseReservedDBInstancesOfferingActionWorker extends 
		AbstractWorker<PurchaseReservedDBInstancesOfferingActionRequestMessage, PurchaseReservedDBInstancesOfferingActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(PurchaseReservedDBInstancesOfferingActionWorker.class
					.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public PurchaseReservedDBInstancesOfferingActionResultMessage doWork(
           PurchaseReservedDBInstancesOfferingActionRequestMessage req) throws Exception {
       logger.debug("Performing work for PurchaseReservedDBInstancesOfferingAction.");
       return super.doWork(req, getSession());
   }

	@Override
	protected PurchaseReservedDBInstancesOfferingActionResultMessage doWork0(PurchaseReservedDBInstancesOfferingActionRequestMessage req,
			ServiceRequestContext context) throws Exception {

		throw RDSQueryFaults
				.InvalidAction("PurchaseReservedDBInstancesOffering is not supported in this version of Transcend RDS.");
	}
}
