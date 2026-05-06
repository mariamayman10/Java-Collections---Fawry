package Search;
import PhotoManagementSystem.Photo;
import PhotoManagementSystem.PhotoIndex;

import java.time.LocalDate;
import java.util.*;

public class SearchByDate implements SearchStrategy{
    private final LocalDate from;
    private final LocalDate to;
    private final DateSearchMode mode;

    public SearchByDate(LocalDate exactDate) {
        if(exactDate == null) throw new IllegalArgumentException("Must provide date");
        this.mode = DateSearchMode.EXACT;
        this.from = exactDate;
        this.to   = exactDate;
    }
    public SearchByDate(LocalDate date, DateSearchMode mode) {
        if (mode == DateSearchMode.RANGE)
            throw new IllegalArgumentException("Use SearchByDate(from, to) for RANGE.");
        if(date == null) throw new IllegalArgumentException("Must provide date");
        this.mode = mode;
        this.from = (mode == DateSearchMode.FROM) ? date : null;
        this.to   = (mode == DateSearchMode.TO)   ? date : null;
    }
    public SearchByDate(LocalDate from, LocalDate to) {
        if(from == null) throw new IllegalArgumentException("Must provide from date");
        if(to == null) throw new IllegalArgumentException("Must provide to date");
        if (from.isAfter(to))
            throw new IllegalArgumentException("'from' must not be after 'to'.");
        this.mode = DateSearchMode.RANGE;
        this.from = from;
        this.to   = to;
    }

    @Override
    public Set<String> search(PhotoIndex photoIndex, int maxSize) {
        TreeMap<LocalDate, Set<String>> dateIndex = photoIndex.getDateIndex();
        if(mode == DateSearchMode.EXACT) {
            Set<String> result = dateIndex.get(from);
            if (result == null) return Set.of();
            return Set.copyOf(result);
        }
        NavigableMap<LocalDate, Set<String>> result;
        if(mode == DateSearchMode.RANGE){
            result = dateIndex.subMap(from, true, to, true);
        }else if(mode == DateSearchMode.TO){
            result = dateIndex.headMap(to, true);
        }else if(mode == DateSearchMode.FROM){
            result = dateIndex.tailMap(from, true);
        }else {
            throw new IllegalArgumentException("Invalid date search mode");
        }
        List<String> photos = new ArrayList<>(maxSize);
        for (Set<String> set : result.values()) {
            photos.addAll(set);
        }
        return Set.copyOf(photos);
    }
}
