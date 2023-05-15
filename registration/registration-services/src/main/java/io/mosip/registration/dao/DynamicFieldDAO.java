package io.github.tf-govstack.registration.dao;

import java.util.List;

import io.github.tf-govstack.registration.dto.mastersync.DynamicFieldValueDto;
import io.github.tf-govstack.registration.entity.DynamicField;

public interface DynamicFieldDAO {

	DynamicField getDynamicField(String fieldName, String Langcode);
	
	List<DynamicFieldValueDto> getDynamicFieldValues(String fieldName, String Langcode);
}
