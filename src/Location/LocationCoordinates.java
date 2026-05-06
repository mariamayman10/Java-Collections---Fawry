package Location;

import java.util.Objects;

public class LocationCoordinates implements LocationValue {
    private final double longitude;
    private final double latitude;

    public LocationCoordinates(double latitude, double longitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() { return longitude; }
    public double getLatitude() { return latitude; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationCoordinates that)) return false;
        return latitude == that.latitude && longitude == that.longitude;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
