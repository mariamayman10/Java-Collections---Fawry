package PhotoManagementSystem;
import Location.LocationCoordinates;
import Location.LocationString;
import java.time.LocalDate;
import java.util.Set;

public class Photo {
    private final String id;
    private final String imgUrl;
    private final LocationCoordinates coordinates;
    private final LocationString locationName;
    private final LocalDate date;
    private final Set<String> tags;

    public Photo(String id, String imgUrl, LocalDate date, LocationCoordinates coordinates, Set<String> tags){
        this.id = id;
        this.imgUrl = imgUrl;
        this.coordinates = coordinates;
        this.locationName = null;
        this.date = date;
        this.tags = tags;
    }
    public Photo(String id, String imgUrl, LocalDate date, LocationString locationName, Set<String> tags){
        this.id = id;
        this.imgUrl = imgUrl;
        this.coordinates = null;
        this.locationName = locationName;
        this.date = date;
        this.tags = tags;
    }

    public void addTag(String newTag){
        tags.add(newTag);
    }

    public boolean containsTag(String tag){
        return tags.contains(tag);
    }

    public String getId() {
        return id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public LocationString getLocationName() {
        return locationName;
    }

    public LocationCoordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getDate() {
        return date;
    }

    public Set<String> getTags() {
        return tags;
    }
    @Override
    public String toString() {
        return "Photo{id='" + id + "', imgUrl='" + imgUrl + "'}";
    }
}
