package com.transcend.rds.worker;

import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.CreateDBSubnetGroupActionMessage.CreateDBSubnetGroupActionRequestMessage;
import com.transcend.rds.message.CreateDBSubnetGroupActionMessage.CreateDBSubnetGroupActionResultMessage;

public class CreateDBSubnetGroupActionWorker extends 
		AbstractWorker<CreateDBSubnetGroupActionRequestMessage, CreateDBSubnetGroupActionResultMessage> {

	private final static Logger logger = Appctx
			.getLogger(CreateDBSubnetGroupActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public CreateDBSubnetGroupActionResultMessage doWork(
           CreateDBSubnetGroupActionRequestMessage req) throws Exception {
       logger.debug("Performing work for CreateDBSubnetGroupAction.");
       return super.doWork(req, getSession());
   }
   
	
	@Override
	protected CreateDBSubnetGroupActionResultMessage doWork0(CreateDBSubnetGroupActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		throw RDSQueryFaults
				.InvalidAction("CreateDBSubnetGroup is not supported in this version of Transcend RDS.");
	}

}
