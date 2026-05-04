import Location.LocationCoordinates;
import Location.LocationString;
import PhotoManagementSystem.PhotoManagementSystem;
import PhotoManagementSystem.Photo;
import Search.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        PhotoManagementSystem manager = new PhotoManagementSystem();

        Photo p1 = new Photo("1", "pyramids.jpg", LocalDate.of(2023, 3, 10),
                new LocationString("Cairo"),
                Set.of("pyramids", "history", "egypt", "tourism"));

        Photo p2 = new Photo("2", "nile.jpg", LocalDate.of(2023, 4, 22),
                new LocationString("Cairo"),
                Set.of("nile", "river", "sunset", "egypt"));

        Photo p3 = new Photo("3", "mosque.jpg", LocalDate.of(2023, 4, 22),
                new LocationString("Cairo"),
                Set.of("mosque", "islamic", "architecture", "egypt"));

        Photo p4 = new Photo("4", "museum.jpg", LocalDate.of(2023, 5, 15),
                new LocationString("Cairo"),
                Set.of("museum", "antiquities", "egypt", "pharaohs"));
        Photo p5 = new Photo("5", "giza_pyramid_close.jpg", LocalDate.of(2023, 6, 1),
                new LocationCoordinates(31.1313, 29.9765),
                Set.of("pyramids", "giza", "ancient"));

        Photo p6 = new Photo("6", "downtown_cairo.jpg", LocalDate.of(2023, 6, 2),
                new LocationCoordinates(31.2357, 30.0444),
                Set.of("city", "cairo", "street", "egypt"));

        Photo p7 = new Photo("7", "alexandria_sea.jpg", LocalDate.of(2023, 6, 3),
                new LocationCoordinates(29.9187, 31.2001),
                Set.of("sea", "alexandria", "sunset"));

        manager.uploadPhoto(p1);
        manager.uploadPhoto(p2);
        manager.uploadPhoto(p3);
        manager.uploadPhoto(p4);
        manager.uploadPhoto(p5);
        manager.uploadPhoto(p6);
        manager.uploadPhoto(p7);

        manager.updateTag(p4, "pharaohs");

        // ......................................................................... //

        System.out.println("\n=== Search by single tag: egypt ===");
        manager.search(new SearchByTags("egypt"))
                .forEach(System.out::println);

        System.out.println("\n=== Search by OR tags: river, egypt ===");
        manager.search(new SearchByTags(Set.of("river", "egypt"), SearchMode.OR))
                .forEach(System.out::println);

        System.out.println("\n=== Search by AND tags: river, egypt ===");
        manager.search(new SearchByTags(Set.of("river", "egypt"), SearchMode.AND))
                .forEach(System.out::println);

        // ......................................................................... //

        System.out.println("\n=== Search by exact date: 22/4/2023 ===");
        manager.search(new SearchByDate(LocalDate.of(2023, 4, 22)))
                .forEach(System.out::println);

        System.out.println("\n=== Search by date TO: 22/4/2023 ===");
        manager.search(new SearchByDate(LocalDate.of(2023, 4, 22), DateSearchMode.TO))
                .forEach(System.out::println);

        System.out.println("\n=== Search by date FROM: 22/4/2023 ===");
        manager.search(new SearchByDate(LocalDate.of(2023, 4, 22), DateSearchMode.FROM))
                .forEach(System.out::println);

        System.out.println("\n=== Search by date RANGE: 22/4 -> 15/5 ===");
        manager.search(new SearchByDate(
                LocalDate.of(2023, 4, 22),
                LocalDate.of(2023, 5, 15)
        )).forEach(System.out::println);

        // ......................................................................... //

        System.out.println("\n=== Search by location string: cairo ===");
        manager.search(new SearchByLocation("cairo"))
                .forEach(System.out::println);

        System.out.println("\n=== Search by location string: air (substring test) ===");
        manager.search(new SearchByLocation("air"))
                .forEach(System.out::println);

        // ......................................................................... //

        System.out.println("\n=== Search by coordinates (small radius) ===");
        manager.search(new SearchByLocation(
                new LocationCoordinates(31.2357, 30.0444),
                10
        )).forEach(System.out::println);

        System.out.println("\n=== Search by coordinates (large radius) ===");
        manager.search(new SearchByLocation(
                new LocationCoordinates(31.2357, 30.0444),
                100
        )).forEach(System.out::println);

        System.out.println("\n=== Search by coordinates (no results case) ===");
        manager.search(new SearchByLocation(
                new LocationCoordinates(0, 0),
                5
        )).forEach(System.out::println);

        // ......................................................................... //

        System.out.println("\n=== Composite AND: egypt + date 22/4/2023 ===");

        manager.search(new CompositeSearch(
                List.of(
                        new SearchByTags("egypt"),
                        new SearchByDate(LocalDate.of(2023, 4, 22))
                ),
                SearchMode.AND
        )).forEach(System.out::println);

        System.out.println("\n=== Composite AND: cairo + pyramids ===");

        manager.search(new CompositeSearch(
                List.of(
                        new SearchByLocation("cairo"),
                        new SearchByTags("pyramids")
                ),
                SearchMode.AND
        )).forEach(System.out::println);

        System.out.println("\n=== Composite AND: geo radius + egypt tag ===");

        manager.search(new CompositeSearch(
                List.of(
                        new SearchByLocation(
                                new LocationCoordinates(31.2357, 30.0444),
                                100
                        ),
                        new SearchByTags("egypt")
                ),
                SearchMode.AND
        )).forEach(System.out::println);

        System.out.println("\n=== Composite OR: cairo OR museum OR river ===");

        manager.search(new CompositeSearch(
                List.of(
                        new SearchByLocation("cairo"),
                        new SearchByTags("museum"),
                        new SearchByTags("river")
                ),
                SearchMode.OR
        )).forEach(System.out::println);

        System.out.println("\n=== Composite AND: impossible combination ===");

        manager.search(new CompositeSearch(
                List.of(
                        new SearchByTags("river"),
                        new SearchByTags("space") // likely doesn't exist
                ),
                SearchMode.AND
        )).forEach(System.out::println);
    }
}