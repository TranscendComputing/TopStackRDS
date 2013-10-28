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
import com.transcend.rds.message.ModifyDBInstanceActionMessage.ModifyDBInstanceActionRequestMessage;
import com.transcend.rds.message.ModifyDBInstanceActionMessage.ModifyDBInstanceActionResultMessage;

/**
 * @author tdhite
 */
public class ModifyDBInstanceActionWorker extends 
		AbstractWorker<ModifyDBInstanceActionRequestMessage, ModifyDBInstanceActionResultMessage> { 

	private final static Logger logger = Appctx
			.getLogger(ModifyDBInstanceActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public ModifyDBInstanceActionResultMessage doWork(
           ModifyDBInstanceActionRequestMessage req) throws Exception {
       logger.debug("Performing work for ModifyDBInstanceAction.");
       return super.doWork(req, getSession());
   }

	@Override
	protected ModifyDBInstanceActionResultMessage doWork0(ModifyDBInstanceActionRequestMessage req,
			ServiceRequestContext context) throws Exception {

		throw RDSQueryFaults
				.InvalidAction("ModifyDBInstance is not supported in this version of Transcend RDS.");

	}
}
