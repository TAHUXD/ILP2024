package uk.ac.ed.inf;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import uk.ac.ed.inf.models.*;
import uk.ac.ed.inf.controllers.RestServiceController;

public class PathCalculationTest {

    @Test
    void testPathCalculationSimpleCase() {
        RestServiceController controller = new RestServiceController();
        LngLat start = new LngLat(-3.19128692150116, 55.9455351525177);
        LngLat end = new LngLat(-3.186874, 55.944494);

        List<NoFlyZone> noFlyZones = Collections.emptyList(); // no restrictions
        Region centralArea = createCentralArea();

        List<LngLat> path = controller.calculatePath(start, end, noFlyZones, centralArea);
        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertTrue(path.get(0).closeTo(start));
        assertTrue(path.get(path.size()-1).closeTo(end));
    }

    @Test
    void testPathNoSolution() {
        RestServiceController controller = new RestServiceController();
        LngLat start = new LngLat(-3.19128692150116, 55.9455351525177);
        LngLat end = new LngLat(-3.186874, 55.944494);

        List<NoFlyZone> noFlyZones = createBlockingNoFlyZones();
        Region centralArea = createCentralArea();

        List<LngLat> path = controller.calculatePath(start, end, noFlyZones, centralArea);
        assertNull(path); // No path found
    }

    // Helper methods
    private Region createCentralArea() {
        Region r = new Region();
        r.setName("central");
        r.setVertices(Arrays.asList(
                new LngLat(-3.192473, 55.946233),
                new LngLat(-3.192473, 55.942617),
                new LngLat(-3.184319, 55.942617),
                new LngLat(-3.184319, 55.946233),
                new LngLat(-3.192473, 55.946233)
        ));
        return r;
    }

    private List<NoFlyZone> createBlockingNoFlyZones() {
        NoFlyZone zone = new NoFlyZone();
        zone.setName("Block");
        zone.setVertices(Arrays.asList(
                new LngLat(-3.190, 55.945),
                new LngLat(-3.189, 55.945),
                new LngLat(-3.189, 55.944),
                new LngLat(-3.190, 55.944),
                new LngLat(-3.190, 55.945)
        ));
        return Arrays.asList(zone);
    }
}
