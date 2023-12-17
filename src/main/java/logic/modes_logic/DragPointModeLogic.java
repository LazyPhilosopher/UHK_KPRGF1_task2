package logic.modes_logic;

import logic.StructDataBase;
import struct.Point;

public class DragPointModeLogic {

    public DragPointModeLogic(){};

    public Point mouseClick(StructDataBase structures_db, Point dragged_point, int x, int y){
        if (dragged_point == null){
            dragged_point = structures_db.getClosestPoint(x, y, 5);
        }
        if (dragged_point != null) {
            structures_db.movePoint(dragged_point, x, y);
            dragged_point.setCoordinates(x, y);
        }
        return dragged_point;
    }
}
