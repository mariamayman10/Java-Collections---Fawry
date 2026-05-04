package Search;

import PhotoManagementSystem.Photo;
import PhotoManagementSystem.PhotoIndex;

import java.util.*;

public class SearchByTags implements SearchStrategy{
    private final Set<String> tags;
    private final SearchMode mode;

    public SearchByTags(String tag){
        tags = new HashSet<>();
        tags.add(tag);
        mode = SearchMode.AND;
    }
    public SearchByTags(Set<String> tags, SearchMode mode){
        if (tags == null || tags.isEmpty())
            throw new IllegalArgumentException("Tag set must not be empty.");
        if(mode == null)throw new IllegalArgumentException("You must set a search mode");
        this.tags = tags;
        this.mode = mode;
    }
    @Override
    public List<Photo> search(PhotoIndex photoIndex, int photosLength) {
        Map<String, List<Photo>> tagIndex = photoIndex.getTagIndex();
        if(mode == SearchMode.AND) return andSearch(tagIndex, photosLength);
        else if(mode == SearchMode.OR) return orSearch(tagIndex, photosLength);
        else throw new IllegalArgumentException("Invalid search mode");
    }

    private List<Photo> orSearch(Map<String, List<Photo>> tagIndex, int photosLength){
        Set<Photo> result = new HashSet<>(photosLength);
        for (String tag: tags){
            List<Photo> list = tagIndex.get(tag);
            if(list != null)
                result.addAll(list);
        }
        return List.copyOf(result);
    }
    private List<Photo> andSearch(Map<String, List<Photo>> tagIndex, int photosLength){
        List<List<Photo>> lists = new ArrayList<>();
        for (String tag : tags) {
            List<Photo> list = tagIndex.get(tag);
            if (list == null) return List.of();
            lists.add(list);
        }
        lists.sort(Comparator.comparingInt(List::size));
        Set<Photo> result = new HashSet<>(lists.getFirst());
        for (int i = 1; i < lists.size(); i++) {
            result.retainAll(lists.get(i));
            if (result.isEmpty()) break;
        }
        return List.copyOf(result);
    }
}
