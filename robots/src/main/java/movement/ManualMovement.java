package movement;

import gui.GameVisualizer;

public class ManualMovement implements MovementMode {
    @Override
    public void update(GameVisualizer visualizer) {
        visualizer.updateManual();
    }
}
