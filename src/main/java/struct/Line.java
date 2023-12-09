package struct;

public class Line {

    private final Point _start_point, _end_point;

    /**
     * Main class constructor method.
     * @param start_point line start point.
     * @param end_point line end point.
     */
    public Line(Point start_point, Point end_point) {
        _start_point = start_point;
        _end_point = end_point;
    }

    /**line start point getter method.*/
    public Point start_point(){
        return _start_point;
    }

    /**line end point getter method.*/
    public Point end_point(){
        return _end_point;
    }

}
