package logic.modes_logic;
import logic.StructDataBase;

import struct.Point;

public class PointModeLogic{

    public PointModeLogic(){}

    // Point mode mouse click routine.
    public void mouseClick(StructDataBase structure_db, int x, int y){
        structure_db.addPoint(new Point(x, y));
    }
}
