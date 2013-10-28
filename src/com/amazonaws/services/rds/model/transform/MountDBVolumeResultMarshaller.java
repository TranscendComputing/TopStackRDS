package com.amazonaws.services.rds.model.transform;

import org.slf4j.Logger;

import com.transcend.rds.message.MountDBVolumeMessage.MountDBVolumeResultMessage;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.RDS_Constants;

public class MountDBVolumeResultMarshaller implements
		Marshaller<String, MountDBVolumeResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(MountDBVolumeResultMarshaller.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final MountDBVolumeResultMessage input)
			 {
		logger.debug("Finished MountDBVolume implementation");
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_DESCRIBEEVENTSRESPONSE);
	
		return nodeResponse.toString();
	}
}
