package com.msi.rdsquery.integration;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.rds.AmazonRDSClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-rdsContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public abstract class AbstractBaseRdsTest {

	@Resource(name = "basicAWSCredentials")
	private AWSCredentials creds;

	@Autowired
	private AmazonRDSClient rdsClient;

    @Autowired
    private AmazonRDSClient rdsClientBadCreds;

	@Autowired
	private String defaultAvailabilityZone;
	
	@Autowired
	private String targetServer;

    public AWSCredentials getCreds() {
		return creds;
	}
	public void setCreds(AWSCredentials creds) {
		this.creds = creds;
	}
	public AmazonRDSClient getRdsClient() {
		return rdsClient;
	}

	public void setRdsClient(AmazonRDSClient rds) {
		this.rdsClient = rds;
	}

    public String getDefaultAvailabilityZone() {
        return defaultAvailabilityZone;
    }
    public void setDefaultAvailabilityZone(String defaultAvailabilityZone) {
        this.defaultAvailabilityZone = defaultAvailabilityZone;
    }
    /**
     * @return the rdsClientBadCreds
     */
    public AmazonRDSClient getRdsClientBadCreds() {
        return rdsClientBadCreds;
    }
    /**
     * @param rdsClientBadCreds the rdsClientBadCreds to set
     */
    public void setRdsClientBadCreds(AmazonRDSClient rdsClientBadCreds) {
        this.rdsClientBadCreds = rdsClientBadCreds;
    }

    public String getTargetServer(){
    	return this.targetServer;
    }
    
}
