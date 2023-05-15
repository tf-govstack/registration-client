package io.github.tf-govstack.registration.repositories;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.FileSignature;

import java.util.Optional;

public interface FileSignatureRepository extends BaseRepository<FileSignature, String> {

    Optional<FileSignature> findByFileName(String fileName);
}
