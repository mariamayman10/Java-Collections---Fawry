package PhotoManagementSystem;

import java.time.LocalDate;
import java.util.*;

public class PhotoIndex {
    private final Map<String, List<Photo>> tagIndex = new HashMap<>();
    private final Map<String, List<Photo>> stringLocationIndex = new HashMap<>();
    private final Map<String, List<Photo>> geoLocationIndex = new HashMap<>();
    private final TreeMap<LocalDate, List<Photo>> dateIndex = new TreeMap<>();
    private static final double CELL_SIZE = 0.1;

    public void registerPhoto(Photo photo){
        updateTagIndex(photo);
        updateDateIndex(photo);
        updateLocationIndex(photo);
    }
    public void addTag(Photo photo, String normalizedTag){
        tagIndex.computeIfAbsent(normalizedTag, p -> new ArrayList<>()).add(photo);
    }
    public Map<String, List<Photo>> getTagIndex() {
        return tagIndex;
    }
    public TreeMap<LocalDate, List<Photo>> getDateIndex() {
        return dateIndex;
    }
    public Map<String, List<Photo>> getStringLocationIndex() {
        return stringLocationIndex;
    }
    public Map<String, List<Photo>> getGeoLocationIndex() {
        return geoLocationIndex;
    }

    private void updateTagIndex(Photo photo){
        for(String tag: photo.getTags()){
            tagIndex.computeIfAbsent(tag.toLowerCase().trim(), p->new ArrayList<>()).add(photo);
        }
    }
    private void updateDateIndex(Photo photo){
        if(photo.getDate() != null){
            dateIndex.computeIfAbsent(photo.getDate(), p->new ArrayList<>()).add(photo);
        }
    }
    private void updateLocationIndex(Photo photo){
        if(photo.getLocationName() != null){
            stringLocationIndex.computeIfAbsent(photo.getLocationName().getLocation().toLowerCase().trim(), p->new ArrayList<>()).add(photo);
        }
        if(photo.getCoordinates() != null){
            int latBucket = (int) (photo.getCoordinates().getLatitude() / CELL_SIZE);
            int lngBucket = (int) (photo.getCoordinates().getLongitude() / CELL_SIZE);
            String key = latBucket + "_" + lngBucket;
            geoLocationIndex.computeIfAbsent(key, p->new ArrayList<>()).add(photo);
        }
    }
}
