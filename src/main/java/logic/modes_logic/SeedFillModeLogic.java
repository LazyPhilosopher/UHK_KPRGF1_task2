package logic.modes_logic;

import gui.Panel;
import rasterize.Raster;
import rasterize.struct.SeedFillRasterizer;

public class SeedFillModeLogic {
    SeedFillRasterizer seed_fill;
    Raster raster;

    public SeedFillModeLogic(Panel panel){
        this.raster = panel.getRaster();
        this.seed_fill = new SeedFillRasterizer(this.raster);
    }

    public void mouseClicked(int x, int y){
        int base_color = raster.getPixel(x, y);
        this.seed_fill.boundaryFill(x, y, 0xF700FF, base_color);
    }
}
