package com.msi.rdsquery.helper;

import javax.annotation.Resource;

import org.slf4j.Logger;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.msi.tough.core.Appctx;

public class SecurityGroupsHelper {
    @Resource
    protected AmazonEC2Client computeClient = null;
    
    private static Logger logger = Appctx.getLogger
    		(SecurityGroupsHelper.class.getName());

    
    public SecurityGroup getEC2SecGroup(){
    	logger.debug("Getting security group");
    	DescribeSecurityGroupsResult result = computeClient.describeSecurityGroups();
    	return result.getSecurityGroups().get(0);
    }
}
