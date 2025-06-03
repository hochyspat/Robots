package movement;

import java.awt.*;

public record RouteCacheEntry(Point from, Point to, double distance, double angleRadians) {
}
