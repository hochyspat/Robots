package movement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class RouteFactory {

    public static List<Point> squareRoute() {
        return List.of(
                new Point(100, 100),
                new Point(300, 100),
                new Point(300, 300),
                new Point(100, 300)
        );
    }

    public static List<Point> figureEightRoute() {
        List<Point> route = new ArrayList<>();

        int centerX = 300;
        int centerY = 250;
        int radius = 120;
        int step = 10;

        for (int angle = 0; angle <= 720; angle += step) {
            double t = Math.toRadians(angle);
            double x = centerX + radius * Math.sin(t);
            double y = centerY + radius * Math.sin(t) * Math.cos(t);
            route.add(new Point((int) x, (int) y));
        }

        return route;
    }

}
