package rasterize;

import struct.Point;
import struct.Ellipse;


public class EllipseRasterizer extends CommonRasterizer{

    public EllipseRasterizer(Raster raster){
        super(raster);
    }

    // Midpoint Ellipse Algorithm
    public void drawEllipse(Ellipse ellipse) {
        Point center = ellipse.center_point();
        int center_x = center.X();
        int center_y = center.Y();
        int semi_minor = ellipse.semi_minor();
        int semi_major = ellipse.semi_major();

        int x = 0;
        int y = semi_minor;

        // Initial decision parameter of region 1
        float d1 = (semi_minor * semi_minor) - (semi_major * semi_major * semi_minor) +
                (0.25f * semi_major * semi_major);
        float dx = 0;
        float dy = 2 * semi_major * semi_major * y;

        // For region 1
        while (dx < dy)
        {
            plotPoints(center_x, center_y, x, y);

            // Checking and updating value of
            // decision parameter based on algorithm
            if (d1 < 0)
            {
                x++;
                dx = dx + (2 * semi_minor * semi_minor);
                d1 = d1 + dx + (semi_minor * semi_minor);
            }
            else
            {
                x++;
                y--;
                dx = dx + (2 * semi_minor * semi_minor);
                dy = dy - (2 * semi_major * semi_major);
                d1 = d1 + dx - dy + (semi_minor * semi_minor);
            }
        }

        // Decision parameter of region 2
        float d2 = ((semi_minor * semi_minor) * ((x + 0.5f) * (x + 0.5f)))
                + ((semi_major * semi_major) * ((y - 1) * (y - 1)))
                - (semi_major * semi_major * semi_minor * semi_minor);

        // Plotting points of region 2
        while (y >= 0) {

            plotPoints(center_x, center_y, x, y);

            // Checking and updating parameter
            // value based on algorithm
            if (d2 > 0) {
                y--;
                dy = dy - (2 * semi_major * semi_major);
                d2 = d2 + (semi_major * semi_major) - dy;
            } else {
                y--;
                x++;
                dx = dx + (2 * semi_minor * semi_minor);
                dy = dy - (2 * semi_major * semi_major);
                d2 = d2 + dx - dy + (semi_major * semi_major);
            }
        }
    }

    // Print points based on 4-way symmetry
    private void plotPoints(int cx, int cy, int x, int y) {
        raster.setPixel(cx + x, cy + y, 0x00FF00);
        raster.setPixel(cx - x, cy + y, 0x00FF00);
        raster.setPixel(cx + x, cy - y, 0x00FF00);
        raster.setPixel(cx - x, cy - y, 0x00FF00);
    }
}
