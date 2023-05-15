package io.github.tf-govstack.registration.repositories;

import io.github.tf-govstack.registration.entity.IdentitySchema;
import io.github.tf-govstack.registration.entity.ProcessSpec;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessSpecRepository extends JpaRepository<ProcessSpec, String> {

    List<ProcessSpec> findAllByIdVersionAndIsActiveTrueOrderByOrderNumAsc(double idVersion);

    ProcessSpec findByIdAndIdVersionAndIsActiveTrue(String processId, double idVersion);
}
