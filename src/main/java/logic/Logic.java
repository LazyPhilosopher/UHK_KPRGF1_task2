package logic;

import gui.Panel;
import logic.modes_logic.*;

public class Logic {
    public PointModeLogic point_mode;
    public LineModeLogic line_mode;
    public PolygonModeLogic polygon_mode;
    public EllipseModeLogic ellipse_mode;
    public DragPointModeLogic drag_point_mode;
    public LineCropByRectangleModeLogic line_crop_mode;
    public SeedFillModeLogic seed_fill_mode;

    public Logic(Panel panel){
        line_mode = new LineModeLogic(panel);
        point_mode = new PointModeLogic();
        polygon_mode = new PolygonModeLogic();
        drag_point_mode = new DragPointModeLogic();
        line_crop_mode = new LineCropByRectangleModeLogic(panel);
        ellipse_mode = new EllipseModeLogic(panel);
        seed_fill_mode = new SeedFillModeLogic(panel);
    }
}
