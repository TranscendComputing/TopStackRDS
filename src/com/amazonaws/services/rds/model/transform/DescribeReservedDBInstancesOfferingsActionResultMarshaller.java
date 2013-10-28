package com.amazonaws.services.rds.model.transform;

import com.transcend.rds.message.DescribeReservedDBInstancesOfferingsActionMessage.DescribeReservedDBInstancesOfferingsActionResultMessage;
import com.transcend.rds.message.RDSMessage.ReservedDBInstancesOffering;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class DescribeReservedDBInstancesOfferingsActionResultMarshaller
		implements
		Marshaller<String, DescribeReservedDBInstancesOfferingsActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(
			final DescribeReservedDBInstancesOfferingsActionResultMessage input)
			 {


		// TODO: fill out the result as an XML document
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_DESCRIBERESERVEDDBINSTANCESOFFERINGSRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_DESCRIBERESERVEDDBINSTANCESOFFERINGSRESULT);

		QueryUtilV2.addNode(nr, RDS_Constants.NODE_MARKER, input.getMarker());

		QueryUtilV2.addNode(nr, RDS_Constants.NODE_RESERVEDDBINSTANCESOFFERINGS);
		for (final ReservedDBInstancesOffering o : input
				.getReservedDBInstancesOfferingsList()) {
			final XMLNode no = QueryUtilV2.addNode(nr,
					RDS_Constants.NODE_RESERVEDDBINSTANCESOFFERING);
			RDSQueryUtilV2.marshalReservedDBInstance(no, o);
		}

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
