package io.github.tf-govstack.registration.service.sync;

import io.github.tf-govstack.registration.dto.ResponseDTO;
import io.github.tf-govstack.registration.exception.RegBaseCheckedException;

import java.net.SocketTimeoutException;

public interface CertificateSyncService {

    public ResponseDTO getCACertificates(String triggerPoint) throws RegBaseCheckedException;
}
