package Search;
import PhotoManagementSystem.PhotoIndex;

import java.util.Set;

public interface SearchStrategy {
    Set<String> search(PhotoIndex photoIndex, int maxSize);
}
