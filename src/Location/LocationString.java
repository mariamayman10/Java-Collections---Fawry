package Location;

public class LocationString implements LocationValue{
    private final String location;
    public LocationString(String location){
        this.location = location;
    }
    public String getLocation() {
        return location;
    }
}
