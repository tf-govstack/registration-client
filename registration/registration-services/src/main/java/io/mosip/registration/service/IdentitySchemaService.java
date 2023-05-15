package io.github.tf-govstack.registration.service;


import java.util.List;

import io.github.tf-govstack.registration.dto.schema.ProcessSpecDto;
import io.github.tf-govstack.registration.dto.schema.SettingsSchema;
import io.github.tf-govstack.registration.dto.schema.UiFieldDTO;
import io.github.tf-govstack.registration.dto.schema.SchemaDto;
import io.github.tf-govstack.registration.exception.RegBaseCheckedException;

public interface IdentitySchemaService {
	
	public Double getLatestEffectiveSchemaVersion() throws RegBaseCheckedException;
		
	//public List<UiSchemaDTO> getLatestEffectiveUISchema() throws RegBaseCheckedException;
	
	public String getLatestEffectiveIDSchema() throws RegBaseCheckedException;
	
	//public List<UiSchemaDTO> getUISchema(double idVersion) throws RegBaseCheckedException;
	
	public String getIDSchema(double idVersion) throws RegBaseCheckedException;
	
	public SchemaDto getIdentitySchema(double idVersion) throws RegBaseCheckedException;

	public List<SettingsSchema> getSettingsSchema(double idVersion) throws RegBaseCheckedException;

	public ProcessSpecDto getProcessSpecDto(String processId, double idVersion) throws RegBaseCheckedException;

	public List<UiFieldDTO> getAllFieldSpec(String processId, double idVersion) throws RegBaseCheckedException;

}
