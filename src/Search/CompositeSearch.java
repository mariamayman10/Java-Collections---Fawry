package Search;

import PhotoManagementSystem.Photo;
import PhotoManagementSystem.PhotoIndex;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CompositeSearch implements SearchStrategy{
    private final List<SearchStrategy> strategies;
    private final SearchMode searchMode;
    public CompositeSearch(List<SearchStrategy> strategies, SearchMode searchMode){
        this.strategies = strategies;
        this.searchMode = searchMode;
    }
    @Override
    public List<Photo> search(PhotoIndex photoIndex, int photosLength) {
        if(searchMode == SearchMode.OR){
            return orSearch(photoIndex, photosLength);
        }
        else if(searchMode == SearchMode.AND){
            return andSearch(photoIndex, photosLength);
        }
        return null;
    }

    private List<Photo> orSearch(PhotoIndex photoIndex, int photosLength){
        Set<Photo> result = new LinkedHashSet<>();
        for (SearchStrategy s : strategies) result.addAll(s.search(photoIndex, photosLength));
        return List.copyOf(result);
    }
    private List<Photo> andSearch(PhotoIndex photoIndex, int photosLength){
        Set<Photo> result = new LinkedHashSet<>(strategies.getFirst().search(photoIndex, photosLength));
        for (int i = 1; i < strategies.size(); i++) {
            Set<Photo> list = new LinkedHashSet<>(strategies.get(i).search(photoIndex, photosLength));
            result.retainAll(list);
        }
        return List.copyOf(result);
    }
}
