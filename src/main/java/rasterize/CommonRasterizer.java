package rasterize;

import struct.Point;
import struct.Polygon;

import java.awt.*;
import java.util.List;
import java.util.*;


/**
 * Common rasterizer for drawing all supported shapes.
 */
public class CommonRasterizer extends Rasterizer {
    public CommonRasterizer(Raster raster){
        super(raster);
    }

    /**
     * Get points laying on the line defined by coordinates.
     * @param forced_y_axis only single active pixel on each Y-axis row.
     * @param x1 source point X axis.
     * @param y1 source point Y axis.
     * @param x2 end point X axis.
     * @param y2 end point Y axis.
     * @return list of points.
     */
    public List<Point> getLinePoints(boolean forced_y_axis, int x1, int y1, int x2, int y2){

        List<Point> out = new ArrayList<>();
        float k = (float) (y2 - y1) /(x2-x1);
        float q = y1 - k*x1;

        if (Math.abs(k) >= 1 || forced_y_axis) {
            // Y-axis oriented
            int max_y = Math.max(y1, y2);
            int min_y = Math.min(y1, y2);
            for (int i = min_y; i <= max_y; i++) {
                float temp_x = (i - q) / k;
                if(temp_x != temp_x){
                    // temp_x i NaN
                    temp_x = x1;
                }
                out.add(new Point((int) temp_x, i));
            }
        } else {
            // X-axis oriented
            int max_x = Math.max(x1, x2);
            int min_x = Math.min(x1, x2);
            for (int i = min_x; i <= max_x; i++) {
                float temp_y = k * i + q;
                if(temp_y != temp_y){
                    // temp_x i NaN
                    temp_y = y1;
                }
                out.add(new Point(i, (int) temp_y));
            }
        }
        return out;
    }

    /**Draw solid line without forced y-axis preference. */
    public List<Point> getLinePoints(int x1, int y1, int x2, int y2){
        return getLinePoints(false, x1, y1, x2, y2);
    }

    public List<Point> getLinePoints(Point a, Point b){
        return getLinePoints(false, a.X(), a.Y(), b.X(), b.Y());
    }


    public void clear(){
        raster.clear();
    }
}
