package rasterize;

import rasterize.CommonRasterizer.*;

import struct.Point;
import struct.Polygon;

import java.awt.*;
import java.util.*;
import java.util.List;


public class PolygonRasterizer extends CommonRasterizer  {

    public PolygonRasterizer(Raster raster){
        super(raster);
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
                    for(Point point : getLinePoints(x1, y, x2, y)){raster.setPixel(point.X(), point.Y(), int_color);}
                }
            }
        }
        for (int i = 0; i < polygon_vertices.size()-1; i++){
            Point a = polygon_vertices.get(i);
            Point b = polygon_vertices.get(i+1);
            for(Point point : getLinePoints(a, b)){raster.setPixel(point.X(), point.Y(), 0xAAAAAA);}
        }
    }
}
