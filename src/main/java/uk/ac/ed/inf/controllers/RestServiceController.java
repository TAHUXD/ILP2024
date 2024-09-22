package uk.ac.ed.inf.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.inf.models.*;

@RestController
public class RestServiceController {
    // 1. /uuid (GET)
    @GetMapping("/uuid")
    public String getUuid() {
        return "s2172881";
    }

    // 2. /distanceTo (POST)
    @PostMapping("/distanceTo")
    public ResponseEntity<Double> distanceTo(@RequestBody Position positions) {
        if (positions == null || positions.getPosition1() == null || positions.getPosition2() == null) {
            return ResponseEntity.badRequest().build();
        }

        double distance = calculateDistance(positions.getPosition1(), positions.getPosition2());
        return ResponseEntity.ok(distance);
    }

    // 3. /isCloseTo (POST)
    @PostMapping("/isCloseTo")
    public ResponseEntity<Boolean> isCloseTo(@RequestBody Position positions) {
        if (positions == null || positions.getPosition1() == null || positions.getPosition2() == null) {
            return ResponseEntity.badRequest().build();
        }

        double distance = calculateDistance(positions.getPosition1(), positions.getPosition2());
        boolean isClose = distance < 0.00015;
        return ResponseEntity.ok(isClose);
    }

    // 4. /nextPosition (POST)
    @PostMapping("/nextPosition")
    public ResponseEntity<LngLat> nextPosition(@RequestBody NextPositionRequest request) {
        if (request == null || request.getStart() == null) {
            return ResponseEntity.badRequest().build();
        }

        LngLat nextPos = calculateNextPosition(request.getStart(), request.getAngle());
        return ResponseEntity.ok(nextPos);
    }

    // 5. /isInRegion (POST)
    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@RequestBody IsInRegionRequest request) {
        if (request == null || request.getPosition() == null || request.getRegion() == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean isInside = isPointInPolygon(request.getPosition(), request.getRegion().getVertices());
        return ResponseEntity.ok(isInside);
    }

    // Helper Methods
    private double calculateDistance(LngLat p1, LngLat p2) {
        double dx = p1.getLng() - p2.getLng();
        double dy = p1.getLat() - p2.getLat();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private LngLat calculateNextPosition(LngLat start, double angle) {
        // Implement the calculation
        double distance = 0.00015; // Assuming a fixed movement distance
        double rad = Math.toRadians(angle);
        double newLng = start.getLng() + distance * Math.cos(rad);
        double newLat = start.getLat() + distance * Math.sin(rad);

        LngLat nextPos = new LngLat();
        nextPos.setLng(newLng);
        nextPos.setLat(newLat);
        return nextPos;
    }

    private boolean isPointInPolygon(LngLat point, List<LngLat> vertices) {
        // Implement the ray casting algorithm
        int intersectCount = 0;
        for (int i = 0; i < vertices.size(); i++) {
            LngLat v1 = vertices.get(i);
            LngLat v2 = vertices.get((i + 1) % vertices.size());

            if (rayIntersectsSegment(point, v1, v2)) {
                intersectCount++;
            }
        }
        return (intersectCount % 2) == 1;
    }

    private boolean rayIntersectsSegment(LngLat p, LngLat a, LngLat b) {
        if (a.getLat() > b.getLat()) {
            LngLat temp = a;
            a = b;
            b = temp;
        }
        if (p.getLat() == a.getLat() || p.getLat() == b.getLat()) {
            p = new LngLat(p.getLng(), p.getLat() + 0.00000001);
        }
        if (p.getLat() < a.getLat() || p.getLat() > b.getLat() || p.getLng() >= Math.max(a.getLng(), b.getLng())) {
            return false;
        }
        if (p.getLng() < Math.min(a.getLng(), b.getLng())) {
            return true;
        }

        double red = (p.getLat() - a.getLat()) / (p.getLng() - a.getLng());
        double blue = (b.getLat() - a.getLat()) / (b.getLng() - a.getLng());
        return red >= blue;
    }
}
