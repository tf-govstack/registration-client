package io.github.tf-govstack.registration.dao;

import java.util.List;

import io.github.tf-govstack.registration.dto.AuthorizationDTO;

/**
 * This DAO class will fetch the screen ids of each screens that needs to be 
 * displayed for a particular user based on that user's role.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface ScreenAuthorizationDAO {

	/**
	 * This method will fetch the screen ids of each screens that needs to be 
	 * displayed for a particular user based on that user's role.
	 * 
	 * @param roleCode
	 *            - Role codes of a particular user
	 * 
	 * @return AuthorizationDTO {@link AuthorizationDTO} object contains the screen 
	 * 			details of that particular user
	 */
	AuthorizationDTO getScreenAuthorizationDetails(List<String> roleCode);
}
