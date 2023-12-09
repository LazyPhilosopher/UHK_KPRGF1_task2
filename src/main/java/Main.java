import gui.Window;
import utils.Controller.Controller2D;

import javax.swing.*;


/**
 * This is a basic painting and rasterization class.
 */
public class Main {

    /**
     * Main instance method.
     *
     * @param args Args passed by JVM. Not used, however.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Window window = new Window();
            new Controller2D(window.getPanel());
            window.setVisible(true);
        });
    }
}