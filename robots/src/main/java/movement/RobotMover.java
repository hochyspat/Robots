package movement;

import model.RobotModel;

import java.awt.Point;

public class RobotMover {
    private final RobotModel model;

    private final double maxVelocity = 0.1;
    private final double maxAngularVelocity = 0.05;
    private final double angularVelocityEpsilon = 1e-5;
    private final double sharpTurnThreshold = 0.5;
    private final double noTurnThreshold = 0.02;
    private final double sharpTurnAngularScale = 0.7;
    private final double smoothTurnAngularScale = 0.05;
    private final int updateDurationMs = 10;
    private final double targetReachedDistance = 0.5;

    public RobotMover(RobotModel model) {
        this.model = model;
    }

    public boolean moveTowards(Point target) {
        double x = model.getX();
        double y = model.getY();
        double direction = model.getDirection();

        double distance = distance(x, y, target.x, target.y);
        if (distance < targetReachedDistance) return true;

        double angleToTarget = angleTo(x, y, target.x, target.y);
        double angleDiff = normalizeAngle(angleToTarget - direction);

        double velocity;
        double angularVelocity;

        if (Math.abs(angleDiff) > sharpTurnThreshold) {
            velocity = 0;
            angularVelocity = applyLimits(angleDiff * sharpTurnAngularScale);
        } else if (Math.abs(angleDiff) < noTurnThreshold) {
            velocity = maxVelocity;
            angularVelocity = 0;
        } else {
            velocity = maxVelocity;
            angularVelocity = applyLimits(angleDiff * smoothTurnAngularScale);
        }

        moveRobot(x, y, direction, velocity, angularVelocity);
        return false;
    }

    private void moveRobot(double x, double y, double direction, double velocity, double angularVelocity) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

        double newX, newY;
        if (Math.abs(angularVelocity) < angularVelocityEpsilon) {
            newX = x + velocity * updateDurationMs * Math.cos(direction);
            newY = y + velocity * updateDurationMs * Math.sin(direction);
        } else {
            double radius = velocity / angularVelocity;
            double deltaAngle = angularVelocity * updateDurationMs;

            newX = x + radius * (Math.sin(direction + deltaAngle) - Math.sin(direction));
            newY = y - radius * (Math.cos(direction + deltaAngle) - Math.cos(direction));
        }

        double newDirection = asNormalizedRadians(direction + angularVelocity * updateDurationMs);
        model.updatePosition(newX, newY, newDirection);
    }

    private static double applyLimits(double value) {
        return applyLimits(value, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private static double applyLimits(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        return asNormalizedRadians(Math.atan2(toY - fromY, toX - fromX));
    }

    private static double normalizeAngle(double angle) {
        while (angle < -Math.PI) angle += 2 * Math.PI;
        while (angle > Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
