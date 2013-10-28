package com.transcend.rds.worker;

import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DeleteDBSubnetGroupActionMessage.DeleteDBSubnetGroupActionRequestMessage;
import com.transcend.rds.message.DeleteDBSubnetGroupActionMessage.DeleteDBSubnetGroupActionResultMessage;

public class DeleteDBSubnetGroupActionWorker extends 
		AbstractWorker<DeleteDBSubnetGroupActionRequestMessage, DeleteDBSubnetGroupActionResultMessage> { 
	
	private final static Logger logger = Appctx
			.getLogger(DeleteDBSubnetGroupActionWorker.class.getName());
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DeleteDBSubnetGroupActionResultMessage doWork(
           DeleteDBSubnetGroupActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DeleteDBSubnetGroupAction.");
       return super.doWork(req, getSession());
   }
   
	@Override
	protected DeleteDBSubnetGroupActionResultMessage doWork0(DeleteDBSubnetGroupActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		throw RDSQueryFaults
				.InvalidAction("DeleteDBSubnetGroup is not supported in this version of Transcend RDS.");
	}

}
