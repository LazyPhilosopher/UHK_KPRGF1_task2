package logic.Controller;

import gui.Menu.Menu;
import gui.Panel;
import logic.Logic;
import logic.StructDataBase;
import logic.Utils;
import rasterize.StructRasterizer;
import struct.Ellipse;
import struct.Line;
import struct.Point;
import struct.Polygon;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Panel interaction control module.
 * Point, Line, Polygon drawing modes are supported.
 * Placed Points are draggable.
 * Lines and polygons can later be created of Points.
 */
public class Controller2D implements Controller {

    private final Panel panel;

    private int x,y;
    private Utils utils;
    private Logic logic;
    private StructDataBase structures_database;
    private StructRasterizer rasterizer;

    public Point dragged_point = null;
    private BufferedImage buffered_panel_raster = null;
    private final Menu menu = new Menu(15, 10);

    Map<Integer, String> modes = new HashMap<>() {{
        put(1, "line");
        put(2, "polygon");
        put(3, "point");
        put(4, "drag point");
        put(5, "ellipse");
        put(6, "seed fill");
        put(7, "crop line with rect");
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
        structures_database = new StructDataBase(panel.getWidth(), panel.getHeight());
        rasterizer = new StructRasterizer(panel);
        logic = new Logic(panel);
        utils = new Utils();
        repaint();
     }

    /**
     * Repaint panel with previously saved raster.
     * No structure recalculation will be done.
     * Used to spare drawing time.
     */
     private void bufferedRepaint(){
         panel.clear();
         panel.setImg(buffered_panel_raster);
         buffered_panel_raster = panel.getRaster().copyImg();
     }

    /**
     * Redraw every structure stored in ModelDataBase object.
     * Raster image is being saved into buffer afterward.
     */
    private void repaint() {
        panel.clear();
        structures_database.initPixelStack();

        for (Polygon polygon : structures_database.getPolygonStack()) {
            rasterizer.polygon.drawFilledPolygon(polygon, 0x0000FF);
            List<Point> polygon_pixels = rasterizer.polygon.get_polygon_pixels(polygon);
            structures_database.addToPixelStack(polygon_pixels);
        }

        for (Ellipse ellipse : structures_database.getEllipseStack()) {
            rasterizer.ellipse.drawEllipse(ellipse);
            rasterizer.point.drawPoint(ellipse.center_point(), 0xFFFFFF);
        }

        for (Line line : structures_database.getLineStack()) {
            rasterizer.line.drawLine(line.start_point().X(), line.start_point().Y(), line.end_point().X(), line.end_point().Y(), new Color(0x00AA00));
            List<Point> line_pixels = rasterizer.line.getLinePoints(line);
            for(Point pixel : line_pixels){pixel.addRelatedStruct(line);}
            structures_database.addToPixelStack(line_pixels);
        }

        for (Point point : structures_database.getPointStack()){
            rasterizer.point.drawPoint(point.X(), point.Y(), structures_database.getLastAddedPointStack().contains(point) ? 0x00ff00 : 0xff0000);
        }

        menu.draw(mode_key, modes, panel);
        buffered_panel_raster = panel.getRaster().copyImg();
    }

    /**
     * Check if user clicked Mode-Switching menu.
     * If so, mode will be updated and true value is returned.
     * Otherwise, returns false.
     */
    public boolean mouseClickedMenuRoutine(int x, int y){
        boolean click_is_within_menu = utils.isWithinRectangle(x, y, menu.getMenuBoundaries());
        if(!click_is_within_menu){
            return false;
        }
        for(Integer mode : modes.keySet()){
            if(utils.isWithinRectangle(x, y, menu.getButtonBoundaries(mode))){
                bufferedRepaint();
                System.out.printf("Mode: %s\n", modes.get(mode));
                mode_key = mode;
                structures_database.emptyTempPoints();
                menu.draw(mode_key, modes, panel);
                buffered_panel_raster = panel.getRaster().copyImg();
            }
        }
        return true;
    }

    @Override
    public void initListeners(Panel panel) {

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                rasterizer = new StructRasterizer(panel);
                repaint();
                logic = new Logic(panel);
            }
        });

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
                    dragged_point = logic.drag_point_mode.mouseClick(structures_database, dragged_point, x, y);
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                x = e.getX() < panel.getWidth() ? e.getX() : panel.getWidth()-1;
                y = e.getY() < panel.getHeight() ? e.getY() : panel.getHeight()-1;
                x = Math.max(e.getX(), 0);
                y = Math.max(e.getY(), 0);

                if(Objects.equals(modes.get(mode_key), "line")){
                    bufferedRepaint();
                    logic.line_mode.mouseMoved(structures_database, e.isShiftDown(), x, y);
                }else if(Objects.equals(modes.get(mode_key), "crop line with rect")){
                    bufferedRepaint();
                    logic.line_crop_mode.mouseMoved(structures_database, x, y);
                }else if(Objects.equals(modes.get(mode_key), "ellipse")){
                    bufferedRepaint();
                    logic.ellipse_mode.mouseMoved(structures_database, x, y);
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
                    logic.line_mode.mouseClick(structures_database, e.isShiftDown(), x, y);
                    repaint();
                }else if (Objects.equals(modes.get(mode_key), "point")) {
                    logic.point_mode.mouseClick(structures_database, x, y);
                    repaint();
                }else if (Objects.equals(modes.get(mode_key), "polygon")) {
                    logic.polygon_mode.mouseClick(structures_database, x, y);
                    repaint();
                }else if (Objects.equals(modes.get(mode_key), "crop line with rect")) {
                    logic.line_crop_mode.mouseClick(structures_database, x, y);
                    repaint();
                }else if (Objects.equals(modes.get(mode_key), "ellipse")) {
                    logic.ellipse_mode.mouseClick(structures_database, x, y);
                    repaint();
                }else if (Objects.equals(modes.get(mode_key), "seed fill")) {
                    logic.seed_fill_mode.mouseClicked(x, y);
                    // repaint is not being done as it will erase seed filled pixels.
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragged_point = null;
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    structures_database.init();
                }
                repaint();
            }
        });
    }
}
