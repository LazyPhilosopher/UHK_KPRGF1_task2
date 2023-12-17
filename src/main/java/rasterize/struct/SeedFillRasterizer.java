package rasterize.struct;

import rasterize.Raster;
import rasterize.RasterizerUtils;

public class SeedFillRasterizer extends RasterizerUtils {
    public SeedFillRasterizer(Raster raster){
        super(raster);
    }

    public void boundaryFill(int x, int y, int new_color, int base_color){
//        if (depth <=0){
//            return;
//        }

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
    }
}
