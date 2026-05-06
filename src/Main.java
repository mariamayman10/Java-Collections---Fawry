import Location.LocationCoordinates;
import Location.LocationString;
import PhotoManagementSystem.Photo;
import PhotoManagementSystem.PhotoManagementSystem;
import Search.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    // ── helpers ──────────────────────────────────────────────────────────────

    private static Set<String> tags(String... values) {
        return new HashSet<>(List.of(values));
    }

    private static void section(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }

    // ── main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {

        PhotoManagementSystem manager = new PhotoManagementSystem();

        // ── Dataset ──────────────────────────────────────────────────────────
        //
        //  p1  pyramids.jpg      Cairo (string)        2023-03-10  pyramids, history, egypt, tourism
        //  p2  nile.jpg          Cairo (string)        2023-04-22  nile, river, sunset, egypt
        //  p3  mosque.jpg        Cairo (string)        2023-04-22  mosque, islamic, architecture, egypt
        //  p4  museum.jpg        Cairo (string)        2023-05-15  museum, antiquities, egypt, pharaohs
        //  p5  giza_pyramid.jpg  (31.13, 29.98)        2023-06-01  pyramids, giza, ancient
        //  p6  downtown.jpg      (31.24, 30.04)        2023-06-02  city, cairo, street, egypt
        //  p7  alexandria.jpg    Alexandria (string)   2023-06-03  sea, sunset
        //  p8  desert.jpg        Cairo (string)        2023-03-10  desert, sinai, egypt  (same day as p1)

        Photo p1 = new Photo("1", "pyramids.jpg",
                LocalDate.of(2023, 3, 10),
                new LocationString("Cairo"),
                tags("pyramids", "history", "egypt", "tourism"));

        Photo p2 = new Photo("2", "nile.jpg",
                LocalDate.of(2023, 4, 22),
                new LocationString("Cairo"),
                tags("nile", "river", "sunset", "egypt"));

        Photo p3 = new Photo("3", "mosque.jpg",
                LocalDate.of(2023, 4, 22),
                new LocationString("Cairo"),
                tags("mosque", "islamic", "architecture", "egypt"));

        Photo p4 = new Photo("4", "museum.jpg",
                LocalDate.of(2023, 5, 15),
                new LocationString("Cairo"),
                tags("museum", "antiquities", "egypt", "pharaohs"));

        Photo p5 = new Photo("5", "giza_pyramid.jpg",
                LocalDate.of(2023, 6, 1),
                new LocationCoordinates(31.1313, 29.9765),
                tags("pyramids", "giza", "ancient"));

        Photo p6 = new Photo("6", "downtown_cairo.jpg",
                LocalDate.of(2023, 6, 2),
                new LocationCoordinates(31.2357, 30.0444),
                tags("city", "cairo", "street", "egypt"));

        Photo p7 = new Photo("7", "alexandria_sea.jpg",
                LocalDate.of(2023, 6, 3),
                new LocationString("alexandria"),
                tags("sea", "alexandria", "sunset"));

        // Extra photo: same date as p1, same string-location "Cairo", used for
        // edge-case coverage (multiple results on exact date, AND date+location).
        Photo p8 = new Photo("8", "desert_sinai.jpg",
                LocalDate.of(2023, 3, 10),
                new LocationString("Cairo"),
                tags("desert", "sinai", "egypt"));

        for (Photo p : List.of(p1, p2, p3, p4, p5, p6, p7, p8)) {
            manager.uploadPhoto(p);
        }

        // ── Tag updates ───────────────────────────────────────────────────────
        // updateTag on a photo whose tag-set is already mutable (fixed bug above).
        manager.updateTag(p4, "artifact");          // new tag
        manager.updateTag(p4, "pharaohs");          // duplicate — should be ignored silently
        manager.updateTag(p1, "UNESCO");            // mixed-case normalised internally

        // ── 1. Tag searches ───────────────────────────────────────────────────

        section("1a  Single tag — 'egypt'  (expected: p1 p2 p3 p4 p6 p8)");
        manager.search(new SearchByTags("egypt"));

        section("1b  Single tag — 'sunset'  (expected: p2 p7)");
        manager.search(new SearchByTags("sunset"));

        section("1c  OR  {pyramid, giza}  (expected: p1 p5)");
        manager.search(new SearchByTags(Set.of("pyramids", "giza"), SearchMode.OR));

        section("1d  AND {river, egypt}  (expected: p2 only — nile photo)");
        manager.search(new SearchByTags(Set.of("river", "egypt"), SearchMode.AND));

        section("1e  AND {sunset, egypt}  (expected: p2 — sunset AND egypt)");
        manager.search(new SearchByTags(Set.of("sunset", "egypt"), SearchMode.AND));

        section("1f  AND {pyramids, giza}  (expected: p5 — giza coords photo)");
        manager.search(new SearchByTags(Set.of("pyramids", "giza"), SearchMode.AND));

        section("1g  Tag added via updateTag — 'artifact'  (expected: p4)");
        manager.search(new SearchByTags("artifact"));

        section("1h  Tag added via updateTag — 'unesco' (case-normalised)  (expected: p1)");
        manager.search(new SearchByTags("unesco"));

        section("1i  Non-existent tag — 'space'  (expected: empty)");
        manager.search(new SearchByTags("space"));

        // ── 2. Date searches ──────────────────────────────────────────────────

        section("2a  Exact date 2023-03-10  (expected: p1 p8 — two photos same day)");
        manager.search(new SearchByDate(LocalDate.of(2023, 3, 10)));

        section("2b  Exact date 2023-04-22  (expected: p2 p3)");
        manager.search(new SearchByDate(LocalDate.of(2023, 4, 22)));

        section("2c  TO 2023-03-10  (expected: p1 p8 — only earliest date)");
        manager.search(new SearchByDate(LocalDate.of(2023, 3, 10), DateSearchMode.TO));

        section("2d  TO 2023-04-22  (expected: p1 p2 p3 p8)");
        manager.search(new SearchByDate(LocalDate.of(2023, 4, 22), DateSearchMode.TO));

        section("2e  FROM 2023-06-01  (expected: p5 p6 p7)");
        manager.search(new SearchByDate(LocalDate.of(2023, 6, 1), DateSearchMode.FROM));

        section("2f  RANGE 2023-04-22 → 2023-05-15  (expected: p2 p3 p4)");
        manager.search(new SearchByDate(
                LocalDate.of(2023, 4, 22),
                LocalDate.of(2023, 5, 15)));

        section("2g  RANGE single-day boundary 2023-05-15 → 2023-05-15  (expected: p4)");
        manager.search(new SearchByDate(
                LocalDate.of(2023, 5, 15),
                LocalDate.of(2023, 5, 15)));

        section("2h  Date with no photos — 2024-01-01  (expected: empty)");
        manager.search(new SearchByDate(LocalDate.of(2024, 1, 1)));

        // ── 3. Location string searches ───────────────────────────────────────

        section("3a  Exact string 'cairo'  (expected: p1 p2 p3 p4 p8)");
        manager.search(new SearchByLocation("cairo"));

        section("3b  Substring 'air'  (expected: p1 p2 p3 p4 p8 — matches 'cairo')");
        manager.search(new SearchByLocation("air"));

        section("3c  Case-insensitive 'CAIRO'  (expected: p1 p2 p3 p4 p8)");
        manager.search(new SearchByLocation("CAIRO"));

        section("3d  No-match string 'paris'  (expected: empty)");
        manager.search(new SearchByLocation("paris"));

        // ── 4. Geo-coordinate searches ────────────────────────────────────────

        section("4a  Radius 5 km around downtown Cairo (31.24, 30.04)  (expected: p6)");
        manager.search(new SearchByLocation(
                new LocationCoordinates(31.2357, 30.0444), 5));

        section("4b  Radius 50 km around downtown Cairo  (expected: p5 p6 — Giza + downtown)");
        manager.search(new SearchByLocation(
                new LocationCoordinates(31.2357, 30.0444), 50));

        section("4c  Radius 250 km around downtown Cairo  (expected: p5 p6 p7 — includes Alex)");
        manager.search(new SearchByLocation(
                new LocationCoordinates(31.2357, 30.0444), 250));

        section("4d  Origin (0,0) radius 5 km — no photos nearby  (expected: empty)");
        manager.search(new SearchByLocation(
                new LocationCoordinates(0.0, 0.0), 5));

         // ── 5. Composite AND searches ─────────────────────────────────────────

        section("5a  AND: tag 'egypt' + exact date 2023-04-22  (expected: p2 p3)");
        manager.search(new CompositeSearch(
                List.of(
                        new SearchByTags("egypt"),
                        new SearchByDate(LocalDate.of(2023, 4, 22))),
                SearchMode.AND));

        section("5b  AND: location 'cairo' + tag 'pyramids'  (expected: p1)");
        manager.search(new CompositeSearch(
                List.of(
                        new SearchByLocation("cairo"),
                        new SearchByTags("pyramids")),
                SearchMode.AND));

        section("5c  AND: geo 250 km + tag 'egypt'  (expected: p6 — only geo+egypt overlap)");
        manager.search(new CompositeSearch(
                List.of(
                        new SearchByLocation(new LocationCoordinates(31.2357, 30.0444), 250),
                        new SearchByTags("egypt")),
                SearchMode.AND));

        section("5d  AND: tag 'egypt' + date RANGE 2023-03 → 2023-04  (expected: p1 p2 p3 p8)");
        manager.search(new CompositeSearch(
                List.of(
                        new SearchByTags("egypt"),
                        new SearchByDate(
                                LocalDate.of(2023, 3, 1),
                                LocalDate.of(2023, 4, 30))),
                SearchMode.AND));

        section("5e  AND: impossible combination — tag 'river' AND tag 'space'  (expected: empty)");
        manager.search(new CompositeSearch(
                List.of(
                        new SearchByTags("river"),
                        new SearchByTags("space")),
                SearchMode.AND));

        // ── 6. Composite OR searches ──────────────────────────────────────────

        section("6a  OR: location 'cairo' OR tag 'museum' OR tag 'river'  (expected: p1-p4 p8)");
        manager.search(new CompositeSearch(
                List.of(
                        new SearchByLocation("cairo"),
                        new SearchByTags("museum"),
                        new SearchByTags("river")),
                SearchMode.OR));

        section("6b  OR: tag 'sunset' OR exact date 2023-06-01  (expected: p2 p5 p7)");
        manager.search(new CompositeSearch(
                List.of(
                        new SearchByTags("sunset"),
                        new SearchByDate(LocalDate.of(2023, 6, 1))),
                SearchMode.OR));

        section("6c  OR: geo 5 km downtown OR location string 'alex'  (expected: p6 p7)");
        manager.search(new CompositeSearch(
                List.of(
                        new SearchByLocation(new LocationCoordinates(31.2357, 30.0444), 5),
                        new SearchByLocation("alex")),
                SearchMode.OR));

        // ── 7. Nested composite ───────────────────────────────────────────────

        section("7a  Nested: (egypt AND 2023-04-22) OR (pyramids AND giza)");
        //  Inner 1: egypt + 22-Apr  → p2 p3
        //  Inner 2: pyramids + giza → p5
        //  OR of both              → p2 p3 p5
        manager.search(new CompositeSearch(
                List.of(
                        new CompositeSearch(
                                List.of(
                                        new SearchByTags("egypt"),
                                        new SearchByDate(LocalDate.of(2023, 4, 22))),
                                SearchMode.AND),
                        new CompositeSearch(
                                List.of(
                                        new SearchByTags("pyramids"),
                                        new SearchByTags("giza")),
                                SearchMode.AND)),
                SearchMode.OR));

        section("7b  Nested: (cairo string OR alexandria) AND (sunset OR ancient)");
        //  Inner 1: cairo OR alex → p1 p2 p3 p4 p7 p8
        //  Inner 2: sunset OR ancient → p2 p5 p7
        //  AND of both              → p2 p7
        manager.search(new CompositeSearch(
                List.of(
                        new CompositeSearch(
                                List.of(
                                        new SearchByLocation("cairo"),
                                        new SearchByLocation("alex")),
                                SearchMode.OR),
                        new CompositeSearch(
                                List.of(
                                        new SearchByTags("sunset"),
                                        new SearchByTags("ancient")),
                                SearchMode.OR)),
                SearchMode.AND));
    }
}