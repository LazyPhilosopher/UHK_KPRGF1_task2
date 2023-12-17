package logic.modes_logic;

import logic.StructDataBase;
import struct.Point;
import struct.Polygon;

public class PolygonModeLogic {
    public PolygonModeLogic() {
    }
//      Polygon mode mouse click routine.
    public void mouseClick(StructDataBase structures_db, int x, int y){
        Point closest_point = structures_db.getClosestPoint(x, y, 5);
        if (structures_db.getLastAddedPointStack().size() > 2 && structures_db.getLastAddedPointStack().contains(closest_point)){
            structures_db.addPolygon(new Polygon(structures_db.getLastAddedPointStack()));
            structures_db.emptyTempPoints();
        } else if (closest_point != null) {
            structures_db.addTempPoint(closest_point);
        } else {
            Point new_point = new Point(x, y);
            structures_db.addPoint(new_point);
        }
    }
}
