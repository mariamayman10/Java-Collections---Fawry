package Search;

import Location.LocationCoordinates;
import PhotoManagementSystem.PhotoIndex;

import java.util.*;

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
        if(radius <= 0) throw new IllegalArgumentException("Must have radius greater than 0");
        this.locationString = null;
        this.locationCoordinates = locationCoordinates;
        this.radius = radius;
    }


    @Override
    public Set<String> search(PhotoIndex photoIndex, int maxSize) {
        if(locationString != null){
            Map<String, Set<String>> stringLocationIndex = photoIndex.getStringLocationIndex();
            return searchByString(stringLocationIndex, maxSize);
        }
        if(locationCoordinates != null){
            Map<LocationCoordinates, Set<String>> geoLocationIndex = photoIndex.getGeoLocationIndex();
            return searchByCoordinates(geoLocationIndex, maxSize);
        }
        return Set.of();
    }
    private Set<String> searchByString(Map<String, Set<String>> stringLocationIndex, int maxSize){
        String normalizedLocationString = locationString.toLowerCase().trim();
        Set<String> result = stringLocationIndex.get(normalizedLocationString);
        if(result != null) return result;
        result = new LinkedHashSet<>(maxSize);
        for (Map.Entry<String, Set<String>> entry : stringLocationIndex.entrySet()) {
            if (entry.getKey().contains(normalizedLocationString)) {
                result.addAll(entry.getValue());
            }
        }
        return Set.copyOf(result);
    }
    private Set<String> searchByCoordinates(Map<LocationCoordinates, Set<String>> geoLocationIndex, int maxSize){
        Set<String> result = new HashSet<>(maxSize);
        for (Map.Entry<LocationCoordinates, Set<String>> entry: geoLocationIndex.entrySet()) {
            if(distanceKm(locationCoordinates, entry.getKey()) <= radius){
                result.addAll(entry.getValue());
            }
        }
        return Set.copyOf(result);
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
