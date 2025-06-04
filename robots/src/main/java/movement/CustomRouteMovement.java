package movement;

import gui.GameVisualizer;

import java.awt.Point;
import java.util.List;

public class CustomRouteMovement implements MovementMode {
    private final CachedRoute route;
    private int currentIndex = 0;

    public CustomRouteMovement(List<Point> points) {
        this.route = new CachedRoute(points);
    }

    @Override
    public void update(GameVisualizer visualizer) {
        RouteCacheEntry entry = route.getEntry(currentIndex);
        boolean reached = visualizer.getMover().moveTowards(entry.to());

        if (reached) {
            currentIndex = (currentIndex + 1) % route.size();
            visualizer.setTargetPosition(route.getEntry(currentIndex).to());
        }
    }
}
