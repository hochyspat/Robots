package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RouteKeyHandler {
    private final GameVisualizer visualizer;
    private final InputMap inputMap;
    private final ActionMap actionMap;

    public RouteKeyHandler(GameVisualizer visualizer) {
        this.visualizer = visualizer;
        this.inputMap = visualizer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        this.actionMap = visualizer.getActionMap();
        bindKeys();
    }

    private void bindKeys() {
        bindEnterKey();
        bindSpaceKey();
        bindResetKey();
    }

    private void bindEnterKey() {
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "startCustomRoute");
        actionMap.put("startCustomRoute", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (visualizer.isInCustomRouteInput() && !visualizer.getCustomRoute().isEmpty()) {
                    visualizer.setTargetPosition(visualizer.getCustomRoute().get(0));
                    visualizer.setMovementMode(new movement.CustomRouteMovement(visualizer.getCustomRoute()));
                    visualizer.setInCustomRouteInput(false);
                    visualizer.setCustomRouteActive(true);
                }
            }
        });
    }

    private void bindSpaceKey() {
        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "pauseOrResume");
        actionMap.put("pauseOrResume", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (visualizer.isCustomRouteActive()) {
                    visualizer.setRoutePaused(!visualizer.isRoutePaused());
                    visualizer.setInCustomRouteInput(
                            visualizer.isRoutePaused() && !visualizer.isLoadedFromFile()
                    );
                }
            }
        });
    }

    private void bindResetKey() {
        inputMap.put(KeyStroke.getKeyStroke("R"), "resetCustomRoute");
        actionMap.put("resetCustomRoute", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizer.enableCustomRouteMode();
            }
        });
    }
}
