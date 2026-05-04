package Search;
import PhotoManagementSystem.Photo;
import PhotoManagementSystem.PhotoIndex;

import java.util.List;

public interface SearchStrategy {
    public List<Photo> search(PhotoIndex photoIndex, int photosLength);
}
