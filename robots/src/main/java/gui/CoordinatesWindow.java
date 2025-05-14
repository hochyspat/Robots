package gui;

import model.RobotModelListener;

import javax.swing.*;
import java.awt.*;

public class CoordinatesWindow extends JInternalFrame implements RobotModelListener, SaveableWindow {
    private final JLabel label;

    public CoordinatesWindow() {
        super("Координаты робота", true, true, true, true);
        label = new JLabel("X: 0.0, Y: 0.0, Dir: 0.0°");
        add(label, BorderLayout.CENTER);
        setSize(200, 100);
        setVisible(true);
    }

    @Override
    public void onModelUpdated(double x, double y, double direction) {
        label.setText(String.format("X: %.1f, Y: %.1f, Dir: %.0f°", x, y, Math.toDegrees(direction)));
    }
}