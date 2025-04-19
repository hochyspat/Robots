package gui;

import movement.CustomRouteMovement;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();
    private movement.MovementMode movementMode = new movement.ManualMovement();
    private final RouteLoader routeLoader = new RouteLoader();

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private final double maxVelocity = 0.1;
    private final double maxAngularVelocity = 0.05;
    private final double angularVelocityEpsilon = 1e-5;

    private final double sharpTurnThreshold = 0.5;
    private final double noTurnThreshold = 0.02;

    private final double sharpTurnAngularScale = 0.7;
    private final double smoothTurnAngularScale = 0.05;
    private final int updateDurationMs = 10;
    private final double nearTargetThreshold = 1.0;

    private final double targetReachedDistance = 0.5;
    private final int routePointDiameter = 4;
    private final Color routePointColor = Color.BLUE;

    private List<Point> customRoute = new ArrayList<>();
    private boolean inCustomRouteInput = false;
    private boolean customRouteActive = false;
    private boolean routePaused = false;
    private boolean loadedFromFile = false;


    public GameVisualizer() {
        initTimers();
        initMouseControls();
        new RouteKeyHandler(this);
        setDoubleBuffered(true);
    }

    private void initMouseControls() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (inCustomRouteInput) {
                    customRoute.add(e.getPoint());
                    repaint();
                } else if (!customRouteActive) {
                    setTargetPosition(e.getPoint());
                    repaint();
                }
            }
        });
    }

    private void initTimers() {
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);
    }

    public void setMovementMode(movement.MovementMode mode) {
        this.movementMode = mode;
    }

    public void enableCustomRouteMode() {
        customRoute.clear();
        inCustomRouteInput = true;
        customRouteActive = false;
        routePaused = false;
        loadedFromFile = false;
        setMovementMode(null);
        repaint();
    }

    public void disableCustomRouteMode() {
        inCustomRouteInput = false;
        customRouteActive = false;
        customRoute.clear();
        repaint();
    }

    public void loadCustomRouteFromFile(File file) {
        List<Point> route = routeLoader.load(file);

        customRoute.clear();
        customRoute.addAll(route);

        if (!customRoute.isEmpty()) {
            setTargetPosition(customRoute.get(0));
            setMovementMode(new CustomRouteMovement(customRoute));
            inCustomRouteInput = false;
            customRouteActive = true;
            routePaused = false;
            loadedFromFile = true;
        }

        repaint();
    }


    public void updateManual() {
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < targetReachedDistance) return;

        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY,
                m_targetPositionX, m_targetPositionY);
        double angleDiff = normalizeAngle(angleToTarget - m_robotDirection);

        double velocity;
        double angularVelocity;

        if (Math.abs(angleDiff) > sharpTurnThreshold) {
            velocity = 0;
            angularVelocity = applyLimits(angleDiff * sharpTurnAngularScale, -maxAngularVelocity, maxAngularVelocity);
        } else if (Math.abs(angleDiff) < noTurnThreshold) {
            velocity = maxVelocity;
            angularVelocity = 0;
        } else {
            velocity = maxVelocity;
            angularVelocity = applyLimits(angleDiff * smoothTurnAngularScale, -maxAngularVelocity, maxAngularVelocity);
        }

        moveRobot(velocity, angularVelocity, updateDurationMs);
    }


    private static double normalizeAngle(double angle) {
        while (angle < -Math.PI) angle += 2 * Math.PI;
        while (angle > Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    public boolean isNearTarget(Point target) {
        return distance(m_robotPositionX, m_robotPositionY, target.x, target.y) < nearTargetThreshold;
    }

    public void setTargetPosition(Point p) {
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    protected void onModelUpdateEvent() {
        if (movementMode != null && !routePaused) {
            movementMode.update(this);
        }

    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

        double newX;
        double newY;

        if (Math.abs(angularVelocity) < angularVelocityEpsilon) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        } else {
            double radius = velocity / angularVelocity;
            double deltaAngle = angularVelocity * duration;

            newX = m_robotPositionX + radius * (
                    Math.sin(m_robotDirection + deltaAngle) - Math.sin(m_robotDirection)
            );
            newY = m_robotPositionY - radius * (
                    Math.cos(m_robotDirection + deltaAngle) - Math.cos(m_robotDirection)
            );
        }

        m_robotPositionX = newX;
        m_robotPositionY = newY;
        m_robotDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
    }


    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static int round(double value) {
        double roundingBias = 0.5;
        return (int) (value + roundingBias);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), m_robotDirection);
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);

        if (!customRoute.isEmpty()) {
            g2d.setColor(routePointColor);
            for (Point p : customRoute) {
                g2d.fillOval(
                        p.x - routePointDiameter / 2,
                        p.y - routePointDiameter / 2,
                        routePointDiameter,
                        routePointDiameter
                );
            }
        }

    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        int robotCenterX = round(m_robotPositionX);
        int robotCenterY = round(m_robotPositionY);
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    public boolean isInCustomRouteInput() {
        return inCustomRouteInput;
    }

    public void setInCustomRouteInput(boolean val) {
        inCustomRouteInput = val;
    }

    public boolean isCustomRouteActive() {
        return customRouteActive;
    }

    public void setCustomRouteActive(boolean val) {
        customRouteActive = val;
    }

    public boolean isRoutePaused() {
        return routePaused;
    }

    public void setRoutePaused(boolean val) {
        routePaused = val;
    }

    public boolean isLoadedFromFile() {
        return loadedFromFile;
    }

    public List<Point> getCustomRoute() {
        return customRoute;
    }

}
