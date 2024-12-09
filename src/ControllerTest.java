package src;

public class ControllerTest {
    public static void main(String[] args) {
        // Set native library path
        String userDir = System.getProperty("user.dir");
        System.setProperty("net.java.games.input.librarypath", userDir + "/natives/windows");

        ControllerInput controller = new ControllerInput();

        if (controller.isControllerPresent()) {
            System.out.println("Controller found! Testing input...");

            // Poll controller for 10 seconds
            long endTime = System.currentTimeMillis() + 10000;
            while (System.currentTimeMillis() < endTime) {
                ControllerInput.ControllerState state = controller.pollController();

                if (Math.abs(state.leftStickX) > 0.1 || Math.abs(state.leftStickY) > 0.1) {
                    System.out.printf("Stick position: X=%.2f, Y=%.2f%n",
                            state.leftStickX, state.leftStickY);
                }

                if (state.shootButton)
                    System.out.println("Shoot button pressed!");
                if (state.passButton)
                    System.out.println("Pass button pressed!");
                if (state.tackleButton)
                    System.out.println("Tackle button pressed!");
                if (state.switchPlayerButton)
                    System.out.println("Switch player button pressed!");

                try {
                    Thread.sleep(16); // Poll at roughly 60Hz
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No controller found!");
        }
    }
}
