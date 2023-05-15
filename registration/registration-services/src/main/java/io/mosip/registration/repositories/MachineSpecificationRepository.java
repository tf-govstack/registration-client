package io.github.tf-govstack.registration.repositories;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.RegMachineSpec;
import io.github.tf-govstack.registration.entity.id.RegMachineSpecId;

/**
 * Repository to perform CRUD operations on MachineSpecification.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

public interface MachineSpecificationRepository extends BaseRepository<RegMachineSpec, String> {

}
