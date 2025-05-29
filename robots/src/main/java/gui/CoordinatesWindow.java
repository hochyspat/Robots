package gui;

import model.RobotModel;
import model.RobotModelListener;

import javax.swing.*;
import java.awt.*;

public class CoordinatesWindow extends JInternalFrame implements RobotModelListener, SaveableWindow {
    private final JLabel label;
    private final RobotModel model;

    public CoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        this.model = model;
        label = new JLabel("X: 0.0, Y: 0.0, ΔDir: 0.0°");
        add(label, BorderLayout.CENTER);
        setSize(200, 100);
        setVisible(true);
    }

    @Override
    public void onModelUpdated(double x, double y, double direction) {
        label.setText(String.format("X: %.1f, Y: %.1f", x, y));
    }

}
