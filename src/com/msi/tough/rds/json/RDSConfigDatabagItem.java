package com.msi.tough.rds.json;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.msi.tough.core.converter.ToJson;


public class RDSConfigDatabagItem implements ToJson{


	private String Id;
	private String allcStg;
	private String mstrUsr;
	private String mstrPsswd;
	private boolean autoMnrVrsUpgrd;
	private String eng;
	private String engVrs;
	private String dbName;
	private String bckupRtnPrd;
	private String prfBckupWndw;
	private String prfMntcWndw;
	private String port;
	private String postWaitUrl;
	private String servletUrl;
	private String physicalId;
	private String stackId;
	private long acid;
	private String instanceType;
	private String restoring;

	public RDSConfigDatabagItem( String Id, String allcStg, String mstrUsr,
			String mstrPsswd, boolean autoMnrVrsUpgrd, String eng, String engVrs,
			String dbName, String bckupRtnPrd, String prfBckupWndw, String prfMntcWndw, 
			String port, String postWaitUrl, String servletUrl, String dbId, String stackId, 
			long acid, String instanceType, String restoring){
		
		this.Id = Id;
		this.allcStg = allcStg;
		this.mstrUsr = mstrUsr;
		this.mstrPsswd = mstrPsswd;
		this.autoMnrVrsUpgrd = autoMnrVrsUpgrd;
		this.eng = eng;
		this.engVrs = engVrs;
		this.dbName = dbName;
		this.bckupRtnPrd = bckupRtnPrd;
		this.prfMntcWndw = prfMntcWndw;
		this.prfBckupWndw = prfBckupWndw;
		this.port = port;		
		this.postWaitUrl = postWaitUrl;
		this.servletUrl = servletUrl;
		this.physicalId = dbId;
		this.stackId = stackId;
		this.acid = acid;
		this.instanceType = instanceType;
		if(restoring == null){
			restoring = "false"; 
		}
		this.restoring = restoring;
	}
	
	@JsonProperty("id")
	public String getId() { return Id ; }
	
	@JsonProperty("allcStg")
	public String getallcStg() { return allcStg ; }
	
	@JsonProperty("mstrUsr")
	public String getmstrUsr() { return mstrUsr; }

	@JsonProperty("mstrPsswd")
	public String getmstrPsswd() { return mstrPsswd; }

	@JsonProperty("autoMnrVrsUpgrd")
	public boolean getautoMnrVrsUpgrd() { return autoMnrVrsUpgrd; }
	
	@JsonProperty("eng")
	public String geteng() { return eng; }
	
	@JsonProperty("engVrs")
	public String getengVrs() { return engVrs; }
	
	@JsonProperty("dbName")
	public String getdbName() { return dbName; }
	
	@JsonProperty("prfMntcWndw")
	public String getprfMntcWndw() { return prfMntcWndw; }
	
	@JsonProperty("bckupRtnPrd")
	public String getbckupRtnPrd() { return bckupRtnPrd; }
	
	@JsonProperty("prfbckupWndw")
	public String getprfbckupWndw() { return prfBckupWndw; }
	
	@JsonProperty("port")
	public String getPort() {
		return port;
	}

	@JsonProperty("PostWaitUrl")
	public String getPostWaitUrl(){
		return this.postWaitUrl;
	}
	
	@JsonProperty("ServletUrl")
	public String getServletUrl(){
		return this.servletUrl;
	}
	
	@JsonProperty("PhysicalId")
	public String getPhysicalId(){
		return this.physicalId;
	}
	
	@JsonProperty("StackId")
	public String getStackId(){
		return this.stackId;
	}
	
	@JsonProperty("AcId")
	public long getAcid(){
		return this.acid;
	}
	
	@JsonProperty("InstanceType")
	public String getInstanceType(){
		return this.instanceType;
	}
	
	@JsonProperty("RestoredDBInstance")
	public String getRestoring(){
		return this.restoring;
	}
	
	public String toJson() throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper() ;
		return mapper.writeValueAsString(this);
	}
}
