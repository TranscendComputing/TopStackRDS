package com.transcend.rds.worker;

import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.ModifyDBSubnetGroupActionMessage.ModifyDBSubnetGroupActionRequestMessage;
import com.transcend.rds.message.ModifyDBSubnetGroupActionMessage.ModifyDBSubnetGroupActionResultMessage;

public class ModifyDBSubnetGroupActionWorker extends 
		AbstractWorker<ModifyDBSubnetGroupActionRequestMessage, ModifyDBSubnetGroupActionResultMessage> { 
	private final static Logger logger = Appctx
			.getLogger(ModifyDBSubnetGroupActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public ModifyDBSubnetGroupActionResultMessage doWork(
           ModifyDBSubnetGroupActionRequestMessage req) throws Exception {
       logger.debug("Performing work for ModifyDBSubnetGroupAction.");
       return super.doWork(req, getSession());
   }

	@Override
	protected ModifyDBSubnetGroupActionResultMessage doWork0(ModifyDBSubnetGroupActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		throw RDSQueryFaults
				.InvalidAction("ModifyDBSubnetGroup is not supported in this version of Transcend RDS.");
	}

}
