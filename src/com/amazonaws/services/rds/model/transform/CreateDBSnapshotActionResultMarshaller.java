package com.amazonaws.services.rds.model.transform;

import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;
import com.transcend.rds.message.CreateDBSnapshotActionMessage.CreateDBSnapshotActionResultMessage;

public class CreateDBSnapshotActionResultMarshaller implements
		Marshaller<String, CreateDBSnapshotActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final CreateDBSnapshotActionResultMessage input)
			 {
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_CREATEDBSNAPSHOTRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_CREATEDBSNAPSHOTRESULT);

		marshalDBSnapshot(nr, input);

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
	
	public static void marshalDBSnapshot(XMLNode parent, CreateDBSnapshotActionResultMessage o) {
		XMLNode n = QueryUtilV2.addNode(parent, RDS_Constants.NODE_DBSNAPSHOT);
		QueryUtilV2.addNode(n, RDS_Constants.NODE_ALLOCATEDSTORAGE, o.getAllocatedStorage());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_AVAILABILITYZONE, o.getAvailabilityZone());
		QueryUtilV2.addNode(n, RDS_Constants.LICENSEMODEL, o.getLicenseModel());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_DBINSTANCEIDENTIFIER,
				o.getDbInstanceIdentifier());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_DBSNAPSHOTIDENTIFIER,
				o.getDbSnapshotIdentifier());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_ENGINE, o.getEngine());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_ENGINEVERSION, o.getEngineVersion());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_INSTANCECREATETIME, o.getInstanceCreateTime());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_MASTERUSERNAME, o.getMasterUsername());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_PORT, o.getPort());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_SNAPSHOTCREATETIME, o.getSnapshotCreateTime());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_STATUS, o.getStatus());
	}
}
