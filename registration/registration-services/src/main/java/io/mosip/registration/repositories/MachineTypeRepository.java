package io.github.tf-govstack.registration.repositories;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.MachineType;
import io.github.tf-govstack.registration.entity.id.CodeAndLanguageCodeID;

/**
 * Repository to perform CRUD operations on MachineType.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface MachineTypeRepository extends BaseRepository<MachineType, String> {

}
