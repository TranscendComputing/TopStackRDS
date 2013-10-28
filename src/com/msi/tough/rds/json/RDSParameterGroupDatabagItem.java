package com.msi.tough.rds.json;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.msi.tough.cf.json.DatabagParameter;
import com.msi.tough.core.converter.ToJson;
import com.msi.tough.model.rds.RdsDbparameterGroup;
import com.msi.tough.model.rds.RdsParameter;
import com.mysql.jdbc.StringUtils;


public class RDSParameterGroupDatabagItem implements ToJson {

	private final String Id;
	private LinkedHashMap<String, Object> parameters = null;

	public RDSParameterGroupDatabagItem(final String Id,
			final RdsDbparameterGroup parameterGroup) {
		this.Id = Id;

		// Add regular parameters
		parameters = new LinkedHashMap<String, Object>();
		for (final RdsParameter p : parameterGroup.getParameters()) {
			if (!StringUtils.isNullOrEmpty(p.getParameterValue())) {
				final DatabagParameter dbp = DatabagParameter.factory(
						p.getDataType(), p.getParameterValue(),
						p.getIsModifiable(), p.getApplyType());
				parameters.put(p.getParameterName(), dbp);
			}
		}
	}

	@JsonProperty("id")
	public String getId() {
		return Id;
	}

	@JsonProperty("Parameters")
	public LinkedHashMap<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(final LinkedHashMap<String, Object> parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toJson() throws JsonGenerationException,
			JsonMappingException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
}
