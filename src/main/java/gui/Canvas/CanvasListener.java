package gui.Canvas;

import java.util.EventListener;


public interface CanvasListener extends EventListener {
    void canvasClicked(CanvasEvent event);
}