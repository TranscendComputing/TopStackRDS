package com.amazonaws.services.rds.model.transform;

import com.transcend.rds.message.DescribeOrderableDBInstanceOptionsActionMessage.DescribeOrderableDBInstanceOptionsActionResultMessage;
import com.transcend.rds.message.RDSMessage.OrderableDBInstanceOption;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class DescribeOrderableDBInstanceOptionsActionResultMarshaller
		implements
		Marshaller<String, DescribeOrderableDBInstanceOptionsActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(
			final DescribeOrderableDBInstanceOptionsActionResultMessage input)
			 {

		// TODO: fill out the result as an XML document
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_DESCRIBEORDERABLEDBINSTANCEOPTIONSRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_DESCRIBEORDERABLEDBINSTANCEOPTIONSRESULT);

		QueryUtilV2.addNode(nr, RDS_Constants.NODE_MARKER, input.getMarker());

		final XMLNode nodeOptions = QueryUtilV2.addNode(nr,
				RDS_Constants.NODE_ORDERABLEDBINSTANCEOPTIONS);
		for (final OrderableDBInstanceOption o : input
				.getOrderableDBInstanceOptionsList()) {
			final XMLNode nodeOption = QueryUtilV2.addNode(nodeOptions,
					RDS_Constants.NODE_ORDERABLEDBINSTANCEOPTION);
			RDSQueryUtilV2.marshalAvailabilityZones(nodeOption,
					o.getAvailabilityZonesList());
			QueryUtilV2.addNode(nodeOption, RDS_Constants.NODE_DBINSTANCECLASS,
					o.getDbInstanceClass());
			QueryUtilV2.addNode(nodeOption, RDS_Constants.NODE_ENGINE,
					o.getEngine());
			QueryUtilV2.addNode(nodeOption, RDS_Constants.NODE_ENGINEVERSION,
					o.getEngineVersion());
			QueryUtilV2.addNode(nodeOption, RDS_Constants.NODE_LICENSEMODEL,
					o.getLicenseModel());
			QueryUtilV2.addNode(nodeOption, RDS_Constants.NODE_MULTIAZCAPABLE,
					o.getMultiAZCapable());
			QueryUtilV2.addNode(nodeOption,
					RDS_Constants.NODE_READREPLICACAPABLE,
					o.getReadReplicaCapable());

			// TODO: update aws sdk so these are available
			// QueryUtilV2.addNode(nodeOption, RDS_Constants.NODE_VPCCAPABLE,
			// o.getVpcCapable();
			// QueryUtilV2.addNode(nodeOption,
			// RDS_Constants.NODE_VPCMULTIAZCAPABLE, o.getVpcMultiAZCapable();
			// QueryUtilV2.addNode(nodeOption,
			// RDS_Constants.NODE_VPCREADREPLICACAPABLE,
			// o.getVpcReadReplicaCapable();
		}
		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
