package Location;

public class LocationCoordinates implements LocationValue {
    private final double longitude;
    private final double latitude;

    public LocationCoordinates(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() { return longitude; }
    public double getLatitude() { return latitude; }
}
