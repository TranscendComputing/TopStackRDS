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
import com.transcend.rds.message.DescribeOrderableDBInstanceOptionsActionMessage.DescribeOrderableDBInstanceOptionsActionRequestMessage;
import com.transcend.rds.message.DescribeOrderableDBInstanceOptionsActionMessage.DescribeOrderableDBInstanceOptionsActionResultMessage;

/**
 * @author tdhite
 */
public class DescribeOrderableDBInstanceOptionsActionWorker extends
		AbstractWorker<DescribeOrderableDBInstanceOptionsActionRequestMessage, DescribeOrderableDBInstanceOptionsActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DescribeOrderableDBInstanceOptionsActionWorker.class.getName());

    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DescribeOrderableDBInstanceOptionsActionResultMessage doWork(
           DescribeOrderableDBInstanceOptionsActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DescribeOrderableDBInstanceOptionsAction.");
       return super.doWork(req, getSession());
   }
   
	@Override
	protected DescribeOrderableDBInstanceOptionsActionResultMessage doWork0(DescribeOrderableDBInstanceOptionsActionRequestMessage req,
ServiceRequestContext context) throws Exception {
		throw RDSQueryFaults
				.InvalidAction("DescribeOrderableDBInstanceOptions is not supported in this version of Transcend RDS.");
	}
}
