package Search;

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
    public Set<String> search(PhotoIndex photoIndex, int maxSize) {
        Map<String, Set<String>> tagIndex = photoIndex.getTagIndex();
        if(mode == SearchMode.AND) return andSearch(tagIndex, tags.size());
        else if(mode == SearchMode.OR) return orSearch(tagIndex, maxSize);
        else throw new IllegalArgumentException("Invalid search mode");
    }

    private Set<String> orSearch(Map<String, Set<String>> tagIndex, int maxSize){
        Set<String> result = new HashSet<>(maxSize);
        for (String tag: tags){
            Set<String> list = tagIndex.get(tag);
            if(list != null)
                result.addAll(list);
        }
        return Set.copyOf(result);
    }
    private Set<String> andSearch(Map<String, Set<String>> tagIndex, int maxSize){
        List<Set<String>> listOfSets = new ArrayList<>(maxSize);
        for (String tag : tags) {
            Set<String> set = tagIndex.get(tag);
            if (set == null) return Set.of();
            listOfSets.add(set);
        }
        listOfSets.sort(Comparator.comparingInt(Set::size));
        Set<String> result = new HashSet<>(listOfSets.getFirst());
        for (int i = 1; i < listOfSets.size(); i++) {
            result.retainAll(listOfSets.get(i));
            if (result.isEmpty()) break;
        }
        return Set.copyOf(result);
    }
}
