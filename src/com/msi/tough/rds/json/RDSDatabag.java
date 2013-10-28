package com.msi.tough.rds.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;

import com.msi.tough.core.JsonUtil;


public class RDSDatabag {

		
		private RDSConfigDatabagItem config ;
		private RDSParameterGroupDatabagItem parameterGroup;
		
		public RDSDatabag( 
			RDSConfigDatabagItem config, 
			RDSParameterGroupDatabagItem parameterGroup ){
			this.config = config ;
			this.parameterGroup = parameterGroup ;
		}

		@JsonProperty("request_parameters")
		public RDSConfigDatabagItem getConfig(){
			return config ;
		}

		@JsonProperty("db_parameter_group")
		public RDSParameterGroupDatabagItem getParameterGroup(){
			return parameterGroup ;
		}

		public String toJson() throws JsonGenerationException, JsonMappingException, IOException {
			return JsonUtil.toJsonString(this);
		}

	}

