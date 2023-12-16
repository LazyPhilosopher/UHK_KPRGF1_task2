package logic.modes;
import logic.ModelDataBase;

import gui.Panel;
import rasterize.struct.PointRasterizer;
import struct.Point;

public class PointModeLogic{

    public PointModeLogic(){}

    public void mouseClick(ModelDataBase structure_db, int x, int y){
        structure_db.addPoint(new Point(x, y));
    }
}
