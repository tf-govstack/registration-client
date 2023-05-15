package io.github.tf-govstack.registration.dao.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;

import io.github.tf-govstack.kernel.core.exception.ExceptionUtils;
import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.dao.DynamicFieldDAO;
import io.github.tf-govstack.registration.dto.mastersync.DynamicFieldValueDto;
import io.github.tf-govstack.registration.entity.DynamicField;
import io.github.tf-govstack.registration.repositories.DynamicFieldRepository;
import io.github.tf-govstack.registration.util.mastersync.MapperUtils;

@Repository
public class DynamicFieldDAOImpl implements DynamicFieldDAO {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(DynamicFieldDAOImpl.class);

	
	@Autowired
	private DynamicFieldRepository dynamicFieldRepository;
	
	@Override
	public DynamicField getDynamicField(String fieldName, String langCode) {
		LOGGER.debug("fetching the dynamic field >>> {} for langCode >>> {}" ,fieldName , langCode);

		return dynamicFieldRepository.findByIsActiveTrueAndNameAndLangCode(fieldName, langCode);
	}

	@Override
	public List<DynamicFieldValueDto> getDynamicFieldValues(String fieldName, String langCode) {
		
		LOGGER.debug("fetching the valueJSON ");
		
		DynamicField dynamicField = getDynamicField(fieldName, langCode);
		
		try {
			String valueJson = (dynamicField != null) ? dynamicField.getValueJson() : "[]" ;

			List<DynamicFieldValueDto> fields = MapperUtils.convertJSONStringToDto(valueJson == null ? "[]" : valueJson,
					new TypeReference<List<DynamicFieldValueDto>>() {});

			if(fields != null)
				fields.sort((DynamicFieldValueDto d1, DynamicFieldValueDto d2) -> d1.getCode().compareTo(d2.getCode()));

			return fields;
			
		} catch (IOException e) {
			LOGGER.error("Unable to parse value json for dynamic field: ", e);
		}
		return null;
	}

}
