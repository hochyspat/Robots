package gui;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Properties;

public class WindowConfigManager {
    private static final Path configPath = Paths.get(System.getProperty("user.home"), ".robotapp", "windows.properties");

    public static void saveWindows(JDesktopPane desktopPane) {
        Properties props = new Properties();

        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            String keyPrefix = frame.getTitle().replaceAll("\\s+", "_");
            props.setProperty(keyPrefix + ".x", String.valueOf(frame.getX()));
            props.setProperty(keyPrefix + ".y", String.valueOf(frame.getY()));
            props.setProperty(keyPrefix + ".width", String.valueOf(frame.getWidth()));
            props.setProperty(keyPrefix + ".height", String.valueOf(frame.getHeight()));
            props.setProperty(keyPrefix + ".icon", String.valueOf(frame.isIcon()));
            props.setProperty(keyPrefix + ".maximized", String.valueOf(frame.isMaximum()));
        }

        try {
            Files.createDirectories(configPath.getParent());
            try (OutputStream out = Files.newOutputStream(configPath)) {
                props.store(out, "Window Geometry Settings");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadWindows(JDesktopPane desktopPane) {
        if (!Files.exists(configPath)) return;

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(configPath)) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            String key = frame.getTitle().replaceAll("\\s+", "_");
            try {
                int x = Integer.parseInt(props.getProperty(key + ".x"));
                int y = Integer.parseInt(props.getProperty(key + ".y"));
                int width = Integer.parseInt(props.getProperty(key + ".width"));
                int height = Integer.parseInt(props.getProperty(key + ".height"));
                boolean icon = Boolean.parseBoolean(props.getProperty(key + ".icon"));
                boolean maximized = Boolean.parseBoolean(props.getProperty(key + ".maximized"));

                frame.setBounds(x, y, width, height);

                try {
                    Method setIconMethod = frame.getClass().getMethod("setIcon", boolean.class);
                    setIconMethod.invoke(frame, icon);

                    Method setMaxMethod = frame.getClass().getMethod("setMaximum", boolean.class);
                    setMaxMethod.invoke(frame, maximized);
                } catch (Exception reflectionError) {
                    reflectionError.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
