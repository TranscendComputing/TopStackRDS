package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.CreateDBParameterGroupRequest;
import com.amazonaws.services.rds.model.DeleteDBParameterGroupRequest;
import com.amazonaws.services.rds.model.ModifyDBParameterGroupRequest;
import com.amazonaws.services.rds.model.Parameter;
import com.msi.tough.core.Appctx;

import java.util.LinkedList;


public class ModifyDBParameterGroupActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(ModifyDBParameterGroupActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "rds-modDbParGp-1-" + baseName;
    String parFamily = "MySQL5.5";

    @Test (expected = AmazonServiceException.class)
    public void testModifyDBParameterGroupNoArgs() throws Exception {
        final ModifyDBParameterGroupRequest request = new ModifyDBParameterGroupRequest();
        getRdsClient().modifyDBParameterGroup(request);
    }

    @Test (expected = AmazonServiceException.class)
    public void testModifyDBParameterGroupInvalidParameters() throws Exception {
        final ModifyDBParameterGroupRequest request = new ModifyDBParameterGroupRequest();
        request.withDBParameterGroupName("DNE");
        request.withParameters(new LinkedList<Parameter>());
        getRdsClient().modifyDBParameterGroup(request);
    }

    @Test
    public void testGoodModifyDBParameterGroup() throws Exception {
        logger.info("Creating DBParamGroup ");
        final CreateDBParameterGroupRequest request = new CreateDBParameterGroupRequest();
        request.setDBParameterGroupFamily(parFamily);
        request.setDBParameterGroupName(name1);
        request.setDescription("This param group was created as a test");
        getRdsClient().createDBParameterGroup(request);
        
        logger.info("Modifying dbParameter group " + name1);
        final ModifyDBParameterGroupRequest modRequest = new ModifyDBParameterGroupRequest();
        modRequest.withDBParameterGroupName(name1);
        Parameter param = new Parameter();
        param.setIsModifiable(true);
        param.setParameterValue("1");
        param.setParameterName("innodb_file_per_table");
        param.setApplyMethod("pending-reboot");
        LinkedList<Parameter> parList = new LinkedList<Parameter>();
        parList.add(param);
        modRequest.withParameters(parList);
        getRdsClient().modifyDBParameterGroup(modRequest);
        
        logger.info("Deleting DBParam Group " + name1);
        final DeleteDBParameterGroupRequest delRequest = new DeleteDBParameterGroupRequest();
        delRequest.withDBParameterGroupName(name1);
        getRdsClient().deleteDBParameterGroup(delRequest);
    }


}

