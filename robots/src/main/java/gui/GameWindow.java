package gui;

import java.awt.*;

import javax.swing.*;

import movement.*;

import java.awt.Point;
import java.io.File;
import java.util.List;

public class GameWindow extends JInternalFrame implements SaveableWindow {
    private final GameVisualizer m_visualizer;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());

        JComboBox<MovementModeType> modeBox = new JComboBox<>(MovementModeType.values());

        modeBox.addActionListener(e -> {
            MovementModeType selected = (MovementModeType) modeBox.getSelectedItem();


            if (selected != MovementModeType.CUSTOM_MOUSE) {
                m_visualizer.disableCustomRouteMode();
            }

            switch (selected) {
                case MANUAL -> m_visualizer.setMovementMode(new ManualMovement());

                case PATROL_SQUARE -> m_visualizer.setMovementMode(new PatrolMovement(RouteFactory.squareRoute()));

                case PATROL_EIGHT -> {
                    List<Point> route = RouteFactory.figureEightRoute();
                    m_visualizer.setTargetPosition(route.get(0));
                    m_visualizer.setMovementMode(new PatrolMovement(route));
                }

                case CUSTOM_MOUSE -> m_visualizer.enableCustomRouteMode();

                case LOAD_FROM_FILE -> {
                    JFileChooser chooser = new JFileChooser();
                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        m_visualizer.loadCustomRouteFromFile(file);
                    }
                }
            }
        });


        panel.add(modeBox, BorderLayout.NORTH);
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}
