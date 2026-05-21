/**
 * Strategy Pattern (Behavioral)
 * Part of Smart City Infrastructure Simulation
 * Defines the routing algorithms for dispatching waste collection vehicles.
 */

// Strategy Interface
public interface RoutingStrategy {
    RouteInfo calculateRoute(SmartBin bin);
}

// Result object to hold advanced route details
record RouteInfo(String description, int estimatedTimeMs) {
}

// Concrete Strategy 1: Emergency / Fastest Routing (Theme A Requirement)
class FastestRoute implements RoutingStrategy {
    @Override
    public RouteInfo calculateRoute(SmartBin bin) {
        int baseTime = 2000; // 2 seconds base simulation time
        boolean heavy = CityTrafficSystem.getInstance().isHeavyTraffic();
        int finalTime = (int) (baseTime * CityTrafficSystem.getInstance().getTrafficMultiplier());
        
        String desc = heavy ? "FASTEST ROUTING: Rerouting emergency response around HEAVY traffic..." 
                            : "FASTEST ROUTING: Proceeding via clear highways.";
        return new RouteInfo(desc, finalTime);
    }
}

// Concrete Strategy 2: Eco-Friendly / Utility Routing
class EcoFriendlyRoute implements RoutingStrategy {
    @Override
    public RouteInfo calculateRoute(SmartBin bin) {
        int baseTime = 4000; // 4 seconds base simulation time
        boolean heavy = CityTrafficSystem.getInstance().isHeavyTraffic();
        int finalTime = (int) (baseTime * CityTrafficSystem.getInstance().getTrafficMultiplier());

        String desc = heavy ? "ECO ROUTING: Slow crawling through heavy traffic to minimize emissions." 
                            : "ECO ROUTING: Steady pace via green corridors to minimize fuel.";
        return new RouteInfo(desc, finalTime);
    }
}

// Concrete Strategy 3: Dynamic / Traffic-Aware Routing (Avoids full congestion delay)
class TrafficAwareRoute implements RoutingStrategy {
    @Override
    public RouteInfo calculateRoute(SmartBin bin) {
        boolean heavy = CityTrafficSystem.getInstance().isHeavyTraffic();
        if (heavy) {
            // Takes a longer physical detour (base time 3s instead of 2s) but avoids the 2.5x multiplier bottleneck
            int finalTime = 3000; 
            String desc = "TRAFFIC-AWARE ROUTING: Detecting heavy congestion. Diverting via alternative detour streets.";
            return new RouteInfo(desc, finalTime);
        } else {
            int finalTime = 2000;
            String desc = "TRAFFIC-AWARE ROUTING: Traffic is light. Proceeding on default shortest route.";
            return new RouteInfo(desc, finalTime);
        }
    }
}

// Strategy Manager / Factory to encapsulate strategy list and creation
class RoutingStrategyManager {
    public static RoutingStrategy getStrategy(String name) {
        return switch (name) {
            case "Eco-Friendly Route" -> new EcoFriendlyRoute();
            case "Traffic-Aware Route" -> new TrafficAwareRoute();
            default -> new FastestRoute(); // Default
        };
    }

    public static String[] getAvailableStrategies() {
        return new String[] { "Fastest Route", "Eco-Friendly Route", "Traffic-Aware Route" };
    }
}
