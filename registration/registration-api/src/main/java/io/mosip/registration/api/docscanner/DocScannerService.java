package io.github.tf-govstack.registration.api.docscanner;

import io.github.tf-govstack.registration.api.docscanner.dto.DocScanDevice;

import java.awt.image.BufferedImage;
import java.util.List;

public interface DocScannerService {

    String getServiceName();

    BufferedImage scan(DocScanDevice docScanDevice);

    List<DocScanDevice> getConnectedDevices();

    void stop(DocScanDevice docScanDevice);
}
