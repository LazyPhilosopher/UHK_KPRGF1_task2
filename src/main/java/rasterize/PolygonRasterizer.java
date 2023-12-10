package rasterize;

import rasterize.CommonRasterizer.*;

import struct.Point;
import struct.Polygon;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class PolygonRasterizer extends CommonRasterizer  {

    public PolygonRasterizer(Raster raster){
        super(raster);
    }

    public void drawPolygon(Polygon polygon, int int_color) {
        List<Point> polygon_vertices = new ArrayList<>(polygon.getVertices());
        polygon_vertices.add(polygon_vertices.get(0));

        // getting polygon Y-axis oriented border pixels
        // no stacked pixels on Y axis
        List<Point> border_pixels = new ArrayList<>();
        for (int i = 0; i < polygon_vertices.size()-1; i++){
            Point a = polygon_vertices.get(i);
            Point b = polygon_vertices.get(i+1);
            border_pixels.addAll(getYOrientedLinePoints(a.X(), a.Y(), b.X(), b.Y()));
//            border_pixels.remove(b);
        }
        Map<Integer, List<Integer>> pixels_on_y_axis = new HashMap<>();
        for (Point pixel : border_pixels){
            if (!pixels_on_y_axis.containsKey(pixel.Y())){
                pixels_on_y_axis.put(pixel.Y(), new ArrayList<>());
            }
//            if ( pixels_on_y_axis.get(pixel.Y()).contains(pixel.X())){
//                continue;
//            }
            pixels_on_y_axis.get(pixel.Y()).add(pixel.X());
        }
//        for (int i = 0; i < polygon_vertices.size()-1; i++){
//            Point a = polygon_vertices.get(i);
//            int idx = pixels_on_y_axis.get(a.Y()).indexOf(a.X());
//            if (idx != -1) {
//                pixels_on_y_axis.get(a.Y()).remove(idx);
//            }
//        }
        for (int y : pixels_on_y_axis.keySet()){
            Collections.sort(pixels_on_y_axis.get(y));
        }

        // ---------------------------------------------------
        // getting polygon X-axis oriented border pixels
        // no stacked pixels on X axis
        border_pixels = new ArrayList<>();
        for (int i = 0; i < polygon_vertices.size()-1; i++){
            Point a = polygon_vertices.get(i);
            Point b = polygon_vertices.get(i+1);
            border_pixels.addAll(getXOrientedLinePoints(a.X(), a.Y(), b.X(), b.Y()));
        }
        Map<Integer, List<Integer>> pixels_on_x_axis = new HashMap<>();
        for (Point pixel : border_pixels){
            if (!pixels_on_x_axis.containsKey(pixel.X())){
                pixels_on_x_axis.put(pixel.X(), new ArrayList<>());
            }
//            if ( pixels_on_x_axis.get(pixel.X()).contains(pixel.Y())){
//                continue;
//            }
            pixels_on_x_axis.get(pixel.X()).add(pixel.Y());

        }
        for (int x : pixels_on_x_axis.keySet()){
            Collections.sort(pixels_on_x_axis.get(x));
        }
        // ---------------------------------------------------

        for (Integer key : pixels_on_y_axis.keySet()){
            int y = key;

            if (pixels_on_y_axis.get(key).size() > 0){
                for (int idx = 1; idx < pixels_on_y_axis.get(key).size(); idx += 1){
                    int x1 = pixels_on_y_axis.get(key).get(idx-1);
                    int x2 = pixels_on_y_axis.get(key).get(idx);
//                    if ((idx%2 == 1 && pixels_on_y_axis.get(key).size()-idx %2 == 1)){
//                        for(Point point : getLinePoints(x1, y, x2, y)){raster.setPixel(point.X(), point.Y(), int_color);}
//                        continue;
//                    }
//                    for(Point point : getLinePoints(x1, y, x2, y)){raster.setPixel(point.X(), point.Y(), int_color);}

                    int mid_x = (int)(x1+x2)/2;

                    if(pixels_on_x_axis.get(mid_x) == null){
                        continue;
                    }

                    Map<Boolean, List<Integer>> XpartitionedLists = pixels_on_x_axis.get(mid_x).stream()
                            .collect(Collectors.partitioningBy(value -> value < key));

                    List<Integer> smallerThanKeyList = XpartitionedLists.get(true);
                    List<Integer> greaterOrEqualToKeyList = XpartitionedLists.get(false);

                    Map<Boolean, List<Integer>> YpartitionedLists = pixels_on_y_axis.get(key).stream()
                            .collect(Collectors.partitioningBy(value -> value < mid_x));

                    List<Integer> smallerThanMidXList = YpartitionedLists.get(true);
                    List<Integer> greaterOrEqualToMidXList = YpartitionedLists.get(false);


                    if(smallerThanMidXList.size()%2 == 1 && greaterOrEqualToMidXList.size()%2 == 1){
                        for (Point point : getLinePoints(x1, y, x2, y)) {
                            raster.setPixel(point.X(), point.Y(), int_color);
                        }
                    }
                    if(smallerThanKeyList.size()%2 == 1 && greaterOrEqualToKeyList.size()%2 == 1){
                        for (Point point : getLinePoints(x1, y, x2, y)) {
                            raster.setPixel(point.X(), point.Y(), int_color);
                        }
                    }
//                    raster.setPixel(mid_x, key, 0xFF0000);
                }
            }
        }
        for (int i = 0; i < polygon_vertices.size()-1; i++){
            Point a = polygon_vertices.get(i);
            Point b = polygon_vertices.get(i+1);
            for(Point point : getLinePoints( a, b)){raster.setPixel(point.X(), point.Y(), 0xFFFFFF);}
        }
    }
}
