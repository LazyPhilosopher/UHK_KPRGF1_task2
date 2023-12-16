package logic.Controller;

import gui.Panel;
import gui.Menu.Menu;
import logic.CommonLogic;
import logic.ModelDataBase;
import logic.modes.*;
import rasterize.struct.*;
import struct.Line;
import struct.Point;
import struct.Polygon;

import java.awt.Color;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;


/**
 * Panel interaction control module.
 * Point, Line, Polygon drawing modes are supported.
 * Placed Points are draggable.
 * Lines and polygons can later be created of Points.
 */
public class Controller2D implements Controller {

    private final Panel panel;

    private int x,y;
    CommonLogic common_logic;
    LineModeLogic line_mode_logic;
    PointModeLogic point_mode_logic;
    PolygonModeLogic polygon_mode_logic;
    DragPointModeLogic drag_point_mode_logic;
    RectangleCropModeLogic rectangle_crop_mode_logic;
    private ModelDataBase structures_database;
    public Point dragged_point = null;
    private BufferedImage buffered_panel_raster = null;
    Menu menu = new Menu(15, 10);

    Map<Integer, String> modes = new HashMap<Integer, String>() {{
        put(1, "line");
        put(2, "polygon");
        put(3, "point");
        put(4, "drag point");
        put(5, "ellipse");
        put(6, "seed fill");
        put(7, "rectangle crop");
    }};
    private Integer mode_key = (Integer) modes.keySet().toArray()[0];

    /**
     * Main class initialization method.
     *
     * @param panel panel instance inheriting from JPanel class.
     */
    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects();
        initListeners(panel);
    }

    /**
     * Main class private attributes initialization method.
     */
    private void initObjects() {
        structures_database = new ModelDataBase(panel.getWidth(), panel.getHeight());
        common_logic = new CommonLogic();
        line_mode_logic = new LineModeLogic(panel);
        point_mode_logic = new PointModeLogic();
        polygon_mode_logic = new PolygonModeLogic();
        drag_point_mode_logic = new DragPointModeLogic();
        rectangle_crop_mode_logic = new RectangleCropModeLogic(panel);
        repaint();
     }

     private void bufferedRepaint(){
         panel.clear();
         panel.setImg(buffered_panel_raster);
         buffered_panel_raster = panel.getRaster().copyImg();
     }

//     redraw objects stored in model_stack
    private void repaint() {
        panel.clear();
        structures_database.initPixelStack();

        for (Polygon polygon : structures_database.getPolygonStack()) {
            PolygonRasterizer polygon_rasterizer = new PolygonRasterizer(panel.getRaster());
            polygon_rasterizer.drawPolygon(polygon, 0x0000FF);
            List<Point> polygon_pixels = polygon_rasterizer.get_polygon_pixels(polygon);
            structures_database.addToPixelStack(polygon_pixels);
        }

//        for (Ellipse ellipse : structures_database.getEllipseStack()) {
//            ellipse_rasterizer.drawEllipse(ellipse);
//            point_rasterizer.drawPoint(ellipse.center_point(), 0xFFFFFF);
//        }

        for (Line line : structures_database.getLineStack()) {
            LineRasterizer line_rasterizer = new LineRasterizer(panel.getRaster());
            line_rasterizer.drawLine(line.start_point().X(), line.start_point().Y(), line.end_point().X(), line.end_point().Y(), new Color(0x00AA00));
            List<Point> line_pixels = line_rasterizer.getLinePoints(line);
            for(Point pixel : line_pixels){pixel.addRelatedStruct(line);}
            structures_database.addToPixelStack(line_pixels);
        }

        for (Point point : structures_database.getPointStack()){
            PointRasterizer point_rasterizer = new PointRasterizer(panel.getRaster());
            point_rasterizer.drawPoint(point.X(), point.Y(), structures_database.getLastAddedPointStack().contains(point) ? 0x00ff00 : 0xff0000);
        }

//        for(int x = 0; x < structures_database.getCoordinatedPixelStack().size(); x++){
//            for(int y = 0; y < structures_database.getCoordinatedPixelStack().get(0).size(); y++){
//                if(structures_database.getCoordinatedPixelStack().get(x).get(y).size() > 0){
//                    panel.getRaster().setPixel(x,y, 0x00AA00);
//                }
//            }
//        }

        menu.draw(mode_key, modes, panel);
        buffered_panel_raster = panel.getRaster().copyImg();
    }

    public boolean mouseClickedMenuRoutine(int x, int y){
        for(Integer mode : modes.keySet()){
            if(common_logic.isWithinRectangle(x, y, menu.getButtonBoundaries(mode))){
                bufferedRepaint();
                System.out.printf("Mode: %s\n", modes.get(mode));
                mode_key = mode;
                structures_database.emptyTempPoints();
                menu.draw(mode_key, modes, panel);
                buffered_panel_raster = panel.getRaster().copyImg();
                return true;
            }
        }
        return false;
    }

    @Override
    public void initListeners(Panel panel) {

        panel.addMouseMotionListener(new MouseAdapter() {
            // Click and drag point.
            @Override
            public void mouseDragged(MouseEvent e) {
                bufferedRepaint();

                x = e.getX() < panel.getWidth() ? e.getX() : panel.getWidth()-1;
                y = e.getY() < panel.getHeight() ? e.getY() : panel.getHeight()-1;
                x = Math.max(e.getX(), 0);
                y = Math.max(e.getY(), 0);

                if (Objects.equals(mode_key, 4)) {
                    dragged_point = drag_point_mode_logic.mouseClick(structures_database, dragged_point, x, y);
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                x = e.getX() < panel.getWidth() ? e.getX() : panel.getWidth()-1;
                y = e.getY() < panel.getHeight() ? e.getY() : panel.getHeight()-1;
                x = Math.max(e.getX(), 0);
                y = Math.max(e.getY(), 0);

                bufferedRepaint();
                if(Objects.equals(modes.get(mode_key), "line")){
                    line_mode_logic.mouseMove(structures_database, e.isShiftDown(), x, y);
                }else if(Objects.equals(modes.get(mode_key), "rectangle crop")){
                    rectangle_crop_mode_logic.mouseMove(structures_database, x, y);
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX() < panel.getWidth() ? e.getX() : panel.getWidth()-1;
                y = e.getY() < panel.getHeight() ? e.getY() : panel.getHeight()-1;
                x = Math.max(e.getX(), 0);
                y = Math.max(e.getY(), 0);

                if(mouseClickedMenuRoutine(x, y)){return;}

                if(Objects.equals(modes.get(mode_key), "line")){
                    line_mode_logic.mouseClick(structures_database, e.isShiftDown(), x, y);
                }else if (Objects.equals(modes.get(mode_key), "point")) {
                    point_mode_logic.mouseClick(structures_database, x, y);
                }else if (Objects.equals(modes.get(mode_key), "polygon")) {
                    polygon_mode_logic.mouseClick(structures_database, x, y);
                }else if (Objects.equals(modes.get(mode_key), "rectangle crop")) {
                    rectangle_crop_mode_logic.mouseClick(structures_database, x, y);
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragged_point = null;
                structures_database.initPixelStack();
                repaint();
            }
        });
    }
}
