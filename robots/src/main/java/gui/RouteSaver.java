package gui;

import java.awt.Point;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class RouteSaver {
    public void saveToFile(List<Point> route, File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Point p : route) {
                writer.println(p.x + "," + p.y);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
