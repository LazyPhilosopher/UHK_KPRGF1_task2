package gui.Canvas;

import java.util.EventObject;

public class CanvasEvent extends EventObject {
    private Object eventData;

    public CanvasEvent(Object source, Object eventData) {
        super(source);
        this.eventData = eventData;
    }

    public Object getEventData() {
        return eventData;
    }
}