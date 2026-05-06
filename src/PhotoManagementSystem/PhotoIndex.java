package PhotoManagementSystem;

import Location.LocationCoordinates;

import java.time.LocalDate;
import java.util.*;

public class PhotoIndex {
    private final Map<String, Set<String>> tagIndex = new HashMap<>();
    private final Map<String, Set<String>> stringLocationIndex = new HashMap<>();
    private final Map<LocationCoordinates, Set<String>> geoLocationIndex = new HashMap<>();
    private final TreeMap<LocalDate, Set<String>> dateIndex = new TreeMap<>();
    private static final double CELL_SIZE = 0.1;

    public void registerPhoto(Photo photo){
        updateTagIndex(photo);
        updateDateIndex(photo);
        updateLocationIndex(photo);
    }
    public void addTag(Photo photo, String normalizedTag){
        tagIndex.computeIfAbsent(normalizedTag, p -> new HashSet<>()).add(photo.getId());
    }
    public Map<String, Set<String>> getTagIndex() {
        return tagIndex;
    }
    public Map<String, Set<String>> getStringLocationIndex() {
        return stringLocationIndex;
    }
    public Map<LocationCoordinates, Set<String>> getGeoLocationIndex() {
        return geoLocationIndex;
    }
    public TreeMap<LocalDate, Set<String>> getDateIndex() {
        return dateIndex;
    }

    private void updateTagIndex(Photo photo){
        for(String tag: photo.getTags()){
            String normalizedTag = tag.toLowerCase().trim();
            tagIndex.computeIfAbsent(normalizedTag, p->new HashSet<>()).add(photo.getId());
        }
    }
    private void updateDateIndex(Photo photo){
        if(photo.getDate() != null){
            dateIndex.computeIfAbsent(photo.getDate(), p->new HashSet<>()).add(photo.getId());
        }
    }
    private void updateLocationIndex(Photo photo){
        if(photo.getLocationName() != null){
            String normalizedStringLocation = photo.getLocationName().getLocation().toLowerCase().trim();
            stringLocationIndex.computeIfAbsent(normalizedStringLocation, p->new HashSet<>()).add(photo.getId());
        }
        if(photo.getCoordinates() != null){
            geoLocationIndex.computeIfAbsent(photo.getCoordinates(), p -> new HashSet<>()).add(photo.getId());
        }
    }
}
