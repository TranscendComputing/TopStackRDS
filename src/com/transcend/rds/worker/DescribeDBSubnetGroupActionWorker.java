package com.transcend.rds.worker;

import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DescribeDBSubnetGroupActionMessage.DescribeDBSubnetGroupActionRequestMessage;
import com.transcend.rds.message.DescribeDBSubnetGroupActionMessage.DescribeDBSubnetGroupActionResultMessage;

public class DescribeDBSubnetGroupActionWorker extends 
		AbstractWorker<DescribeDBSubnetGroupActionRequestMessage, DescribeDBSubnetGroupActionResultMessage> {

	private final static Logger logger = Appctx
			.getLogger(DescribeDBSubnetGroupActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DescribeDBSubnetGroupActionResultMessage doWork(
           DescribeDBSubnetGroupActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DescribeDBSubnetGroupAction.");
       return super.doWork(req, getSession());
   }
   
	@Override
	protected DescribeDBSubnetGroupActionResultMessage doWork0(DescribeDBSubnetGroupActionRequestMessage req,
			ServiceRequestContext context) throws Exception {

		throw RDSQueryFaults
				.InvalidAction("DescribeDBSubnetGroup is not supported in this version of Transcend RDS.");
	}

}
