package model;

import java.util.ArrayList;
import java.util.List;

public class RobotModel {
    private double x = 100;
    private double y = 100;
    private double direction = 0;
    private double targetX = 150, targetY = 100;
    private double prevTargetX = 150, prevTargetY = 100;

    private final List<RobotModelListener> listeners = new ArrayList<>();

    public void updatePosition(double x, double y, double direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        notifyListeners();
    }

    public void setTarget(double x, double y) {
        this.prevTargetX = this.targetX;
        this.prevTargetY = this.targetY;
        this.targetX = x;
        this.targetY = y;
    }

    public void addListener(RobotModelListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (RobotModelListener l : listeners) {
            l.onModelUpdated(x, y, direction);
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDirection() { return direction; }
}