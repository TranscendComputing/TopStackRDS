package com.amazonaws.services.rds.model.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.services.rds.model.Parameter;
import com.transcend.rds.message.ResetDBParameterGroupActionMessage.ResetDBParameterGroupActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.utils.RDSQueryFaults;

public class ResetDBParameterGroupActionRequestUnmarshaller implements
    Unmarshaller<ResetDBParameterGroupActionRequestMessage, Map<String, String[]>>
{
    private static Logger logger = Appctx
        .getLogger(ResetDBParameterGroupActionRequestUnmarshaller.class.getName());

    private static ResetDBParameterGroupActionRequestUnmarshaller instance;

    public static ResetDBParameterGroupActionRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new ResetDBParameterGroupActionRequestUnmarshaller();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    @Override
    public ResetDBParameterGroupActionRequestMessage unmarshall(Map<String, String[]> in)
        
    {
        final ResetDBParameterGroupActionRequestMessage.Builder req =  ResetDBParameterGroupActionRequestMessage.newBuilder();
        req.setDbParameterGroupName(QueryUtilV2.getString(in, "DBParameterGroupName"));
        req.setResetAllParameters(QueryUtilV2.getBoolean(in, "ResetAllParameters", true));
        
        Collection<Parameter> params =  new LinkedList<Parameter>();
        int i = 1;
        boolean cont = true;
        while(cont){
        	String paramName = QueryUtilV2.getString(in, "Parameters.Parameter." + i + ".ParameterName");
        	String applyMethod = QueryUtilV2.getString(in, "Parameters.Parameter." + i + ".ApplyMethod");
        	if(paramName != null && !("".equals(paramName)) && applyMethod != null && !("".equals(applyMethod))){
        		if(!applyMethod.equals("immediate") && !applyMethod.equals("pending-reboot")){
        			throw RDSQueryFaults.InvalidParameterValue("ApplyMethod has to be \"immediate\" or \"pending-reboot\".");
        		}
        		params.add(new Parameter().withParameterName(paramName).withApplyMethod(applyMethod));
        		++i;
        	}
        	else if((paramName != null && applyMethod == null) || (paramName == null && applyMethod != null)){
        		cont = false;
        		throw RDSQueryFaults.InvalidParameterCombination();
        	}
        	else{
        		cont = false;
        	}
        }
        if(params.size() > 0){
        	req.addAllParameters(new ArrayList(params));
        }
        return req.buildPartial();
    }
}
