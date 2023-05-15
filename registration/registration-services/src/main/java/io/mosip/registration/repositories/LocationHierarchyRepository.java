package io.github.tf-govstack.registration.repositories;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.LocationHierarchy;
import io.github.tf-govstack.registration.entity.id.LocationHierarchyId;

import java.util.List;

public interface LocationHierarchyRepository extends BaseRepository<LocationHierarchy, LocationHierarchyId> {

    List<LocationHierarchy> findAllByIsActiveTrueAndLangCode(String langCode);
}
