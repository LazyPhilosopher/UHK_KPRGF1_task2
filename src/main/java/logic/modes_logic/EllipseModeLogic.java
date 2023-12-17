package logic.modes_logic;


import gui.Panel;
import logic.StructDataBase;
import rasterize.StructRasterizer;
import struct.Ellipse;
import struct.Point;
import struct.Polygon;

public class EllipseModeLogic {
    private final StructRasterizer _struct_rasterizer;


    public EllipseModeLogic(Panel panel){
        // rasterizer is required for drawing proposed ellipse within rectangle.
        this._struct_rasterizer = new StructRasterizer(panel);
    }

    // Ellipse mode mouse click routine.
    public void mouseClick(StructDataBase structure_db, int x, int y){
        if (structure_db.getLastAddedPointStack().size() == 0){
            Point point = new Point(x,y);
            structure_db.addTempPoint(point);
        } else if (structure_db.getLastAddedPointStack().size() == 1) {
            Point first_click_point = structure_db.popLastAddedPoint(0);
            Point new_click_point = new Point(x,y);
            Polygon ellipse_polygon = new Polygon(first_click_point, new_click_point);

            Point center = ellipse_polygon.getMiddlePoint();
            int minor_axis = (ellipse_polygon.getRightUpPoint().X() - ellipse_polygon.getLeftUpPoint().X())/2;
            int major_axis = (ellipse_polygon.getBottomLeftPoint().Y() - ellipse_polygon.getLeftUpPoint().Y())/2;

            Point minor_axis_point = new Point(center.X()+minor_axis, center.Y());
            Point major_axis_point = new Point(center.X(), center.Y()-major_axis);

            structure_db.addPoint(center);
            structure_db.addPoint(major_axis_point);
            structure_db.addPoint(minor_axis_point);
            structure_db.addEllipse(new Ellipse(center, major_axis_point, minor_axis_point));
            structure_db.emptyTempPoints();
        }
    }

    // Ellipse mode mouse move routine.
    public void mouseMoved(StructDataBase structure_db, int x, int y){
        if (structure_db.getLastAddedPointStack().size() == 1) {
            Point first_click_point = structure_db.getLastAddedPointStack().get(0);
            Point new_point = new Point(x,y);
            Polygon ellipse_polygon = new Polygon(first_click_point, new_point);

            Point center = ellipse_polygon.getMiddlePoint();
            int minor_axis = (ellipse_polygon.getRightUpPoint().X() - ellipse_polygon.getLeftUpPoint().X())/2;
            int major_axis = (ellipse_polygon.getBottomLeftPoint().Y() - ellipse_polygon.getLeftUpPoint().Y())/2;

            Point minor_axis_point = new Point(center.X()+minor_axis, center.Y());
            Point major_axis_point = new Point(center.X(), center.Y()-major_axis);

            // drawing proposed ellipse within rectangle.
            this._struct_rasterizer.ellipse.drawEllipse(new Ellipse(center, major_axis_point, minor_axis_point));
            this._struct_rasterizer.polygon.drawShallowPolygon(ellipse_polygon, 0xFFFFFF);
            this._struct_rasterizer.point.drawPoint(center, 0xFF0000);
            this._struct_rasterizer.point.drawPoint(major_axis_point, 0xFF0000);
            this._struct_rasterizer.point.drawPoint(minor_axis_point, 0xFF0000);
        }
    }
}
