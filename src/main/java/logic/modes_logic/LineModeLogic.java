package logic.modes_logic;

import gui.Panel;
import logic.StructDataBase;
import rasterize.struct.LineRasterizer;
import struct.Line;
import struct.Point;

import java.awt.Color;


public class LineModeLogic {
    // Drag-point mode mouse click routine.
    // Draws dotted line to mouse cursor location.
    private LineRasterizer _line_rasterizer;

    public LineModeLogic(Panel panel){
        this._line_rasterizer = new LineRasterizer(panel.getRaster());
    }

    public void setLineRasterizer(LineRasterizer new_line_rasterizer){
        this._line_rasterizer = new_line_rasterizer;
    }

    public void mouseClick(StructDataBase structures_db, boolean shift_pressed, int x, int y){
        if (shift_pressed) {
            if (structures_db.getLastAddedPointStack().size() == 1) {
                Point start_point = structures_db.getTempPoint(0);
                int delta_x = Math.abs(start_point.X() - x);
                int delta_y = Math.abs(start_point.Y() - y);
                if (delta_x < delta_y && delta_x < 15) {
                    x = start_point.X();
                }
                if (delta_x > delta_y && delta_y < 15) {
                    y = start_point.Y();
                }
            }
        }
        Point point = new Point(x, y);
        // Search for already existing closest point.
        Point closest_point = structures_db.getClosestPoint(x ,y, 5);
        if (closest_point != null){
            point = closest_point;
            structures_db.addTempPoint(point);
        } else {
            // if no point nearby cursor exists - create new one.
            structures_db.addPoint(point);
        }

        // if single point already present in last added point stack - create a line instance.
        if(structures_db.getLastAddedPointStack().size() >= 2){
            Point point1 = structures_db.popLastAddedPoint(0);
            Point point2 = structures_db.popLastAddedPoint(0);
            structures_db.addLine(new Line(point1, point2));
        }
    }

    public void mouseMoved(StructDataBase structures_db, boolean shift_pressed, int x, int y){
        drawDottedLine(structures_db, shift_pressed, x, y);
    }

    private void drawDottedLine(StructDataBase structures_db, boolean shift_pressed, int x, int y){
        if(structures_db.getLastAddedPointStack().size() == 1){
            Point start_point = structures_db.getTempPoint(0);
            if (shift_pressed) {
                if (structures_db.getLastAddedPointStack().size() == 1) {
                    int delta_x = Math.abs(start_point.X() - x);
                    int delta_y = Math.abs(start_point.Y() - y);
                    if(delta_x < delta_y && delta_x < 15) {
                        x = start_point.X();
                    }
                    if(delta_x > delta_y && delta_y < 15) {
                        y = start_point.Y();
                    }
                }
            }
            _line_rasterizer.drawDottedLine(5, start_point.X(), start_point.Y(), x, y, new Color(0x00FF00));
        }
    }
}
