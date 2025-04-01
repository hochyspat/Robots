package gui;

import javax.swing.JInternalFrame;
import java.util.Properties;

public record WindowGeometry(int x, int y, int width, int height, boolean minimized, boolean maximized) {

    WindowGeometry(JInternalFrame frame) {
        this(
                frame.getX(),
                frame.getY(),
                frame.getWidth(),
                frame.getHeight(),
                frame.isIcon(),
                frame.isMaximum()
        );
    }

    WindowGeometry(Properties props, String key) {
        this(
                Integer.parseInt(props.getProperty(key + ".x", "0")),
                Integer.parseInt(props.getProperty(key + ".y", "0")),
                Integer.parseInt(props.getProperty(key + ".width", "200")),
                Integer.parseInt(props.getProperty(key + ".height", "150")),
                Boolean.parseBoolean(props.getProperty(key + ".minimized", "false")),
                Boolean.parseBoolean(props.getProperty(key + ".maximized", "false"))
        );
    }

    void applyTo(JInternalFrame frame) throws Exception {
        frame.setBounds(x, y, width, height);
        frame.setIcon(minimized);
        frame.setMaximum(maximized);
    }

    void saveTo(Properties props, String key) {
        props.setProperty(key + ".x", String.valueOf(x));
        props.setProperty(key + ".y", String.valueOf(y));
        props.setProperty(key + ".width", String.valueOf(width));
        props.setProperty(key + ".height", String.valueOf(height));
        props.setProperty(key + ".minimized", String.valueOf(minimized));
        props.setProperty(key + ".maximized", String.valueOf(maximized));
    }
}