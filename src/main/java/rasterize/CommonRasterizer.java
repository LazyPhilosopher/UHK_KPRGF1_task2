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
     * @param dot_size dotted line step length.
     * @param x1 source point X axis.
     * @param y1 source point Y axis.
     * @param x2 end point X axis.
     * @param y2 end point Y axis.
     * @return list of points.
     */
    public List<Point> getLinePoints(boolean forced_y_axis, int dot_size, int x1, int y1, int x2, int y2){

        List<Point> out = new ArrayList<>();
        float k = (float) (y2 - y1) /(x2-x1);
        float q = y1 - k*x1;

        if (Math.abs(k) >= 1 || forced_y_axis) {
            // Y-axis oriented
            int max_y = Math.max(y1, y2);
            int min_y = Math.min(y1, y2);
            for (int i = min_y; i <= max_y; i++) {
                if (dot_size != -1 && i % (2*dot_size) < dot_size){
                   continue;
                }
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
                if (dot_size != -1 && i % (2*dot_size) < dot_size){
                    continue;
                }
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

    /**Draw dotted line without forced y-axis preference. */
    public List<Point> getLinePoints(int dot_size, int x1, int y1, int x2, int y2){
        return getLinePoints(false, dot_size, x1, y1, x2, y2);
    }

    /**Draw solid line without forced y-axis preference. */
    public List<Point> getLinePoints(int x1, int y1, int x2, int y2){
        return getLinePoints(false, -1, x1, y1, x2, y2);
    }

    /**Draw solid line with forced y-axis preference. */
    public List<Point> getLinePoints(boolean forced_y_axis, int x1, int y1, int x2, int y2){
        return getLinePoints(forced_y_axis, -1, x1, y1, x2, y2);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        List<Point> points = getLinePoints(x1, y1, x2, y2);
        for (Point point : points) {
            this.raster.setPixel(point.X(), point.Y(), color.getRGB());
        }
    }

    public void drawDottedLine(int dot_size, int x1, int y1, int x2, int y2, Color color) {
        List<Point> points = getLinePoints(dot_size, x1, y1, x2, y2);
        for (Point point : points) {
            this.raster.setPixel(point.X(), point.Y(), color.getRGB());
        }
    }

    public void drawPoint(int x1, int y1, int int_color){
        for (int delta = -5; delta < 6; delta++){
            this.raster.setPixel(x1+delta, y1, int_color);
            this.raster.setPixel(x1, y1+delta, int_color);
        }
    }

    public void drawPolygon(Polygon polygon, int int_color) {
        List<Point> polygon_vertices = new ArrayList<>(polygon.getVertices());
        polygon_vertices.add(polygon_vertices.get(0));

        List<Point> border_pixels = new ArrayList<>();
        for (int i = 0; i < polygon_vertices.size()-1; i++){
            Point a = polygon_vertices.get(i);
            Point b = polygon_vertices.get(i+1);
            border_pixels.addAll(getLinePoints(true, a.X(), a.Y(), b.X(), b.Y()));
        }
        Map<Integer, List<Integer>> pixels_on_y_axis = new HashMap<>();
        for (Point pixel : border_pixels){
            if (!pixels_on_y_axis.containsKey(pixel.Y())){
                pixels_on_y_axis.put(pixel.Y(), new ArrayList<>());
            }
            pixels_on_y_axis.get(pixel.Y()).add(pixel.X());
        }
        for (int i = 0; i < polygon_vertices.size()-1; i++){
            Point a = polygon_vertices.get(i);
            int idx = pixels_on_y_axis.get(a.Y()).indexOf(a.X());
            if (idx != -1) {
//                if(pixels_on_y_axis.get(a.Y()).contains(a.X()+1) || pixels_on_y_axis.get(a.Y()).contains(a.X()-1)) {
                pixels_on_y_axis.get(a.Y()).remove(idx);
//                }
            }
        }
        for (int y : pixels_on_y_axis.keySet()){
            Collections.sort(pixels_on_y_axis.get(y));
        }
        for (Integer key : pixels_on_y_axis.keySet()) {
            Integer leftest_x = null;
            // avoid concurrent modification by using copy of array
            for (Integer pixel_x : new ArrayList<>(pixels_on_y_axis.get(key))){
                if (leftest_x == null) {
                    leftest_x = pixel_x;
                } else if(pixels_on_y_axis.get(key).contains(pixel_x+1)){
                    pixels_on_y_axis.get(key).remove(pixel_x);
                } else {
                    leftest_x = null;
                }
            }
        }

        for (Integer key : pixels_on_y_axis.keySet()){
            int y = key;

            if (pixels_on_y_axis.get(key).size()%2 == 0){
                for (int idx = 0; idx < pixels_on_y_axis.get(key).size(); idx += 2){
                    int x1 = pixels_on_y_axis.get(key).get(idx);
                    int x2 = pixels_on_y_axis.get(key).get(idx+1);
                    drawLine(x1, y, x2, y, new Color(int_color));
                }
            }
        }
        for (int i = 0; i < polygon_vertices.size()-1; i++){
            Point a = polygon_vertices.get(i);
            Point b = polygon_vertices.get(i+1);
            drawLine(a.X(), a.Y(), b.X(), b.Y(), new Color(0xAAAAAA));
        }
    }

    public void clear(){
        raster.clear();
    }
}
