package com.mycompany.mavenproject8;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Smart City Infrastructure Simulation (Theme A)
 * Waste Management & Traffic Monitoring Optimizer
 * University Project ITSE424
 *
 * Implemented Design Patterns:
 * 1. Factory Method Pattern (Creational): BinFactory and concrete classes. (In this file)
 * 2. Observer Pattern (Behavioral): Subject, Observer, SmartBin, DispatchCenter. (In this file)
 * 3. Strategy Pattern (Behavioral): RoutingStrategy, FastestRoute, EcoFriendlyRoute. (In RoutingStrategy.java)
 * 4. Singleton Pattern (Creational): CityTrafficSystem. (In CityTrafficSystem.java)
 */
public class WasteManagementOptimizerApp extends JFrame {

    // ==========================================
    // CORE SYSTEM & ANALYTICS
    // ==========================================
    private int totalWasteCollected = 0;
    private int totalDispatches = 0;

    private List<SmartBin> bins;
    private DispatchCenter dispatchCenter;
    private JTextArea logArea;
    private JLabel statsLabel;
    private JLabel trafficLabel;
    private Timer autoFillTimer;
    private JComboBox<String> strategyComboBox;

    public WasteManagementOptimizerApp() {
        // Main Frame Setup
        setTitle("Smart City Infrastructure Simulator - Theme A (ITSE424)");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        dispatchCenter = new DispatchCenter();

        buildTopPanel();
        buildCenterPanel();
        buildBottomPanel();

        log("[SYSTEM] Smart City Infrastructure Simulator Booted.");
        log("[SYSTEM] Dispatch Center online. Traffic sensors active.");
    }

    private void buildTopPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(40, 40, 40));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        // Title and Stats (Left)
        JPanel titleStatsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        titleStatsPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Smart City Infrastructure Simulator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        statsLabel = new JLabel("Total Waste Collected: 0 units   |   Total Truck Dispatches: 0");
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statsLabel.setForeground(new Color(170, 170, 170));
        
        trafficLabel = new JLabel("City Traffic Sensor: LIGHT \u25CF");
        trafficLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        trafficLabel.setForeground(new Color(76, 175, 80)); // Green

        titleStatsPanel.add(titleLabel);
        titleStatsPanel.add(statsLabel);
        titleStatsPanel.add(trafficLabel);
        headerPanel.add(titleStatsPanel, BorderLayout.WEST);

        // Controls (Right)
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        controlsPanel.setOpaque(false);

        JLabel strategyLabel = new JLabel("Emergency Routing:");
        strategyLabel.setForeground(Color.WHITE);
        strategyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        strategyComboBox = new JComboBox<>(RoutingStrategyManager.getAvailableStrategies());
        strategyComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        strategyComboBox.setFocusable(false);
        strategyComboBox.addActionListener(e -> {
            String selected = (String) strategyComboBox.getSelectedItem();
            RoutingStrategy selectedStrategy = RoutingStrategyManager.getStrategy(selected);
            dispatchCenter.setStrategy(selectedStrategy);
            log("[CONFIG] Strategy changed to " + selected.toUpperCase() + ".");
        });

        JButton simBtn = new JButton("Start City Simulation");
        styleButton(simBtn);
        simBtn.setBackground(new Color(46, 125, 50)); // Green

        // Auto-fill and Traffic Simulation Timer
        autoFillTimer = new Timer(800, e -> {
            // Traffic Sensor Simulation (Theme A)
            if (new Random().nextInt(100) < 5) { // 5% chance every tick to change traffic
                String logMessage = CityTrafficSystem.getInstance().toggleTrafficAndGetLog();
                log(logMessage);
                trafficLabel.setText(CityTrafficSystem.getInstance().getTrafficStatusLabel());
                trafficLabel.setForeground(CityTrafficSystem.getInstance().isHeavyTraffic() ? new Color(244, 67, 54) : new Color(76, 175, 80));
            }

            // Waste Generation Simulation
            SmartBin randomBin = bins.get(new Random().nextInt(bins.size()));
            if (randomBin.getState() == BinState.NORMAL) {
                int waste = 5 + new Random().nextInt(15); // Add 5% to 20%
                randomBin.addWaste(waste);
            }
        });

        simBtn.addActionListener(e -> {
            if (autoFillTimer.isRunning()) {
                autoFillTimer.stop();
                simBtn.setText("Start City Simulation");
                simBtn.setBackground(new Color(46, 125, 50)); // Green
                log("[SYSTEM] City simulation STOPPED.");
            } else {
                autoFillTimer.start();
                simBtn.setText("Stop City Simulation");
                simBtn.setBackground(new Color(198, 40, 40)); // Red
                log("[SYSTEM] City simulation STARTED.");
            }
        });

        controlsPanel.add(strategyLabel);
        controlsPanel.add(strategyComboBox);
        controlsPanel.add(simBtn);

        headerPanel.add(controlsPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void buildCenterPanel() {
        JPanel binsPanel = new JPanel();
        binsPanel.setBackground(new Color(30, 30, 30));
        binsPanel.setLayout(new GridLayout(2, 4, 20, 20));
        binsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        bins = new ArrayList<>();
        BinFactory organicFactory = new OrganicBinFactory();
        BinFactory recyclingFactory = new RecyclingBinFactory();
        BinFactory glassFactory = new GlassBinFactory();
        BinFactory hazardousFactory = new HazardousBinFactory();

        // Create a diverse set of bins representing city utilities
        bins.add(organicFactory.createBin("Zone-1 Organic"));
        bins.add(recyclingFactory.createBin("Zone-1 Recycle"));
        bins.add(glassFactory.createBin("Downtown Glass"));
        bins.add(hazardousFactory.createBin("Industrial Haz"));
        bins.add(organicFactory.createBin("Zone-2 Organic"));
        bins.add(recyclingFactory.createBin("Zone-2 Recycle"));
        bins.add(recyclingFactory.createBin("Suburban Recycle"));
        bins.add(hazardousFactory.createBin("Hospital Haz"));

        for (SmartBin bin : bins) {
            bin.addObserver(dispatchCenter);
            binsPanel.add(new BinPanelUI(bin));
        }

        add(binsPanel, BorderLayout.CENTER);
    }

    private void buildBottomPanel() {
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(20, 20, 20));
        logArea.setForeground(new Color(0, 255, 0)); // Matrix green text
        logArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        logArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(getWidth(), 220));
        logScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                "Central Command Event Log", 0, 0, new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));
        logScrollPane.getViewport().setBackground(new Color(20, 20, 20));

        add(logScrollPane, BorderLayout.SOUTH);
    }

    public void log(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public void incrementAnalytics(int wasteAmount) {
        totalWasteCollected += wasteAmount;
        totalDispatches++;
        SwingUtilities.invokeLater(() -> {
            statsLabel.setText(String.format("Total Waste Collected: %d units   |   Total Truck Dispatches: %d",
                    totalWasteCollected, totalDispatches));
        });
    }

    private static void styleButton(JButton btn) {
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                new EmptyBorder(8, 15, 8, 15)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ==========================================
    // DISPATCH CENTER (Observer)
    // ==========================================
    class DispatchCenter implements Observer {
        private RoutingStrategy strategy;

        public DispatchCenter() {
            this.strategy = new FastestRoute(); // Default strategy
        }

        public void setStrategy(RoutingStrategy strategy) {
            this.strategy = strategy;
        }

        @Override
        public void update(SmartBin bin) {
            // Check if bin just became full and is waiting for truck
            if (bin.getState() == BinState.WAITING_FOR_TRUCK) {
                RouteInfo route = strategy.calculateRoute(bin);
                log("\n[UTILITY ALERT] \u26A0\uFE0F " + bin.getId() + " (" + bin.getType() + ") is FULL! Dispatching truck...");
                log("  \u2514\u25B6 " + route.description());
                log("  \u2514\u25B6 Estimated arrival time: " + (route.estimatedTimeMs() / 1000.0) + "s");

                // Simulate truck travel time asynchronously
                Timer travelTimer = new Timer(route.estimatedTimeMs(), e -> {
                    log("[TRUCK] \uD83D\uDE9A Arrived at " + bin.getId() + ". Commencing emptying process...");
                    bin.setState(BinState.EMPTYING);

                    // Simulate the time it takes to physically empty the bin
                    Timer emptyTimer = new Timer(1500, ev -> {
                        int amount = bin.getCapacity();
                        bin.emptyBin();
                        incrementAnalytics(amount);
                        log("[SUCCESS] \u2705 " + bin.getId() + " has been emptied and is back in service.");
                    });
                    emptyTimer.setRepeats(false);
                    emptyTimer.start();
                });
                travelTimer.setRepeats(false);
                travelTimer.start();
            }
        }
    }

    // ==========================================
    // CUSTOM UI COMPONENTS
    // ==========================================
    class BinPanelUI extends JPanel {
        private SmartBin bin;
        private CircularProgressBar progressBar;
        private JLabel statusLabel;
        private JButton addWasteBtn;

        public BinPanelUI(SmartBin bin) {
            this.bin = bin;
            setLayout(new BorderLayout());
            setBackground(new Color(45, 45, 45));
            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(15, new Color(70, 70, 70)),
                    new EmptyBorder(15, 15, 15, 15)));

            // Header
            JLabel titleLabel = new JLabel(bin.getId());
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel typeLabel = new JLabel(bin.getType());
            typeLabel.setForeground(new Color(150, 150, 150));
            typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JPanel headerPanel = new JPanel(new GridLayout(2, 1));
            headerPanel.setOpaque(false);
            headerPanel.add(titleLabel);
            headerPanel.add(typeLabel);
            
            add(headerPanel, BorderLayout.NORTH);

            // Progress Bar
            Color binColor = switch (bin.getType()) {
                case "Organic" -> new Color(76, 175, 80);
                case "Recycling" -> new Color(33, 150, 243);
                case "Glass" -> new Color(0, 188, 212);
                case "Hazardous" -> new Color(255, 152, 0);
                default -> Color.GRAY;
            };
            progressBar = new CircularProgressBar(binColor);
            add(progressBar, BorderLayout.CENTER);

            // Footer / Controls
            JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            bottomPanel.setOpaque(false);

            statusLabel = new JLabel("Status: NORMAL");
            statusLabel.setForeground(new Color(170, 170, 170));
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
            bottomPanel.add(statusLabel);

            addWasteBtn = new JButton("+ Add Waste");
            styleButton(addWasteBtn);
            addWasteBtn.setBackground(new Color(60, 60, 60));
            addWasteBtn.addActionListener(e -> {
                bin.addWaste(25); // Adds exactly 25%
            });
            bottomPanel.add(addWasteBtn);

            add(bottomPanel, BorderLayout.SOUTH);

            // Register UI Callback to react to backend changes
            bin.setUiCallback(this::updateUIState);
            updateUIState(); // Initial paint
        }

        private void updateUIState() {
            SwingUtilities.invokeLater(() -> {
                progressBar.updateCapacity(bin.getCapacity());

                switch (bin.getState()) {
                    case NORMAL -> {
                        statusLabel.setText("Status: NORMAL");
                        statusLabel.setForeground(new Color(170, 170, 170));
                        addWasteBtn.setEnabled(true);
                        addWasteBtn.setBackground(new Color(60, 60, 60));
                    }
                    case WAITING_FOR_TRUCK -> {
                        statusLabel.setText("Status: AWAITING TRUCK");
                        statusLabel.setForeground(new Color(244, 67, 54)); // Red
                        addWasteBtn.setEnabled(false);
                        addWasteBtn.setBackground(new Color(40, 40, 40));
                    }
                    case EMPTYING -> {
                        statusLabel.setText("Status: EMPTYING...");
                        statusLabel.setForeground(new Color(255, 152, 0)); // Orange
                        addWasteBtn.setEnabled(false);
                        addWasteBtn.setBackground(new Color(40, 40, 40));
                    }
                }
            });
        }
    }

    static class CircularProgressBar extends JComponent {
        private int capacity = 0;
        private Color themeColor;

        public CircularProgressBar(Color themeColor) {
            this.themeColor = themeColor;
            setOpaque(false);
            setPreferredSize(new Dimension(100, 100));
        }

        public void updateCapacity(int capacity) {
            this.capacity = capacity;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int strokeWidth = 12;
            int size = Math.min(getWidth(), getHeight()) - (strokeWidth * 2);
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            // Background circle track
            g2.setColor(new Color(60, 60, 60));
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(x, y, size, size);

            // Colored Progress arc
            Color progressColor = (capacity >= 100) ? new Color(244, 67, 54) : themeColor;
            g2.setColor(progressColor);
            int angle = (int) (360 * (capacity / 100.0));
            g2.drawArc(x, y, size, size, 90, -angle);

            // Center Text
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
            String text = capacity + "%";
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(text)) / 2;
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, textX, textY);
        }
    }

    static class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        private Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius / 2, this.radius / 2, this.radius / 2, this.radius / 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    // ==========================================
    // MAIN LAUNCHER
    // ==========================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                // Ignore
            }
            WasteManagementOptimizerApp app = new WasteManagementOptimizerApp();
            app.setLocationRelativeTo(null); // Center on screen
            app.setVisible(true);
        });
    }
}

// ==========================================
// BIN STATE ENUM
// ==========================================
enum BinState {
    NORMAL, WAITING_FOR_TRUCK, EMPTYING
}

// ==========================================
// OBSERVER PATTERN (Behavioral)
// ==========================================
interface Observer {
    void update(SmartBin bin);
}

interface Subject {
    void addObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}

// Concrete Subject (Base Class)
abstract class SmartBin implements Subject {
    private String id;
    private String type;
    private int capacity; // Percentage: 0 to 100
    private BinState state;
    private List<Observer> observers;
    private Runnable uiCallback;

    public SmartBin(String id, String type) {
        this.id = id;
        this.type = type;
        this.capacity = 0;
        this.state = BinState.NORMAL;
        this.observers = new ArrayList<>();
    }

    public void setUiCallback(Runnable callback) {
        this.uiCallback = callback;
    }

    public void addWaste(int amount) {
        // Only add waste if normal functioning state
        if (this.state != BinState.NORMAL)
            return;

        this.capacity += amount;
        if (this.capacity >= 100) {
            this.capacity = 100;
            this.state = BinState.WAITING_FOR_TRUCK;
            if (uiCallback != null)
                uiCallback.run();
            notifyObservers();
        } else {
            if (uiCallback != null)
                uiCallback.run();
        }
    }

    public void emptyBin() {
        this.capacity = 0;
        this.state = BinState.NORMAL;
        if (uiCallback != null)
            uiCallback.run();
    }

    public void setState(BinState state) {
        this.state = state;
        if (uiCallback != null)
            uiCallback.run();
    }

    public int getCapacity() {
        return capacity;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public BinState getState() {
        return state;
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(this);
        }
    }
}

// Specific Bin Subclasses
class OrganicBin extends SmartBin {
    public OrganicBin(String id) {
        super(id, "Organic");
    }
}

class RecyclingBin extends SmartBin {
    public RecyclingBin(String id) {
        super(id, "Recycling");
    }
}

class GlassBin extends SmartBin {
    public GlassBin(String id) {
        super(id, "Glass");
    }
}

class HazardousBin extends SmartBin {
    public HazardousBin(String id) {
        super(id, "Hazardous");
    }
}

// ==========================================
// FACTORY METHOD PATTERN (Creational)
// ==========================================
abstract class BinFactory {
    public abstract SmartBin createBin(String id);
}

class OrganicBinFactory extends BinFactory {
    @Override
    public SmartBin createBin(String id) {
        return new OrganicBin(id);
    }
}

class RecyclingBinFactory extends BinFactory {
    @Override
    public SmartBin createBin(String id) {
        return new RecyclingBin(id);
    }
}

class GlassBinFactory extends BinFactory {
    @Override
    public SmartBin createBin(String id) {
        return new GlassBin(id);
    }
}

class HazardousBinFactory extends BinFactory {
    @Override
    public SmartBin createBin(String id) {
        return new HazardousBin(id);
    }
}