package PhotoManagementSystem;

import Search.SearchStrategy;
import java.util.*;

public class PhotoManagementSystem {
    private final Map<String, Photo> photos = new HashMap<>();
    private final PhotoIndex photoIndex = new PhotoIndex();


    public void search(SearchStrategy searchStrategy){
        if(searchStrategy == null) throw new IllegalArgumentException("Strategy must not be null.");
        Set<String> searchResult = searchStrategy.search(photoIndex, photos.size());
        printPhotos(searchResult);
    }

    public void uploadPhoto(Photo photo){
        if(photo == null) return;
        photos.put(photo.getId(), photo);
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
    public void printPhotos(Set<String> photoIds){
        for(String id: photoIds){
            Photo photo = photos.get(id);
            System.out.println("Photo Id: " + id + " Photo name: " + photo.getImgUrl());
        }
    }

}
