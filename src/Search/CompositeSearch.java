package Search;

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
    public Set<String> search(PhotoIndex photoIndex, int maxSize) {
        if(searchMode == SearchMode.OR){
            return orSearch(photoIndex, maxSize);
        }
        else if(searchMode == SearchMode.AND){
            return andSearch(photoIndex, maxSize);
        }
        throw new IllegalArgumentException("Invalid search mode");
    }

    private Set<String> orSearch(PhotoIndex photoIndex, int maxSize){
        Set<String> result = new LinkedHashSet<>();
        for (SearchStrategy s : strategies) result.addAll(s.search(photoIndex, maxSize));
        return Set.copyOf(result);
    }
    private Set<String> andSearch(PhotoIndex photoIndex, int maxSize){
        Set<String> result = new LinkedHashSet<>(strategies.getFirst().search(photoIndex, maxSize));
        for (int i = 1; i < strategies.size(); i++) {
            Set<String> list = new LinkedHashSet<>(strategies.get(i).search(photoIndex, maxSize));
            result.retainAll(list);
        }
        return Set.copyOf(result);
    }
}
