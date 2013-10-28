package com.amazonaws.services.rds.model.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.ModifyDBParameterGroupActionMessage.ModifyDBParameterGroupActionRequestMessage;
import com.transcend.rds.message.RDSMessage.Parameter;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.utils.RDSQueryFaults;

public class ModifyDBParameterGroupActionRequestUnmarshaller implements
    Unmarshaller<ModifyDBParameterGroupActionRequestMessage, Map<String, String[]>>
{
    private static Logger logger = Appctx
        .getLogger(ModifyDBParameterGroupActionRequestUnmarshaller.class.getName());

    private static ModifyDBParameterGroupActionRequestUnmarshaller instance;

    public static ModifyDBParameterGroupActionRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new ModifyDBParameterGroupActionRequestUnmarshaller();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    @Override
    public ModifyDBParameterGroupActionRequestMessage unmarshall(Map<String, String[]> in) 
    {
    	
        final ModifyDBParameterGroupActionRequestMessage.Builder req =  ModifyDBParameterGroupActionRequestMessage.newBuilder();

        logger.debug("Unmarshalling (inbound) ModifyDBParameterGroupActionRequestMessage");
        String groupName = QueryUtilV2.getString(in, "DBParameterGroupName");
        if(groupName == null || "".equals(groupName)){
        	throw RDSQueryFaults.MissingParameter("DBPameterGroupName must be supplied for ModifyDBParameterGroup ActionRequestMessage.");
        }
        req.setDbParameterGroupName(groupName);
        
        Collection<Parameter> params =  new LinkedList<Parameter>();
        
        int index = 1;
        boolean more = true;
        while(more){
        	String paramName = QueryUtilV2.getString(in, "Parameters.Parameter." + index + ".ParameterName");
        	String paramValue = QueryUtilV2.getString(in, "Parameters.Parameter." + index + ".ParameterValue");
        	String applyMethod = QueryUtilV2.getString(in, "Parameters.Parameter." + index + ".ApplyMethod");
        	if(!"".equals(paramName) && !"".equals(paramValue) && !"".equals(applyMethod)){
        		if(!applyMethod.equals("immediate") && !applyMethod.equals("pending-reboot")){
        			throw RDSQueryFaults.InvalidParameterValue("ApplyMethod has to be \"immediate\" or \"pending-reboot\".");
        		}
        		logger.debug("Paramter found: " + paramName);
        		params.add(Parameter.newBuilder().setParameterName(paramName).setParameterValue(paramValue).setApplyMethod(applyMethod).buildPartial());
        		++index;
        	}
        	else if((paramName == null && paramValue == null && applyMethod == null) ||
        			("".equals(paramName) && "".equals(paramValue) && "".equals(applyMethod))){
        		more = false;
        	}
        	else{
        		more = false;
        		throw RDSQueryFaults.InvalidParameterCombination("ParameterName, ParameterValue, and ApplyMethod have to be given.");
        	}
        }
  
        if(params.size() > 0){
        	req.addAllParameters(new ArrayList(params));
        }
        else{
        	throw RDSQueryFaults.MissingParameter("At least one Parameters member has to be supplied for ModifyDBParameterGroup ActionRequestMessage.");
        }
        
        logger.debug("Unmarshalling has completed!");
        return req.buildPartial();
    }
}
