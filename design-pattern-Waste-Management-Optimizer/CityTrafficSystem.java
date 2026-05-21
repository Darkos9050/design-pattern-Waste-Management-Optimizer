package com.mycompany.mavenproject8;

/**
 * Singleton Pattern (Creational)
 * Part of Smart City Infrastructure Simulation
 * Manages the global city traffic sensor status and metrics.
 */
public class CityTrafficSystem {
    private static CityTrafficSystem instance;
    private boolean isHeavyTraffic = false;
    private int trafficChangeCount = 0;

    private CityTrafficSystem() {
        // Private constructor to prevent instantiation
    }

    public static synchronized CityTrafficSystem getInstance() {
        if (instance == null) {
            instance = new CityTrafficSystem();
        }
        return instance;
    }

    public boolean isHeavyTraffic() {
        return isHeavyTraffic;
    }

    public void setHeavyTraffic(boolean heavy) {
        this.isHeavyTraffic = heavy;
    }

    public double getTrafficMultiplier() {
        return isHeavyTraffic ? 2.5 : 1.0; // 2.5x travel time delay if heavy
    }

    public int getTrafficChangeCount() {
        return trafficChangeCount;
    }

    public String toggleTrafficAndGetLog() {
        this.isHeavyTraffic = !this.isHeavyTraffic;
        this.trafficChangeCount++;
        if (this.isHeavyTraffic) {
            return "\n[TRAFFIC SENSOR] \u26A0\uFE0F HIGH TRAFFIC detected on main routes! (Sensor Update #" + trafficChangeCount + ")";
        } else {
            return "\n[TRAFFIC SENSOR] \u2705 Traffic cleared. Normal flow resumed. (Sensor Update #" + trafficChangeCount + ")";
        }
    }

    public String getTrafficStatusLabel() {
        return "City Traffic Sensor: " + (isHeavyTraffic ? "HEAVY \u25CF" : "LIGHT \u25CF");
    }
}
