package logic.modes_logic;

import gui.Panel;
import logic.StructDataBase;
import rasterize.StructRasterizer;
import struct.Line;
import struct.Point;
import struct.Polygon;
import struct.Struct;
import logic.Utils;

import java.util.ArrayList;
import java.util.List;

public class LineCropByRectangleModeLogic {
    StructRasterizer _struct_rasterizer;
    Utils _utils;

    public LineCropByRectangleModeLogic(Panel panel) {
        // rasterizer is required for drawing crop rectangle and calculating points on line.
        this._struct_rasterizer = new StructRasterizer(panel);
        this._utils = new Utils();
    }

    // Line crop with polygon mode mouse click routine.
    public void mouseClick(StructDataBase structure_db, int x, int y) {
        Point click_point = new Point(x, y);
        structure_db.addTempPoint(click_point);

        if (structure_db.getLastAddedPointStack().size() == 2) {
            Point center_point = structure_db.getLastAddedPointStack().get(0);
            Point bottom_right_point = structure_db.getLastAddedPointStack().get(1);
            Polygon crop_polygon = new Polygon(center_point, bottom_right_point);

            List<Struct> cropped_structs = new ArrayList<>();
            // Searching for points withing crop rectangle.
            for(Point point : structure_db.getPointStack()){
                if(_utils.isWithinRectangle(point.X(), point.Y(), crop_polygon)){
                    for (Struct related_struct : point.getRelatedStructs()) {
                        if (cropped_structs.contains(related_struct)) {
                            continue;
                        }
                        cropped_structs.add(related_struct);
                    }
                }
            }
            List<Point> crop_polygon_border = this._struct_rasterizer.polygon.get_polygon_pixels(crop_polygon);
            // search for pixel related to any structure overlapped by crop polygon pixels.
            for (Point crop_point : crop_polygon_border) {
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
                // at this point only line cropping feature is supported.
                if(struct instanceof Line){
                    Line line = (Line) struct;
                    List<Point> cross_points = linePolygonIntersections(line, crop_polygon);

                    // check if line start point is within crop polygon.
                    if(!this._utils.isWithinRectangle(line.start_point(), crop_polygon)){
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

                    // check if line end point is within crop polygon.
                    if(!this._utils.isWithinRectangle(line.end_point(), crop_polygon)){
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
                    // polygon cropping not implemented due to program architecture complications.
                }
            }
            structure_db.emptyTempPoints();
        }
    }

    // Line crop with polygon mode mouse move routine.
    public void mouseMoved(StructDataBase structures_db, int x, int y){
        if (structures_db.getLastAddedPointStack().size() == 1) {
            Point center_point = structures_db.getLastAddedPointStack().get(0);
            List<Point> polygon_vertices = new ArrayList<>();
            polygon_vertices.add(center_point);
            polygon_vertices.add(new Point(center_point.X(), y));
            polygon_vertices.add(new Point(x, y));
            polygon_vertices.add(new Point(x, center_point.Y()));
            this._struct_rasterizer.polygon.drawShallowPolygon(new Polygon(polygon_vertices), 0x00AA00);
        }
    }

    // Search for points laying both on line and polygon edge,
    private List<Point> linePolygonIntersections(Line line, Polygon polygon){
        List<Point> line_points = this._struct_rasterizer.line.getLinePoints(line);
        List<Point> polygon_points = this._struct_rasterizer.polygon.get_polygon_pixels(polygon);
        List<Point> cross_points = new ArrayList<>();
        for(Point line_point : line_points){
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
