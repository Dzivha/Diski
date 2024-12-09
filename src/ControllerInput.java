package src;

import net.java.games.input.*;
import net.java.games.input.Component.Identifier;

public class ControllerInput {
    private Controller gameController;
    private float deadzone = 0.15f; // Ignore small stick movements
    private boolean isInitialized = false;

    public ControllerInput() {
        String userDir = System.getProperty("user.dir");
        System.setProperty("net.java.games.input.librarypath", userDir + "/natives/windows");
        initializeController();
    }

    private void initializeController() {
        // Get all controllers
        ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
        Controller[] controllers = ce.getControllers();

        // Find the first gamepad/controller
        for (Controller controller : controllers) {
            if (controller.getType() == Controller.Type.GAMEPAD ||
                    controller.getType() == Controller.Type.STICK) {
                gameController = controller;
                isInitialized = true;
                System.out.println("Found controller: " + controller.getName());
                break;
            }
        }

        if (!isInitialized) {
            System.out.println("No game controller found. Using keyboard controls only.");
        }
    }

    public boolean isControllerPresent() {
        return isInitialized && gameController != null;
    }

    public ControllerState pollController() {
        if (!isInitialized || gameController == null) {
            return new ControllerState();
        }

        gameController.poll();
        EventQueue queue = gameController.getEventQueue();
        Event event = new Event();

        ControllerState state = new ControllerState();

        // Get all components' current values
        for (Component component : gameController.getComponents()) {
            float value = component.getPollData();

            // Handle analog sticks (with deadzone)
            if (component.getIdentifier() == Identifier.Axis.X) {
                if (Math.abs(value) > deadzone) {
                    state.leftStickX = value;
                }
            } else if (component.getIdentifier() == Identifier.Axis.Y) {
                if (Math.abs(value) > deadzone) {
                    state.leftStickY = value;
                }
            }

            // Handle buttons
            // Circle button (Shoot)
            if (component.getIdentifier() == Component.Identifier.Button._1) {
                state.shootButton = value > 0.5f;
            }
            // Cross button (Pass)
            else if (component.getIdentifier() == Component.Identifier.Button._0) {
                state.passButton = value > 0.5f;
            }
            // Square button (Tackle)
            else if (component.getIdentifier() == Component.Identifier.Button._2) {
                state.tackleButton = value > 0.5f;
            }
            // Triangle button (Switch Player)
            else if (component.getIdentifier() == Component.Identifier.Button._3) {
                state.switchPlayerButton = value > 0.99f;
            }
        }

        return state;
    }

    public static class ControllerState {
        public float leftStickX = 0;
        public float leftStickY = 0;
        public boolean shootButton = false;
        public boolean passButton = false;
        public boolean tackleButton = false;
        public boolean switchPlayerButton = false;
    }
}