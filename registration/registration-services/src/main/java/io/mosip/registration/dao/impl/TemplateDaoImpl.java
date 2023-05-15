package io.github.tf-govstack.registration.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.tf-govstack.registration.dao.TemplateDao;
import io.github.tf-govstack.registration.entity.Template;
import io.github.tf-govstack.registration.repositories.TemplateRepository;

/**
 * DaoImpl for calling the respective template repositories and getting data from database
 * 
 * @author Himaja Dhanyamraju
 */
@Repository
public class TemplateDaoImpl implements TemplateDao{

	@Autowired
	private TemplateRepository<Template> templateRepository;
	

	
	public List<Template> getAllTemplates(String templateTypeCode){
		return templateRepository.findByIsActiveTrueAndTemplateTypeCode(templateTypeCode);
	}
	

	public List<Template> getAllTemplates(String templateTypeCode, String langCode){
		return templateRepository.findAllByIsActiveTrueAndTemplateTypeCodeLikeAndLangCodeOrderByIdAsc(templateTypeCode, langCode);
	}
}
