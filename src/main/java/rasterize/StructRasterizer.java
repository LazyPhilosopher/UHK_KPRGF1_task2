package rasterize;

import gui.Panel;
import rasterize.struct.*;

public class StructRasterizer {
    public PointRasterizer point;
    public LineRasterizer line;
    public PolygonRasterizer polygon;
    public EllipseRasterizer ellipse;
    public SeedFillRasterizer seed;

    public StructRasterizer(Panel panel){
        point = new PointRasterizer(panel.getRaster());
        line = new LineRasterizer(panel.getRaster());
        polygon = new PolygonRasterizer(panel.getRaster());
        ellipse = new EllipseRasterizer(panel.getRaster());
        seed = new SeedFillRasterizer(panel.getRaster());
    }
}
