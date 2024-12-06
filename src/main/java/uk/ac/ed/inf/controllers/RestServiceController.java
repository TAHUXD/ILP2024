package uk.ac.ed.inf.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.inf.models.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.stream.Collectors;

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

    // 6. /validateOrder (POST)
    @PostMapping("/validateOrder")
    public ResponseEntity<OrderValidationResult> validateOrder(@RequestBody Order order) {
        OrderValidationResult result = performOrderValidation(order);
        return ResponseEntity.ok(result);
    }

    // 7. /calcDeliveryPath (POST)
    @PostMapping("/calcDeliveryPath")
    public ResponseEntity<List<LngLat>> calcDeliveryPath(@RequestBody Order order) {
        // Validate the order using the performOrderValidation method
        OrderValidationResult validationResult = performOrderValidation(order);
        if (validationResult.getOrderStatus() != OrderStatus.VALID) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Get restaurant location
        Restaurant restaurant = getRestaurantForOrder(order);
        if (restaurant == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        LngLat restaurantLocation = restaurant.getLocation();

        // Get Appleton Tower location
        LngLat appletonTower = new LngLat(-3.186874, 55.944494);

        // Fetch no-fly zones and central area
        List<NoFlyZone> noFlyZones = getNoFlyZones();
        Region centralArea = getCentralArea();

        // Calculate path
        List<LngLat> path = calculatePath(restaurantLocation, appletonTower, noFlyZones, centralArea);

        if (path == null || path.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(path);
    }

    // Helper method to retrieve restaurant data
    private RestTemplate restTemplate = new RestTemplate();

    private List<Restaurant> getRestaurants() {
        String url = "https://ilp-rest-2024.azurewebsites.net/restaurants";
        try {
            Restaurant[] restaurants = restTemplate.getForObject(url, Restaurant[].class);
            return Arrays.asList(restaurants);
        } catch (Exception e) {
            // Handle exceptions (e.g., logging)
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Helper method to retrieve no-fly zones data
    private List<NoFlyZone> getNoFlyZones() {
        String url = "https://ilp-rest-2024.azurewebsites.net/noFlyZones";
        try {
            NoFlyZone[] zones = restTemplate.getForObject(url, NoFlyZone[].class);
            return Arrays.asList(zones);
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Helper method to retrieve central area data
    private Region getCentralArea() {
        String url = "https://ilp-rest-2024.azurewebsites.net/centralArea";
        try {
            Region centralArea = restTemplate.getForObject(url, Region.class);
            return centralArea;
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to validate credit card number using Luhn Algorithm
    private boolean isValidCreditCardNumber(String ccNumber) {
        if (ccNumber == null || !ccNumber.matches("\\d{16}")) {
            return false;
        }
        int sum = 0;
        boolean alternate = false;
        for (int i = ccNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(ccNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    // Helper method to validate expiry date
    private boolean isValidExpiryDate(String expiryDate) {
        if (expiryDate == null || !expiryDate.matches("\\d{2}/\\d{2}")) {
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth expiry = YearMonth.parse(expiryDate, formatter);
            YearMonth now = YearMonth.now();
            return expiry.isAfter(now) || expiry.equals(now);
        } catch (Exception e) {
            return false;
        }
    }

    // Helper Methods
    private double calculateDistance(LngLat p1, LngLat p2) {
        double dx = p1.getLng() - p2.getLng();
        double dy = p1.getLat() - p2.getLat();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private LngLat calculateNextPosition(LngLat start, double angle) {
        double distance = 0.00015;
        double rad = Math.toRadians(angle);
        double newLng = start.getLng() + distance * Math.cos(rad);
        double newLat = start.getLat() + distance * Math.sin(rad);

        LngLat nextPos = new LngLat();
        nextPos.setLng(newLng);
        nextPos.setLat(newLat);
        return nextPos;
    }

    private boolean isPointInPolygon(LngLat point, List<LngLat> vertices) {
        int intersectCount = 0;
        for (int i = 0; i < vertices.size(); i++) {
            LngLat v1 = vertices.get(i);
            LngLat v2 = vertices.get((i + 1) % vertices.size());

            if (intersectsSegment(point, v1, v2)) {
                intersectCount++;
            }
        }
        return (intersectCount % 2) == 1;
    }

    private boolean intersectsSegment(LngLat p, LngLat a, LngLat b) {
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

    // Helper method to validate the order internally
    private OrderValidationResult performOrderValidation(Order order) {
        OrderValidationResult result = new OrderValidationResult();
        result.setOrderStatus(OrderStatus.VALID);
        result.setOrderValidationCode(OrderValidationCode.NO_ERROR);

        // Check if pizzasInOrder is null or empty
        if (order.getPizzasInOrder() == null || order.getPizzasInOrder().isEmpty()) {
            result.setOrderStatus(OrderStatus.INVALID);
            result.setOrderValidationCode(OrderValidationCode.EMPTY_ORDER);
            return result;
        }

        // Check max pizza count
        if (order.getPizzasInOrder().size() > 4) {
            result.setOrderStatus(OrderStatus.INVALID);
            result.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            return result;
        }

        // Get restaurants data
        List<Restaurant> restaurants = getRestaurants();

        // Check that all pizzas are defined and get possible restaurants
        Set<String> possibleRestaurants = null;
        for (Pizza orderedPizza : order.getPizzasInOrder()) {
            boolean pizzaDefined = false;
            Set<String> pizzaRestaurants = new HashSet<>();
            for (Restaurant restaurant : restaurants) {
                for (Pizza menuPizza : restaurant.getMenu()) {
                    if (orderedPizza.getName().equals(menuPizza.getName())) {
                        pizzaDefined = true;
                        pizzaRestaurants.add(restaurant.getName());
                        break;
                    }
                }
            }
            if (!pizzaDefined) {
                result.setOrderStatus(OrderStatus.INVALID);
                result.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
                return result;
            }
            if (possibleRestaurants == null) {
                possibleRestaurants = pizzaRestaurants;
            } else {
                possibleRestaurants.retainAll(pizzaRestaurants);
            }
        }

        // Check that pizzas are from the same restaurant
        if (possibleRestaurants == null || possibleRestaurants.isEmpty()) {
            result.setOrderStatus(OrderStatus.INVALID);
            result.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
            return result;
        }

        // Get the restaurant
        String restaurantName = possibleRestaurants.iterator().next();
        Restaurant restaurant = null;
        for (Restaurant r : restaurants) {
            if (r.getName().equals(restaurantName)) {
                restaurant = r;
                break;
            }
        }

        // Check restaurant is open on order date
        String orderDayOfWeek = order.getOrderDate().getDayOfWeek().name();
        if (!restaurant.getOpeningDays().contains(orderDayOfWeek)) {
            result.setOrderStatus(OrderStatus.INVALID);
            result.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            return result;
        }

        // Check pizza prices
        for (Pizza orderedPizza : order.getPizzasInOrder()) {
            boolean priceValid = false;
            for (Pizza menuPizza : restaurant.getMenu()) {
                if (orderedPizza.getName().equals(menuPizza.getName())) {
                    if (orderedPizza.getPriceInPence() == menuPizza.getPriceInPence()) {
                        priceValid = true;
                    }
                    break;
                }
            }
            if (!priceValid) {
                result.setOrderStatus(OrderStatus.INVALID);
                result.setOrderValidationCode(OrderValidationCode.PRICE_FOR_PIZZA_INVALID);
                return result;
            }
        }

        // Check total price (including £1 delivery charge)
        int totalPrice = order.getPizzasInOrder().stream()
                .mapToInt(Pizza::getPriceInPence).sum() + 100;
        if (order.getPriceTotalInPence() != totalPrice) {
            result.setOrderStatus(OrderStatus.INVALID);
            result.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            return result;
        }

        // Check credit card number
        String ccNumber = order.getCreditCardInformation().getCreditCardNumber();
        if (!isValidCreditCardNumber(ccNumber)) {
            result.setOrderStatus(OrderStatus.INVALID);
            result.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            return result;
        }

        // Check credit card expiry date
        String expiryDate = order.getCreditCardInformation().getCreditCardExpiry();
        if (!isValidExpiryDate(expiryDate)) {
            result.setOrderStatus(OrderStatus.INVALID);
            result.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return result;
        }

        // Check CVV
        String cvv = order.getCreditCardInformation().getCvv();
        if (!cvv.matches("\\d{3}")) {
            result.setOrderStatus(OrderStatus.INVALID);
            result.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            return result;
        }

        // All checks passed
        return result;
    }

    // Helper method to get the restaurant for the order
    private Restaurant getRestaurantForOrder(Order order) {
        List<Restaurant> restaurants = getRestaurants();
        Set<String> possibleRestaurants = null;
        for (Pizza orderedPizza : order.getPizzasInOrder()) {
            Set<String> pizzaRestaurants = new HashSet<>();
            for (Restaurant restaurant : restaurants) {
                for (Pizza menuPizza : restaurant.getMenu()) {
                    if (orderedPizza.getName().equals(menuPizza.getName())) {
                        pizzaRestaurants.add(restaurant.getName());
                        break;
                    }
                }
            }
            if (possibleRestaurants == null) {
                possibleRestaurants = pizzaRestaurants;
            } else {
                possibleRestaurants.retainAll(pizzaRestaurants);
            }
        }
        if (possibleRestaurants == null || possibleRestaurants.isEmpty()) {
            return null;
        }
        String restaurantName = possibleRestaurants.iterator().next();
        for (Restaurant r : restaurants) {
            if (r.getName().equals(restaurantName)) {
                return r;
            }
        }
        return null;
    }

    // Helper method to calculate the path
    private List<LngLat> calculatePath(LngLat start, LngLat end, List<NoFlyZone> noFlyZones, Region centralArea) {
        // Implement a pathfinding algorithm that considers:
        // - Drone movement constraints (16 compass directions)
        // - No-fly zones
        // - Central area constraints

        List<LngLat> path = new ArrayList<>();
        LngLat currentPosition = start;

        // Hover at the restaurant to pick up the order
        path.add(currentPosition); // Hover move

        boolean enteredCentralArea = false;

        while (!currentPosition.closeTo(end)) {
            // Get possible moves
            List<Double> possibleAngles = Arrays.asList(
                    0.0, 22.5, 45.0, 67.5, 90.0, 112.5, 135.0, 157.5,
                    180.0, 202.5, 225.0, 247.5, 270.0, 292.5, 315.0, 337.5
            );

            final LngLat positionForLambda = currentPosition;

            // Sort angles based on how close they bring us to the end point
            possibleAngles.sort(Comparator.comparingDouble(angle -> {
                LngLat nextPos = positionForLambda.nextPosition(angle);
                return nextPos.distanceTo(end);
            }));

            boolean moved = false;
            for (Double angle : possibleAngles) {
                LngLat potentialPosition = currentPosition.nextPosition(angle);
                if (isValidMove(currentPosition, potentialPosition, noFlyZones, centralArea)) {
                    path.add(potentialPosition);
                    currentPosition = potentialPosition;
                    moved = true;

                    // Check if we have entered the central area
                    if (!enteredCentralArea && isPointInPolygon(currentPosition, centralArea.getVertices())) {
                        enteredCentralArea = true;
                    }

                    break;
                }
            }
            if (!moved) {
                // No valid moves available, cannot reach the destination
                return null;
            }
        }

        // Hover at Appleton Tower to deliver the order
        path.add(currentPosition); // Hover move

        return path;
    }

    // Helper method to check if a move is valid
    private boolean isValidMove(LngLat from, LngLat to, List<NoFlyZone> noFlyZones, Region centralArea) {
        // Check if the move crosses any no-fly zones
        for (NoFlyZone zone : noFlyZones) {
            if (lineIntersectsPolygon(from, to, zone.getVertices())) {
                return false;
            }
        }
        // Check if we are inside the central area after entering it
        boolean fromInCentral = isPointInPolygon(from, centralArea.getVertices());
        boolean toInCentral = isPointInPolygon(to, centralArea.getVertices());

        if (fromInCentral && !toInCentral) {
            // We are leaving the central area after entering it, which is not allowed
            return false;
        }

        return true;
    }

    // Helper method to check if a line segment intersects with any polygon
    private boolean lineIntersectsPolygon(LngLat p1, LngLat p2, List<LngLat> polygon) {
        for (int i = 0; i < polygon.size(); i++) {
            LngLat a = polygon.get(i);
            LngLat b = polygon.get((i + 1) % polygon.size());
            if (linesIntersect(p1, p2, a, b)) {
                return true;
            }
        }
        return false;
    }

    // Helper method to check if two line segments intersect
    private boolean linesIntersect(LngLat p1, LngLat p2, LngLat q1, LngLat q2) {
        // Implement the algorithm for line segment intersection
        // Using the cross product method
        double s1_x = p2.getLng() - p1.getLng();
        double s1_y = p2.getLat() - p1.getLat();
        double s2_x = q2.getLng() - q1.getLng();
        double s2_y = q2.getLat() - q1.getLat();

        double s = (-s1_y * (p1.getLng() - q1.getLng()) + s1_x * (p1.getLat() - q1.getLat())) / (-s2_x * s1_y + s1_x * s2_y);
        double t = ( s2_x * (p1.getLat() - q1.getLat()) - s2_y * (p1.getLng() - q1.getLng())) / (-s2_x * s1_y + s1_x * s2_y);

        return (s >= 0 && s <= 1 && t >= 0 && t <= 1);
    }


}
