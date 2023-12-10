package rasterize;

import struct.Point;

import java.awt.*;
import java.util.List;


public class SeedFillRasterizer extends CommonRasterizer{
    public SeedFillRasterizer(Raster raster){
        super(raster);
    }

    public void seedFill(int x, int y){
        int base_color = raster.getPixel(x, y-5);
        boundaryFill(x, y, 0x123456, base_color);

    }

    public void boundaryFill(int x, int y, int new_color, int base_color){

        if(0 > x || x > raster.getWidth()-1){
            return;
        }

        if(0 > y || y > raster.getHeight()-1){
            return;
        }

        if(raster.getPixel(x, y) != base_color) {
            return;
        }

        raster.setPixel(x, y, new_color);
        boundaryFill(x + 1, y, new_color, base_color);
        boundaryFill(x - 1, y, new_color, base_color);
        boundaryFill(x, y + 1, new_color, base_color);
        boundaryFill(x, y - 1, new_color, base_color);
        System.out.println("Seed!");

    }
}
