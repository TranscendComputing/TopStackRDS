package com.amazonaws.services.rds.model.transform;

import org.slf4j.Logger;

import com.transcend.rds.message.SignalCreateDBSnapshotMessage.SignalCreateDBSnapshotResultMessage;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.RDS_Constants;

public class SignalCreateDBSnapshotResultMarshaller implements
		Marshaller<String, SignalCreateDBSnapshotResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(SignalCreateDBSnapshotResultMarshaller.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final SignalCreateDBSnapshotResultMessage input)
			 {
		logger.debug("Finished SignalCreateDBSnapshot implementation");
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_DESCRIBEEVENTSRESPONSE);
	
		return nodeResponse.toString();
	}
}
