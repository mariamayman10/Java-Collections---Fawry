package PhotoManagementSystem;

import Search.SearchStrategy;
import java.util.*;

public class PhotoManagementSystem {
    private final List<Photo> photos = new ArrayList<>();
    private final PhotoIndex photoIndex = new PhotoIndex();


    public List<Photo> search(SearchStrategy searchStrategy){
        if(searchStrategy == null) throw new IllegalArgumentException("Strategy must not be null.");
        return searchStrategy.search(photoIndex, photos.size());
    }

    public void uploadPhoto(Photo photo){
        if(photo == null) return;
        photos.add(photo);
        photoIndex.registerPhoto(photo);
    }
    public void updateTag(Photo photo, String tag){
        if(photo == null || tag.isBlank()) return;
        String normalizedTag = tag.toLowerCase().trim();
        if(!photo.containsTag(normalizedTag)){
            photo.addTag(normalizedTag);
            photoIndex.addTag(photo, normalizedTag);
        }
    }

}
