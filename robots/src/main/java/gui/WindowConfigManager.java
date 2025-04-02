package gui;

import javax.swing.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Properties;

public class WindowConfigManager {
    private final Path configPath = Paths.get(System.getProperty("user.home"), ".robotapp", "windows.properties");

    public void saveAllWindows(JDesktopPane desktopPane, JFrame mainFrame) {
        Properties props = new Properties();

        if (Files.exists(configPath)) {
            try (InputStream in = Files.newInputStream(configPath)) {
                props.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new WindowGeometry(mainFrame).saveTo(props, "main");

        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (!(frame instanceof SaveableWindow)) continue;

            String key = frame.getTitle().replaceAll("\\s+", "_");
            try {
                new WindowGeometry(frame).saveTo(props, key);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public void loadAllWindows(JDesktopPane desktopPane, JFrame mainFrame) {
        if (!Files.exists(configPath)) return;

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(configPath)) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        new WindowGeometry(props, "main").applyTo(mainFrame);

        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            String key = frame.getTitle().replaceAll("\\s+", "_");
            try {
                new WindowGeometry(props, key).applyTo(frame);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}