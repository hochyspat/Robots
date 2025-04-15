package gui;

import javax.swing.JInternalFrame;
import javax.swing.JFrame;
import java.awt.Component;
import java.awt.Frame;
import java.util.Properties;

public record WindowGeometry(int x, int y, int width, int height, boolean minimized, boolean maximized) {

    public WindowGeometry(Component window, boolean isMinimized, boolean isMaximized) {
        this(window.getX(), window.getY(), window.getWidth(), window.getHeight(), isMinimized, isMaximized);
    }

    public WindowGeometry(JInternalFrame frame) {
        this(frame, frame.isIcon(), frame.isMaximum());
    }

    public WindowGeometry(JFrame frame) {
        this(frame, false, frame.getExtendedState() == Frame.MAXIMIZED_BOTH);
    }

    public WindowGeometry(Properties props, String key) {
        this(
                Integer.parseInt(props.getProperty(key + ".x", "0")),
                Integer.parseInt(props.getProperty(key + ".y", "0")),
                Integer.parseInt(props.getProperty(key + ".width", "800")),
                Integer.parseInt(props.getProperty(key + ".height", "600")),
                Boolean.parseBoolean(props.getProperty(key + ".minimized", "false")),
                Boolean.parseBoolean(props.getProperty(key + ".maximized", "false"))
        );
    }

    public void applyTo(Component window) {
        window.setBounds(x, y, width, height);
        if (window instanceof JFrame frame) {
            frame.setExtendedState(maximized ? Frame.MAXIMIZED_BOTH : Frame.NORMAL);
        } else if (window instanceof JInternalFrame frame) {
            try {
                frame.setIcon(minimized);
                frame.setMaximum(maximized);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveTo(Properties props, String key) {
        props.setProperty(key + ".x", String.valueOf(x));
        props.setProperty(key + ".y", String.valueOf(y));
        props.setProperty(key + ".width", String.valueOf(width));
        props.setProperty(key + ".height", String.valueOf(height));
        props.setProperty(key + ".minimized", String.valueOf(minimized));
        props.setProperty(key + ".maximized", String.valueOf(maximized));
    }
}