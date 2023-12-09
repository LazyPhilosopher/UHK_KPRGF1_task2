package rasterize;

public class PointRasterizer extends CommonRasterizer {

    public PointRasterizer(Raster raster){
        super(raster);
    }

    public void drawPoint(int x1, int y1, int int_color){
        for (int delta = -5; delta < 6; delta++){
            this.raster.setPixel(x1+delta, y1, int_color);
            this.raster.setPixel(x1, y1+delta, int_color);
        }
    }
}
