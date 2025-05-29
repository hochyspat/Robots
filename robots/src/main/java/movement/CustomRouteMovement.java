package movement;

import gui.GameVisualizer;

import java.awt.Point;
import java.util.List;

public class CustomRouteMovement implements MovementMode {
    private final List<Point> route;
    private int currentIndex = 0;

    public CustomRouteMovement(List<Point> route) {
        this.route = route;
    }

    @Override
    public void update(GameVisualizer visualizer) {
        if (route == null || route.isEmpty()) return;

        Point target = route.get(currentIndex);
        boolean reached = visualizer.getMover().moveTowards(target);

        if (reached) {
            currentIndex = (currentIndex + 1) % route.size();
            visualizer.setTargetPosition(route.get(currentIndex));
        }
    }
}
