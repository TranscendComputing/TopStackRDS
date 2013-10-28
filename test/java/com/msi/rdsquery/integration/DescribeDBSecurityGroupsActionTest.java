package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.CreateDBSecurityGroupRequest;
import com.amazonaws.services.rds.model.DeleteDBSecurityGroupRequest;
import com.amazonaws.services.rds.model.DescribeDBSecurityGroupsRequest;
import com.msi.tough.core.Appctx;

public class DescribeDBSecurityGroupsActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DescribeDBSecurityGroupsActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "rds-descSecGp-1-" + baseName;

    @Before
    public void setup(){
        logger.info("Creating DBsecGroup to describe");
        final CreateDBSecurityGroupRequest request = new CreateDBSecurityGroupRequest();
        request.withDBSecurityGroupDescription("SecGroup to describe");
        request.withDBSecurityGroupName(name1);
        getRdsClient().createDBSecurityGroup(request);
    };

    @After
    public void teardown() throws Exception{
        logger.info("Deleting DBSecGroup described");
        final DeleteDBSecurityGroupRequest delRequest = new DeleteDBSecurityGroupRequest();
        delRequest.withDBSecurityGroupName(name1);
        getRdsClient().deleteDBSecurityGroup(delRequest);
    }

    @Test
    public void testDescribeDBSecurityGroupsNoArgs() throws Exception {
        logger.info("Describing all security groups");
        final DescribeDBSecurityGroupsRequest request = new DescribeDBSecurityGroupsRequest();
        getRdsClient().describeDBSecurityGroups(request);
    }

    @Test (expected = AmazonServiceException.class)
    public void testDescribeDBSecurityGroupsInvalidParameters() throws Exception {
        final DescribeDBSecurityGroupsRequest request = new DescribeDBSecurityGroupsRequest();
        request.withDBSecurityGroupName("NonExistentName");
        getRdsClient().describeDBSecurityGroups(request);
    }


    @Test
    public void testGoodDescribeDBSecurityGroups() throws Exception {
        logger.info("Describing SecurityGroup with name = " + name1);
        final DescribeDBSecurityGroupsRequest request = new DescribeDBSecurityGroupsRequest();
        request.withDBSecurityGroupName(name1);
        getRdsClient().describeDBSecurityGroups(request);
    }
}

