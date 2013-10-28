package com.amazonaws.services.rds.model.transform;

import com.transcend.rds.message.DescribeDBSnapshotsActionMessage.DescribeDBSnapshotsActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBSnapshot;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class DescribeDBSnapshotsActionResultMarshaller implements
		Marshaller<String, DescribeDBSnapshotsActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final DescribeDBSnapshotsActionResultMessage input)
			 {
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_DESCRIBEDBSNAPSHOTSRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nodeActionResultMessage = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_DESCRIBEDBSNAPSHOTSRESULT);

		final XMLNode nodeSnapshots = QueryUtilV2.addNode(nodeActionResultMessage,
				RDS_Constants.NODE_DBSNAPSHOTS);

		if (input.getDbSnapshotsCount() > 0) {
			for (final DBSnapshot s : input.getDbSnapshotsList()) {
				final XMLNode nodeSnapshot = QueryUtilV2.addNode(nodeSnapshots,
						RDS_Constants.NODE_DBSNAPSHOT);
				QueryUtilV2.addNode(nodeSnapshot,
						RDS_Constants.NODE_ALLOCATEDSTORAGE,
						s.getAllocatedStorage());
				QueryUtilV2.addNode(nodeSnapshot,
						RDS_Constants.NODE_AVAILABILITYZONE,
						s.getAvailabilityZone());
				QueryUtilV2.addNode(nodeSnapshot,
						RDS_Constants.NODE_DBINSTANCEIDENTIFIER,
						s.getDbInstanceIdentifier());
				QueryUtilV2.addNode(nodeSnapshot,
						RDS_Constants.NODE_DBSNAPSHOTIDENTIFIER,
						s.getDbSnapshotIdentifier());
				QueryUtilV2.addNode(nodeSnapshot, RDS_Constants.NODE_ENGINE,
						s.getEngine());
				QueryUtilV2.addNode(nodeSnapshot,
						RDS_Constants.NODE_ENGINEVERSION, s.getEngineVersion());
				QueryUtilV2.addNode(nodeSnapshot,
						RDS_Constants.NODE_INSTANCECREATETIME,
						s.getInstanceCreateTime());
				QueryUtilV2.addNode(nodeSnapshot,
						RDS_Constants.NODE_LICENSEMODEL, s.getLicenseModel());
				QueryUtilV2.addNode(nodeSnapshot,
						RDS_Constants.NODE_MASTERUSERNAME,
						s.getMasterUsername());
				QueryUtilV2.addNode(nodeSnapshot, RDS_Constants.NODE_PORT,
						s.getPort());
				QueryUtilV2.addNode(nodeSnapshot,
						RDS_Constants.NODE_SNAPSHOTCREATETIME,
						s.getSnapshotCreateTime());
				QueryUtilV2.addNode(nodeSnapshot, RDS_Constants.NODE_STATUS,
						s.getStatus());
				// TODO: update aws sdk so this is available
				// QueryUtilV2.addNode(nodeSnapshots,
				// RDS_Constants.NODE_SNAPSHOTTYPE, s.getSnapshotType());

				// TODO: update aws sdk so this is available
				// QueryUtilV2.addNode(nodeSnapshots, RDS_Constants.NODE_VPCID,
				// s.getVpcId();
			}
		}

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
