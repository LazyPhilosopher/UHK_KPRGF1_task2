package rasterize;

import struct.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Common rasterizer for drawing all supported shapes.
 */
public class RasterizerUtils extends Rasterizer {
    public RasterizerUtils(Raster raster){
        super(raster);
    }

    /**
     * Get points laying on the line defined by coordinates.
     *
     * @param x1 source point X axis.
     * @param y1 source point Y axis.
     * @param x2 end point X axis.
     * @param y2 end point Y axis.
     * @return list of points.
     */
    public List<Point> getLinePoints(String mode, int x1, int y1, int x2, int y2){

        List<Point> out = new ArrayList<>();
        float k = (float) (y2 - y1) /(x2-x1);
        float q = y1 - k*x1;

        if ( Objects.equals(mode, "Y") || (Objects.equals(mode, null) && Math.abs(k) > 1)) {
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
        } else if (Objects.equals(mode, "X") || (Objects.equals(mode, null) && Math.abs(k) < 1)){
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
        return getLinePoints(null, x1, y1, x2, y2);
    }
    public List<Point> getYOrientedLinePoints(int x1, int y1, int x2, int y2){
        return getLinePoints("Y", x1, y1, x2, y2);
    }
    public List<Point> getXOrientedLinePoints(int x1, int y1, int x2, int y2){
        return getLinePoints("X", x1, y1, x2, y2);
    }

    public List<Point> getLinePoints(Point a, Point b){
        return getLinePoints(null, a.X(), a.Y(), b.X(), b.Y());
    }

    public List<Point> getYOrientedLinePoints(Point a, Point b){
        return getLinePoints("Y", a.X(), a.Y(), b.X(), b.Y());
    }
    public List<Point> getXOrientedLinePoints(Point a, Point b){
        return getLinePoints("X", a.X(), a.Y(), b.X(), b.Y());
    }
}
