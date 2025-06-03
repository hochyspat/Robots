package movement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class CachedRoute {
    private final List<RouteCacheEntry> cache;

    public CachedRoute(List<Point> points) {
        this.cache = new ArrayList<>(points.size());

        for (int i = 0; i < points.size(); i++) {
            Point from = points.get(i);
            Point to = points.get((i + 1) % points.size());
            double dx = to.x - from.x;
            double dy = to.y - from.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            double angle = Math.atan2(dy, dx);

            cache.add(new RouteCacheEntry(from, to, distance, angle));
        }
    }

    public RouteCacheEntry getEntry(int index) {
        return cache.get(index % cache.size());
    }

    public int size() {
        return cache.size();
    }
}
