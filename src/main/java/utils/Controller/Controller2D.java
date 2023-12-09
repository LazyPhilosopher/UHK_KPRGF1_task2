package utils.Controller;

import gui.Panel;
import rasterize.*;
import struct.Line;
import struct.Point;
import struct.Polygon;
import utils.ModelDataBase;
import utils.gui.Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.HashMap;
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
    private PolygonRasterizer polygon_rasterizer;
    private LineRasterizer line_rasterizer;
    private PointRasterizer point_rasterizer;
    private ModelDataBase model_stack;
    private Point active_point = null;
    Menu menu = new Menu(15, 10);

    Map<Integer, String> modes = new HashMap<Integer, String>() {{
        put(1, "line");
        put(2, "polygon");
        put(3, "point");
        put(4, "drag point");
        put(5, "ellipse");
    }};
    private Integer mode_key = (Integer) modes.keySet().toArray()[0];

    /**
     * Main class initialization method.
     *
     * @param panel panel instance inheriting from JPanel class.
     */
    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    /**
     * Main class private attributes initialization method.
     */
    private void initObjects(Raster raster) {
        model_stack = new ModelDataBase(panel.getWidth(), panel.getHeight());
        point_rasterizer = new PointRasterizer(raster);
        line_rasterizer = new LineRasterizer(raster);
        polygon_rasterizer = new PolygonRasterizer(raster);
        drawUI();
     }

    // Draw mode-switching menu.
    private void drawUI(){

        for (Integer key : modes.keySet()) {
            menu.addButton(key, modes.get(key));
        }

        int margin = menu.getMargin();
        int[] menu_dimensions = menu.getMenuDimensions();
        int menu_x_dimension = menu_dimensions[0];
        int menu_y_dimension = menu_dimensions[1];

        int menu_padding = 10;
        int y = panel.getHeight()-menu_padding;

        // Draw menu box
        List<Point> menu_vertices = List.of(new Point(menu_padding, y - menu_y_dimension),
                new Point(menu_padding + menu_x_dimension, y - menu_y_dimension),
                new Point(menu_padding + menu_x_dimension, y),
                new Point(menu_padding, y));
        menu.setMenuBoundaries(new Polygon(menu_vertices));
        polygon_rasterizer.drawPolygon(menu.getMenuBoundaries(), 0x333333);

        // Draw each menu button
        int start_x_pos = menu_padding + margin;
        int start_y_pos = y - menu_y_dimension + menu.getFontSize() + margin;
        for (Integer mode : modes.keySet()){

            List<Point> button_vertices = List.of(new Point(start_x_pos, start_y_pos - menu.getFontSize()),
                    new Point(start_x_pos + menu_dimensions[0] - 2*margin, start_y_pos - menu.getFontSize()),
                    new Point(start_x_pos + menu_dimensions[0] - 2*margin, start_y_pos + margin),
                    new Point(start_x_pos, start_y_pos + margin));
            menu.addButtonBoundaries(mode, new Polygon(button_vertices));
            polygon_rasterizer.drawPolygon(menu.getButtonBoundaries(mode), Objects.equals(mode, mode_key) ? 0x00AA00 : 0x000000);
            panel.writeText(menu.getButton(mode).getText(),
                    menu.getFontSize(), start_x_pos + margin, start_y_pos, 0xFFFFFF);

            start_y_pos += menu.getButton(mode).getSizeY() + margin;
        }
    }

    // redraw objects stored in model_stack
    private void repaint() {
        panel.clear();
        drawUI();

        for (Line line : model_stack.getLineStack()) {
            line_rasterizer.drawLine(line.start_point().X(), line.start_point().Y(), line.end_point().X(), line.end_point().Y(), new Color(0xff0000));
        }
        for (Polygon polygon : model_stack.getPolygonStack()) {
            polygon_rasterizer.drawPolygon(polygon, 0x00ff00);
        }

        // unoptimized way of drawing points
        // next version should avoid iterating through every coordinate on panel
        int model_stack_width = model_stack.getPointStack().size();
        int model_stack_height = model_stack.getPointStack().get(0).size();
        for (int x = 0; x < model_stack_width; x++) {
            for (int y = 0; y < model_stack_height; y++) {
                List<Point> points = model_stack.getPointStack().get(x).get(y);
                for (Point point : points) {
                    point_rasterizer.drawPoint(point.X(), point.Y(), model_stack.getLastAddedPointStack().contains(point) ? 0x00ff00 : 0xff0000);
                }
            }
        }
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                panel.clear();
                point_rasterizer = new PointRasterizer(panel.getRaster());
                line_rasterizer = new LineRasterizer(panel.getRaster());
                polygon_rasterizer = new PolygonRasterizer(panel.getRaster());
                repaint();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            // Click and drag point.
            @Override
            public void mouseDragged(MouseEvent e) {
                repaint();
                x = e.getX() < panel.getWidth() ? e.getX() : panel.getWidth()-1;
                y = e.getY() < panel.getWidth() ? e.getY() : panel.getHeight()-1;
                if (Objects.equals(mode_key, 4)) {
                    dragPointMode(x, y);
                }
                drawUI();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                repaint();
                x = e.getX() < panel.getWidth() ? e.getX() : panel.getWidth()-1;
                y = e.getY() < panel.getWidth() ? e.getY() : panel.getHeight()-1;
                if(Objects.equals(mode_key, 1)){
                    drawProposedLineInLineMode(e.isShiftDown(), x, y);
                }
                drawUI();
            }
        });

        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isControlDown()) return;

                if (SwingUtilities.isLeftMouseButton(e)) {
                    x = e.getX() < panel.getWidth() ? e.getX() : panel.getWidth()-1;
                    y = e.getY() < panel.getWidth() ? e.getY() : panel.getHeight()-1;

                    if(mouseClickedMenuRoutine(x, y)){return;}

                    if (Objects.equals(mode_key, 1)) {
                        clickInLineMode(e.isShiftDown(), x, y);
                    }else if (Objects.equals(mode_key, 3)) {
                        clickInPointMode(x, y);
                    }

                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                active_point = null;
            }
        });

    }

    public boolean mouseClickedMenuRoutine(int x, int y){
        for(Integer mode : modes.keySet()){
            if(clickIsWithinRectangle(x, y, menu.getButtonBoundaries(mode))){
                System.out.printf("Mode: %s\n", modes.get(mode));
                mode_key = mode;
                drawUI();
                return true;
            }
        }
        return false;
    }

    // Line mode mouse click routine.
    private void clickInLineMode(boolean shift_pressed, int x, int y){
        if (shift_pressed) {
            if (model_stack.getLastAddedPointStack().size() == 1) {
                Point start_point = model_stack.getTempPoint(0);
                int delta_x = Math.abs(start_point.X() - x);
                int delta_y = Math.abs(start_point.Y() - y);
                if(delta_x < delta_y && delta_x < 15) {
                    x = start_point.X();
                }
                if(delta_x > delta_y && delta_y < 15) {
                    y = start_point.Y();
                }
            }
        }

        Point point = new Point(x, y);
        // Search for already existing closest point.
        Point closest_point = model_stack.getClosestPoint(x ,y, 5);
        if (closest_point != null){
            point = closest_point;
            model_stack.addTempPoint(point);
        } else {
            // if no point nearby cursor exists - create new one.
            model_stack.addPoint(point);
        }

        // if single point already present in last added point stack - create a line instance.
        if(model_stack.getLastAddedPointStack().size() >= 2){
            Point point1 = model_stack.popTempPoint(0);
            Point point2 = model_stack.popTempPoint(0);
            model_stack.addLine(new Line(point1, point2));
        }
    }

    // Point mode mouse click routine.
    private void clickInPointMode(int x, int y){
        model_stack.addPoint(new Point(x, y));
    }

    // Drag-point mode mouse click routine.
    private void dragPointMode(int x, int y){
        // search for point user is dragging.
        if (active_point == null){
            active_point = model_stack.getClosestPoint(x, y, 5);
        }
        if (active_point != null) {
            model_stack.movePoint(active_point, x, y);
            active_point.setCoordinates(x, y);
        }
        // Repaint every object in database
        repaint();
    }

    // Drag-point mode mouse click routine.
    private void drawProposedLineInLineMode(boolean shift_pressed, int x, int y){
        repaint();
        if(model_stack.getLastAddedPointStack().size() == 1){
            Point start_point = model_stack.getTempPoint(0);
            if (shift_pressed) {
                if (model_stack.getLastAddedPointStack().size() == 1) {
                    int delta_x = Math.abs(start_point.X() - x);
                    int delta_y = Math.abs(start_point.Y() - y);
                    if(delta_x < delta_y && delta_x < 15) {
                        x = start_point.X();
                    }
                    if(delta_x > delta_y && delta_y < 15) {
                        y = start_point.Y();
                    }
                }
            }
            line_rasterizer.drawDottedLine(5, start_point.X(), start_point.Y(), x, y, new Color(0x00FF00));
        }
    }

    public boolean clickIsWithinRectangle(int click_x, int click_y, Polygon rectangle){
        int rectangle_max_x = rectangle.getVertices().stream()
                .mapToInt(Point::X)
                .max()
                .orElse(0);
        int rectangle_min_x = rectangle.getVertices().stream()
                .mapToInt(Point::X)
                .min()
                .orElse(0);
        int rectangle_max_y = rectangle.getVertices().stream()
                .mapToInt(Point::Y)
                .max()
                .orElse(0);
        int rectangle_min_y = rectangle.getVertices().stream()
                .mapToInt(Point::Y)
                .min()
                .orElse(0);

        if(!(rectangle_min_x <= click_x && click_x <= rectangle_max_x)){
            return false;
        } else if(!(rectangle_min_y <= click_y && click_y <= rectangle_max_y)){
            return false;
        }
        return true;
    }
}
