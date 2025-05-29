package gui;

import model.RobotModel;
import movement.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();
    private movement.MovementMode movementMode = new ManualMovement();
    private final RouteLoader routeLoader = new RouteLoader();
    private final RouteSaver routeSaver = new RouteSaver();

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private final int updateDurationMs = 10;
    private final RobotModel model;
    private final RobotMover mover;

    private final int routePointDiameter = 4;
    private final Color routePointColor = Color.BLUE;

    private List<Point> customRoute = new ArrayList<>();
    private boolean inCustomRouteInput = false;
    private boolean customRouteActive = false;
    private boolean routePaused = false;
    private boolean loadedFromFile = false;

    public GameVisualizer(RobotModel model) {
        this.model = model;
        this.mover = new RobotMover(model);

        initTimers();
        initMouseControls();
        new RouteKeyHandler(this);
        setDoubleBuffered(true);
    }

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    public void saveCustomRouteToFile(File file) {
        if (!customRoute.isEmpty()) {
            routeSaver.saveToFile(customRoute, file);
        }
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
                EventQueue.invokeLater(() -> repaint());
            }
        }, 0, 50);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, updateDurationMs);
    }

    public void setMovementMode(MovementMode mode) {
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
        mover.moveTowards(new Point(m_targetPositionX, m_targetPositionY));
    }

    public void setTargetPosition(Point p) {
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
        model.setTarget(p.x, p.y);
    }

    protected void onModelUpdateEvent() {
        if (movementMode != null && !routePaused) {
            movementMode.update(this);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d);
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);

        if (!customRoute.isEmpty()) {
            g2d.setColor(routePointColor);
            for (Point p : customRoute) {
                g2d.fillOval(p.x - routePointDiameter / 2, p.y - routePointDiameter / 2, routePointDiameter, routePointDiameter);
            }
        }
    }

    private void drawRobot(Graphics2D g) {
        int x = round(model.getX());
        int y = round(model.getY());
        double direction = model.getDirection();

        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, x, y, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, x + 10, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x + 10, y, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        g.setTransform(new AffineTransform());
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static int round(double value) {
        return (int) (value + 0.5);
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

    public RobotMover getMover() {
        return mover;
    }
}
