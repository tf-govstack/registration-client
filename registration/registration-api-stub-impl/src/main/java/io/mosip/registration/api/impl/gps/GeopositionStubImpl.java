package io.github.tf-govstack.registration.api.impl.gps;

import io.github.tf-govstack.registration.api.geoposition.GeoPositionService;
import io.github.tf-govstack.registration.api.geoposition.dto.GeoPosition;
import org.springframework.stereotype.Component;

@Component
public class GeopositionStubImpl implements GeoPositionService {

    @Override
    public GeoPosition getGeoPosition(GeoPosition geoPosition) {
        geoPosition.setLatitude(0);
        geoPosition.setLongitude(0);
        return geoPosition;
    }
}
