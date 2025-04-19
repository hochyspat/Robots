package gui;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RouteLoader {
    public List<Point> load(File file) {
        List<Point> route = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    int x = Integer.parseInt(parts[0].trim());
                    int y = Integer.parseInt(parts[1].trim());
                    route.add(new Point(x, y));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return route;
    }
}

