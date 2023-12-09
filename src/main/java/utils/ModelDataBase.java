package utils;

import struct.Line;
import struct.Point;
import struct.Polygon;

import java.util.ArrayList;
import java.util.List;


/**
 * Class for storing geometric structures and their attributes.
 * Method for structure attribute modification are also implemented.
 */
public class ModelDataBase {

    private List<List<List<Point>>> coordinated_point_stack = new ArrayList<>();
    private List<Point> last_added_point_stack = new ArrayList<>();

    private List<Point> point_stack = new ArrayList<>();
    private List<Line> line_stack = new ArrayList<>();
    private List<Polygon> polygon_stack = new ArrayList<>();

    /**
     * Class initialization method.
     * In order to store structures it requires to know canvas dimensions.
     *
     * @param x_axis_size The canvas width.
     * @param y_axis_size The canvas height.
     */
    public ModelDataBase(int x_axis_size, int y_axis_size) {
        init(x_axis_size, y_axis_size);
    }

    /**
     * Reinitialization method.
     * Used primarily to reset database and clear canvas.
     *
     * @param x_axis_size The canvas width.
     * @param y_axis_size The canvas height.
     */
    public void init(int x_axis_size, int y_axis_size){
        coordinated_point_stack = new ArrayList<>();
        line_stack = new ArrayList<>();
        last_added_point_stack = new ArrayList<>();
        polygon_stack = new ArrayList<>();

        for (int x = 0; x < x_axis_size; x++) {
            coordinated_point_stack.add(new ArrayList<>());
            for (int y = 0; y < y_axis_size; y++) {
                coordinated_point_stack.get(x).add(new ArrayList<>());
            }
        }
    }

    /**
     * Add new Line structure to database.
     *
     * @param new_line New Line structure.
     */
    public void addLine(Line new_line){
        line_stack.add(new_line);
    }

    /**
     * Add new Point structure to database.
     *
     * @param new_point New Point structure.
     */
    public void addPoint(Point new_point){
        last_added_point_stack.add(new_point);
        point_stack.add(new_point);
        coordinated_point_stack.get(new_point.X()).get(new_point.Y()).add(new_point);
    }

    /**
     * Add new Point structure to last added Point stack.
     * Used primarily for Undo functionality.
     *
     * @param new_point New Point structure.
     */
    public void addTempPoint(Point new_point){
        last_added_point_stack.add(new_point);
    }

    /**
     * Add new Polygon structure to database.
     *
     * @param new_polygon New Polygon structure.
     */
    public void addPolygon(Polygon new_polygon){
        polygon_stack.add(new_polygon);
    }

    /**
     * Line structure stack getter method.
     *
     * @return Line structure stack.
     */
    public List<Line> getLineStack(){
        return line_stack;
    }

    /**
     * Coordinated point stack getter method.
     * Returns 2D array of Point structures present in database.
     * In order to get list of Points in particular pixel access by
     * coordinated_point_stack.get(x).get(y).
     *
     * @return Coordinated point stack array.
     */
    public List<List<List<Point>>> getPointStack(){
        return coordinated_point_stack;
    }

    /**
     * Last added Point stack getter method.
     *
     * @return Last added Point stack.
     */
    public List<Point> getLastAddedPointStack(){
        return last_added_point_stack;
    }

    /**
     * Polygon structure stack getter method.
     *
     * @return Polygon structure stack.
     */
    public List<Polygon> getPolygonStack(){
        return polygon_stack;
    }

    /**
     * Pop Point structure from Last added Point stack.
     * Popped structure is returned and removed from the stack.
     *
     * @param pos stack item index.
     * @return Point structure.
     */
    public Point popTempPoint(int pos){
        Point out = last_added_point_stack.get(pos);
        last_added_point_stack.remove(pos);
        return out;
    }

    /**
     * Get Point structure from Last added Point stack.
     *
     * @param pos stack item index.
     * @return Point structure.
     */
    public Point getTempPoint(int pos){
        return last_added_point_stack.get(pos);
    }

    /**
     * Clear Last added Point stack.
     * Primarily used in Canvas clear functionalty.
     */
    public void emptyTempPoints(){
        last_added_point_stack = new ArrayList<>();
    }

    /**
     * Get all Point structures that are close enough to provided coordinates.
     * Primarily used in Canvas clear functionalty.
     *
     * @param coord_x search point X coordinate.
     * @param coord_y search point Y coordinate.
     * @param range pixel search range radius.
     * @return List of Points close enough to search point.
     */
    public List<Point> getClosePoints(int coord_x, int coord_y, int range){
        List<Point> out = new ArrayList<>();

        int start_x = (coord_x > range ? coord_x - range : 0);
        int end_x = (coord_x < coordinated_point_stack.size() - range ? coord_x + range : coordinated_point_stack.size());
        int start_y = (coord_y > range ? coord_y - range : 0);
        int end_y = (coord_x < coordinated_point_stack.get(0).size() - range ? coord_y + range : coordinated_point_stack.get(0).size());

        for (int x = start_x; x < end_x; x++){
            for (int y = start_y; y < end_y; y++){
                out.addAll(coordinated_point_stack.get(x).get(y));
            }
        }
        return out;
    }

    /**
     * Get Euclidean distance between two Points.
     *
     * @param a Point A.
     * @param b Point B.
     * @return distance float value.
     */
    public float pointToPointDistance(Point a, Point b){
        return (float)(Math.pow(a.X() - b.X(), 2) + Math.pow(a.Y() - b.Y(), 2));
    }


    /**
     * Get Euclidean distance between Point and provided coordinates.
     *
     * @param a Point A.
     * @param x Provided X coordinate.
     * @param y Provided Y coordinate.
     * @return distance float value.
     */
    public float pointToPointDistance(Point a, int x, int y){
        return (float)(Math.pow(a.X() - x, 2) + Math.pow(a.Y() - y, 2));
    }

    /**
     * Get the closest Point to provided coordinates.
     * If no Point found null value is returned.
     *
     * @param x Search point X coordinate.
     * @param y Search point Y coordinate.
     * @param range pixel search range radius.
     * @return closest Point.
     */
    public Point getClosestPoint(int x, int y, int range){
        List<Point> points = getClosePoints(x, y, range);
        Point closest_point = null;
        for (Point point : points){
            if (closest_point == null || pointToPointDistance(point, x, y) < pointToPointDistance(point, closest_point)){
                closest_point = point;
            }
        }
        return closest_point;
    }

    /**
     * Change Point coordinates.
     * Both Point object coordinate attributes and coordinated_point_stack are modified.
     *
     * @param point Moved Point .
     * @param dest_x Destination point X coordinate.
     * @param dest_y Destination point Y coordinate.
     *               TODO. check point existance.
     */
    public void movePoint(Point point, int dest_x, int dest_y){
        List<Point> point_list = coordinated_point_stack.get(point.X()).get(point.Y());
        point_list.remove(point);
        point_stack.remove(point);
        coordinated_point_stack.get(dest_x).get(dest_y).add(point);
    }

    /**
     * Delete point from database.
     *
     * @param point Deleted Point .
     *               TODO. check point existance.
     */
    public void deletePoint(Point point){
        List<Point> point_list = coordinated_point_stack.get(point.X()).get(point.Y());
        last_added_point_stack.remove(point);
        point_list.remove(point);
        point_stack.remove(point);
    }

    /**
     * Init coordinated point stack.
     * Used for previously initialized coordinated point stack.
     *
     */
    public void init(){
        init(coordinated_point_stack.size(), coordinated_point_stack.get(0).size());
    }

}
