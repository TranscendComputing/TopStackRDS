package com.amazonaws.services.rds.model.transform;

import com.transcend.rds.message.DescribeDBEngineVersionsActionMessage.DescribeDBEngineVersionsActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBEngineVersion;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class DescribeDBEngineVersionsActionResultMarshaller implements
		Marshaller<String, DescribeDBEngineVersionsActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(
			final DescribeDBEngineVersionsActionResultMessage input)
			 {
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_DESCRIBEDBENGINEVERSIONSRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);
		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_DBENGINEVERSIONSRESULT);

		final XMLNode nodeVersions = QueryUtilV2.addNode(nr,
				RDS_Constants.NODE_DBENGINEVERSIONS);

		for (final DBEngineVersion v : input.getDbEngineVersionsList()) {
			final XMLNode nodeV = QueryUtilV2.addNode(nodeVersions,
					RDS_Constants.NODE_DBENGINEVERSION);

			QueryUtilV2.addNode(nodeV, RDS_Constants.NODE_DBPARAMETERGROUPFAMILY,
					v.getDbParameterGroupFamily());
			QueryUtilV2.addNode(nodeV, RDS_Constants.NODE_ENGINE, v.getEngine());
			QueryUtilV2.addNode(nodeV, RDS_Constants.NODE_ENGINEVERSION,
					v.getEngineVersion());
		}

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
