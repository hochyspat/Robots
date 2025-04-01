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
    private final Path configPath = Paths.get(System.getProperty("user.home"), ".robotapp", "windows.properties");

    public void saveWindows(JDesktopPane desktopPane) {
        Properties props = new Properties();

        try {
            Method getAllFramesMethod = JDesktopPane.class.getMethod("getAllFrames");
            JInternalFrame[] frames = (JInternalFrame[]) getAllFramesMethod.invoke(desktopPane);

            for (JInternalFrame frame : frames) {
                String windowName = frame.getTitle().replaceAll("\\s+", "_");
                try {
                    WindowGeometry geometry = new WindowGeometry(frame);
                    geometry.saveTo(props, windowName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Files.createDirectories(configPath.getParent());
            try (OutputStream out = Files.newOutputStream(configPath)) {
                props.store(out, "Window Geometry Settings");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadWindows(JDesktopPane desktopPane) {
        if (!Files.exists(configPath)) return;

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(configPath)) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            Method getAllFramesMethod = JDesktopPane.class.getMethod("getAllFrames");
            JInternalFrame[] frames = (JInternalFrame[]) getAllFramesMethod.invoke(desktopPane);

            for (JInternalFrame frame : frames) {
                String windowName = frame.getTitle().replaceAll("\\s+", "_");
                try {
                    WindowGeometry geometry = new WindowGeometry(props, windowName);
                    geometry.applyTo(frame);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
