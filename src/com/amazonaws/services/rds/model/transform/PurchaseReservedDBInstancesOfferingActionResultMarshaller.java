package com.amazonaws.services.rds.model.transform;

import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;
import com.transcend.rds.message.PurchaseReservedDBInstancesOfferingActionMessage.PurchaseReservedDBInstancesOfferingActionResultMessage;

public class PurchaseReservedDBInstancesOfferingActionResultMarshaller implements
		Marshaller<String, PurchaseReservedDBInstancesOfferingActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final PurchaseReservedDBInstancesOfferingActionResultMessage input)
			 {

		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_PURCHASERESERVEDDBINSTANCESOFFERINGRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_PURCHASERESERVEDDBINSTANCESOFFERINGRESULT);

		marshalReservedDBInstance(nr, input);

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
	
	
	public static void marshalReservedDBInstance(XMLNode parent,
			PurchaseReservedDBInstancesOfferingActionResultMessage rdbInstance) {
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_CURRENCYCODE, rdbInstance.getCurrencyCode());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_DBINSTANCECLASS, rdbInstance.getDbInstanceClass());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_DBINSTANCECOUNT, rdbInstance.getDbInstanceCount());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_DURATION, rdbInstance.getDuration());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_FIXEDPRICE, rdbInstance.getFixedPrice());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_MULTIAZ, rdbInstance.getMultiAZ());
		
		// TODO: these exist in the latest SDK
		//QueryUtilV2.addNode(rdbisNode, RDS_Constants.NODE_OFFERINGTYPE, rdbi.getOfferingType());
		//RDSQueryUtilV2.marshallNode(rdbisNode, rdbi.getRecurringCharges);

		QueryUtilV2.addNode(parent, RDS_Constants.NODE_PRODUCTDESCRIPTION, rdbInstance.getProductDescription());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_RESERVEDDBINSTANCEID, rdbInstance.getReservedDBInstanceId());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_RESERVEDDBINSTANCESOFFERINGID, rdbInstance.getReservedDBInstancesOfferingId());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_STARTTIME, rdbInstance.getStartTime());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_STATE, rdbInstance.getState());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_USAGEPRICE, rdbInstance.getUsagePrice());
	}
}
