package io.github.tf-govstack.registration.dao;

import java.util.List;

import io.github.tf-govstack.registration.entity.Location;

/**
 * This class is used to fetch all the locations present in the {@link Location} table.
 * 
 * @author Brahmananda Reddy
 *
 */
public interface LocationDAO {
	/**
	 * This method is used to fetch all the locations from {@link Location} table.
	 * 
	 * @return the list of locations
	 */

	List<Location> getLocations();

}
