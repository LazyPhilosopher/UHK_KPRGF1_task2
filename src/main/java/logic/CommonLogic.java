package logic;

import rasterize.struct.PolygonRasterizer;
import struct.Line;
import struct.Point;
import struct.Polygon;
import rasterize.CommonRasterizer;

import java.util.ArrayList;
import java.util.List;

public class CommonLogic {

    // Check if mouse-click is within rectangle.
    public boolean isWithinRectangle(int click_x, int click_y, Polygon rectangle){
        int rectangle_max_x = rectangle.getVertices().stream()
                .mapToInt(Point::X)
                .max()
                .orElse(0);
        int rectangle_min_x = rectangle.getVertices().stream()
                .mapToInt(Point::X)
                .min()
                .orElse(0);
        int rectangle_max_y = rectangle.getVertices().stream()
                .mapToInt(Point::Y)
                .max()
                .orElse(0);
        int rectangle_min_y = rectangle.getVertices().stream()
                .mapToInt(Point::Y)
                .min()
                .orElse(0);

        if(!(rectangle_min_x <= click_x && click_x <= rectangle_max_x)){
            return false;
        } else if(!(rectangle_min_y <= click_y && click_y <= rectangle_max_y)){
            return false;
        }
        return true;
    }
}
