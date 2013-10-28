package com.amazonaws.services.rds.model.transform;

import java.util.List;

import com.transcend.rds.message.DescribeDBInstancesActionMessage.DescribeDBInstancesActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBInstance;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class DescribeDBInstancesActionResultMarshaller implements
		Marshaller<String, DescribeDBInstancesActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final DescribeDBInstancesActionResultMessage input)
			 {

		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_DESCRIBEDBINSTANCESRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_DESCRIBEDBINSTANCESRESULT);

		final XMLNode nl = QueryUtilV2
				.addNode(nr, RDS_Constants.NODE_DBINSTANCES);

		final List<DBInstance> dbInstances = input.getDbInstancesList();
		if (dbInstances != null) {
			for (final DBInstance inst : dbInstances) {
				RDSQueryUtilV2.marshalDBInstance(nl, inst);
			}
		}

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
