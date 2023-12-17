package logic;

import struct.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Class for storing geometric structures and their attributes.
 * Method for structure attribute modification are also implemented.
 */
public class StructDataBase {

    private List<List<List<Point>>> coordinated_vertex_stack = new ArrayList<>();
    private List<List<List<Point>>> coordinated_pixel_stack = new ArrayList<>();
    private List<Point> last_added_point_stack = new ArrayList<>();

    private List<Point> point_stack = new ArrayList<>();
    private List<Line> line_stack = new ArrayList<>();
    private List<Polygon> polygon_stack = new ArrayList<>();
    private List<Ellipse> ellipse_stack = new ArrayList<>();

    /**
     * Class initialization method.
     * In order to store structures it requires to know canvas dimensions.
     *
     * @param x_axis_size The canvas width.
     * @param y_axis_size The canvas height.
     */
    public StructDataBase(int x_axis_size, int y_axis_size) {
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
        this.coordinated_vertex_stack = new ArrayList<>();
        this.coordinated_pixel_stack = new ArrayList<>();
        this.line_stack = new ArrayList<>();
        this.last_added_point_stack = new ArrayList<>();
        this.polygon_stack = new ArrayList<>();
        this.point_stack = new ArrayList<>();
        this.ellipse_stack = new ArrayList<>();

        for (int x = 0; x < x_axis_size; x++) {
            this.coordinated_vertex_stack.add(new ArrayList<>());
            for (int y = 0; y < y_axis_size; y++) {
                this.coordinated_vertex_stack.get(x).add(new ArrayList<>());
            }
        }
        initPixelStack();
    }

    /**
     * Add new Line structure to database.
     *
     * @param new_line New Line structure.
     */
    public void addLine(Line new_line){
        this.line_stack.add(new_line);
    }

    /**
     * Add new Point structure to database.
     *
     * @param new_point New Point structure.
     */
    public void addPoint(Point new_point){
        this.last_added_point_stack.add(new_point);
        this.point_stack.add(new_point);
        this.coordinated_vertex_stack.get(new_point.X()).get(new_point.Y()).add(new_point);
    }

    /**
     * Add new Point structure to last added Point stack.
     * Used primarily for Undo functionality.
     *
     * @param new_point New Point structure.
     */
    public void addTempPoint(Point new_point){
        this.last_added_point_stack.add(new_point);
    }

    /**
     * Add new Polygon structure to database.
     *
     * @param new_polygon New Polygon structure.
     */
    public void addPolygon(Polygon new_polygon){
        this.polygon_stack.add(new_polygon);
    }

    public void addEllipse(Ellipse new_ellipse){
        this.ellipse_stack.add(new_ellipse);
    }

    /**
     * Line structure stack getter method.
     *
     * @return Line structure stack.
     */
    public List<Line> getLineStack(){
        return this.line_stack;
    }

    /**
     * Coordinated point stack getter method.
     * Returns 2D array of Point structures present in database.
     * In order to get list of Points in particular pixel access by
     * coordinated_point_stack.get(x).get(y).
     *
     * @return Coordinated point stack array.
     */
    public List<List<List<Point>>> getCoordinatedPointStack(){
        return this.coordinated_vertex_stack;
    }

    public List<List<List<Point>>> getCoordinatedPixelStack(){
        return this.coordinated_pixel_stack;
    }

    public List<Point> getPointStack(){
        return this.point_stack;
    }

    /**
     * Last added Point stack getter method.
     *
     * @return Last added Point stack.
     */
    public List<Point> getLastAddedPointStack(){
        return this.last_added_point_stack;
    }

    /**
     * Polygon structure stack getter method.
     *
     * @return Polygon structure stack.
     */
    public List<Polygon> getPolygonStack(){
        return polygon_stack;
    }

    public List<Ellipse> getEllipseStack(){
        return ellipse_stack;
    }


    /**
     * Pop Point structure from Last added Point stack.
     * Popped structure is returned and removed from the stack.
     *
     * @param pos stack item index.
     * @return Point structure.
     */
    public Point popLastAddedPoint(int pos){
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
        int end_x = (coord_x < coordinated_vertex_stack.size() - range ? coord_x + range : coordinated_vertex_stack.size());
        int start_y = (coord_y > range ? coord_y - range : 0);
        int end_y = (coord_x < coordinated_vertex_stack.get(0).size() - range ? coord_y + range : coordinated_vertex_stack.get(0).size());

        for (int x = start_x; x < end_x; x++){
            for (int y = start_y; y < end_y; y++){
                out.addAll(coordinated_vertex_stack.get(x).get(y));
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
        List<Point> point_list = coordinated_vertex_stack.get(point.X()).get(point.Y());
        coordinated_vertex_stack.get(dest_x).get(dest_y).add(point);
        point.setCoordinates(dest_x, dest_y);
    }

    /**
     * Delete point from database.
     *
     * @param point Deleted Point .
     *               TODO. check point existance.
     */
    public void deletePoint(Point point){
        List<Point> point_list = coordinated_vertex_stack.get(point.X()).get(point.Y());
        last_added_point_stack.remove(point);
        point_list.remove(point);
        point_stack.remove(point);
        for (Struct related_struct: point.getRelatedStructs()){
            related_struct.removeRelatedStruct(point);
        }
    }

    public void deleteLine(Line line){
        line.start_point().removeRelatedStruct(line);
        line.end_point().removeRelatedStruct(line);
        this.line_stack.remove(line);
    }

    public void addToPixelStack(List<Point> pixels){
        for (Point pixel : pixels){
            this.coordinated_pixel_stack.get(pixel.X()).get(pixel.Y()).add(pixel);
        }
    }

    public List<Point> getPixelFromCoordinatedStack(int x, int y){
        return this.coordinated_pixel_stack.get(x).get(y);
    }

    public void initPixelStack(){
        coordinated_pixel_stack = new ArrayList<>();
        int size_x = this.coordinated_vertex_stack.size();
        int size_y = this.coordinated_vertex_stack.get(0).size();

        for (int x = 0; x < size_x; x++) {
            this.coordinated_pixel_stack.add(new ArrayList<>());
            for (int y = 0; y < size_y; y++) {
                this.coordinated_pixel_stack.get(x).add(new ArrayList<>());
            }
        }
    }

    /**
     * Init coordinated point stack.
     * Used for previously initialized coordinated point stack.
     *
     */
    public void init(){
        init(this.coordinated_vertex_stack.size(), this.coordinated_vertex_stack.get(0).size());
    }

}
