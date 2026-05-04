package Search;

import Location.LocationCoordinates;
import PhotoManagementSystem.Photo;
import PhotoManagementSystem.PhotoIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchByLocation implements SearchStrategy{
    private final String locationString;
    private final LocationCoordinates locationCoordinates;
    private final double radius;


    public SearchByLocation(String locationString){
        if(locationString == null || locationString.isBlank()) throw new IllegalArgumentException("Location string must be provided");
        this.locationString = locationString;
        this.locationCoordinates = null;
        this.radius = 0;
    }
    public SearchByLocation(LocationCoordinates locationCoordinates, double radius){
        if(locationCoordinates == null) throw new IllegalArgumentException("Location coordinates must be provided");
        if(radius == 0.0) throw new IllegalArgumentException("Must have radius greater than 0");
        this.locationString = null;
        this.locationCoordinates = locationCoordinates;
        this.radius = radius;
    }


    @Override
    public List<Photo> search(PhotoIndex photoIndex, int photosLength) {
        if(locationString != null){
            Map<String, List<Photo>> stringLocationIndex = photoIndex.getStringLocationIndex();
            return searchByString(stringLocationIndex, photosLength);
        }
        if(locationCoordinates != null){
            Map<String, List<Photo>> geoLocationIndex = photoIndex.getGeoLocationIndex();
            return searchByCoordinates(geoLocationIndex, photosLength);
        }
        return List.of();
    }
    private List<Photo> searchByString(Map<String, List<Photo>> stringLocationIndex, int photosLength){
        String normalizedLocationString = locationString.toLowerCase().trim();
        List<Photo> result = stringLocationIndex.get(normalizedLocationString);
        if(result != null) return result;
        result = new ArrayList<>(photosLength);
        for (Map.Entry<String, List<Photo>> entry : stringLocationIndex.entrySet()) {
            if (entry.getKey().contains(normalizedLocationString)) {
                result.addAll(entry.getValue());
            }
        }
        return List.copyOf(result);
    }
    private List<Photo> searchByCoordinates(Map<String, List<Photo>> geoLocationIndex, int photosLength){
        List<Photo> result = new ArrayList<>(photosLength);
        for (List<Photo> list : geoLocationIndex.values()) {
            for (Photo p : list) {
                LocationCoordinates loc = p.getCoordinates();
                if (loc != null && distanceKm(loc, locationCoordinates) <= radius) {
                    result.add(p);
                }
            }
        }

        return List.copyOf(result);
    }
    private double distanceKm(LocationCoordinates a, LocationCoordinates b) {
        double R = 6371;

        double lat1 = Math.toRadians(a.getLatitude());
        double lon1 = Math.toRadians(a.getLongitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double lon2 = Math.toRadians(b.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));

        return R * c;
    }
}
