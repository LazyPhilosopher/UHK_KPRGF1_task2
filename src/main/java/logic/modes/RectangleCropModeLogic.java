package logic.modes;

import gui.Panel;
import logic.ModelDataBase;
import rasterize.struct.LineRasterizer;
import rasterize.struct.PolygonRasterizer;
import struct.Line;
import struct.Point;
import struct.Polygon;
import struct.Struct;
import logic.CommonLogic;

import java.util.ArrayList;
import java.util.List;

public class RectangleCropModeLogic {
    PolygonRasterizer _polygon_rasterizer;
    LineRasterizer _line_rasterizer;
    CommonLogic _common_logic;

    public RectangleCropModeLogic(Panel panel) {
        this._polygon_rasterizer = new PolygonRasterizer(panel.getRaster());
        this._line_rasterizer = new LineRasterizer(panel.getRaster());
        this._common_logic = new CommonLogic();
    }

    public void mouseClick(ModelDataBase structure_db, int x, int y) {
        Point click_point = new Point(x, y);
        structure_db.addTempPoint(click_point);


        if (structure_db.getLastAddedPointStack().size() == 2) {
            Point center_point = structure_db.getLastAddedPointStack().get(0);
            Point bottom_right_point = structure_db.getLastAddedPointStack().get(1);

            List<Point> polygon_vertices = new ArrayList<>();
            polygon_vertices.add(center_point);
            polygon_vertices.add(new Point(center_point.X(), bottom_right_point.Y()));
            polygon_vertices.add(new Point(bottom_right_point.X(), bottom_right_point.Y()));
            polygon_vertices.add(new Point(bottom_right_point.X(), center_point.Y()));
            Polygon crop_polygon = new Polygon(polygon_vertices);

            List<Struct> cropped_structs = new ArrayList<>();
            for(Point point : structure_db.getPointStack()){
                if(_common_logic.isWithinRectangle(point.X(), point.Y(), crop_polygon)){
                    for (Struct related_struct : point.getRelatedStructs()) {
                        if (cropped_structs.contains(related_struct)) {
                            continue;
                        }
                        cropped_structs.add(related_struct);
                    }
                }
            }
            List<Point> crop_points = this._polygon_rasterizer.get_polygon_pixels(crop_polygon);
            for (Point crop_point : crop_points) {
                List<Point> pixels = structure_db.getPixelFromCoordinatedStack(crop_point.X(), crop_point.Y());
                if (pixels != null) {
                    for (Point pixel : pixels) {
                        for (Struct related_struct : pixel.getRelatedStructs()) {
                            if (cropped_structs.contains(related_struct)) {
                                continue;
                            }
                            cropped_structs.add(related_struct);
                        }
                    }
                }
            }

            for (Struct struct : cropped_structs){
                if(struct instanceof Line){
                    Line line = (Line) struct;
                    List<Point> cross_points = linePolygonIntersections(line, crop_polygon);

                    if(!this._common_logic.isWithinRectangle(line.start_point().X(), line.start_point().Y(), crop_polygon)){
                        for (Point cross_point : cross_points){
                            Line new_line = new Line(line.start_point(), cross_point);
                            if(linePolygonIntersections(new_line, crop_polygon).size() < 2){
                                structure_db.addPoint(cross_point);
                                structure_db.addLine(new_line);
                                break;
                            }
                        }
                    } else {
                        structure_db.deletePoint(line.start_point());
                    }


                    if(!this._common_logic.isWithinRectangle(line.end_point().X(), line.end_point().Y(), crop_polygon)){
                        for (Point cross_point : cross_points){
                            Line new_line = new Line(line.end_point(), cross_point);
                            if(linePolygonIntersections(new_line, crop_polygon).size() < 2){
                                structure_db.addPoint(cross_point);
                                structure_db.addLine(new_line);
                                break;
                            }
                        }
                    } else {
                        structure_db.deletePoint(line.end_point());
                    }
                    structure_db.deleteLine(line);
                }
                else if(struct instanceof Point){
                    System.out.println("Polygon");
                }
            }
            structure_db.emptyTempPoints();
        }
    }

    public void mouseMove(ModelDataBase structures_db, int x, int y){
        if (structures_db.getLastAddedPointStack().size() == 1) {
            Point center_point = structures_db.getLastAddedPointStack().get(0);
            List<Point> polygon_vertices = new ArrayList<>();
            polygon_vertices.add(center_point);
            polygon_vertices.add(new Point(center_point.X(), y));
            polygon_vertices.add(new Point(x, y));
            polygon_vertices.add(new Point(x, center_point.Y()));
            this._polygon_rasterizer.drawPolygon(new Polygon(polygon_vertices), 0x00AA00);
        }
    }

    private List<Point> linePolygonIntersections(Line line, Polygon polygon){
        List<Point> line_points = _line_rasterizer.getLinePoints(line);
        List<Point> polygon_points = _polygon_rasterizer.get_polygon_pixels(polygon);
        List<Point> cross_points = new ArrayList<>();
        for(Point line_point : line_points){
//            if(Math.abs(line_point.X() - line.start_point().X()) < 3 && Math.abs(line_point.Y() - line.start_point().Y()) < 3 ){
//                continue;
//            }
//            if(Math.abs(line_point.X() - line.end_point().X()) < 3 && Math.abs(line_point.Y() - line.end_point().Y()) < 3 ){
//                continue;
//            }
            for(Point polygon_point : polygon_points){
                if(polygon_point.X() == line_point.X() && polygon_point.Y() == line_point.Y()){
                    boolean not_a_double_intersection = true;
                    for(Point cross_point : cross_points){
                        if (Math.abs(line_point.X() - cross_point.X()) < 3 && Math.abs(line_point.Y() - cross_point.Y()) < 3 ){
                            not_a_double_intersection = false;
                            break;
                        }
                    }
                    if (not_a_double_intersection){
                        cross_points.add(line_point);
                    }
                    break;
                }
            }
        }
        return cross_points;
    }
}
