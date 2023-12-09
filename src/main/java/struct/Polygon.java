package struct;

import java.util.List;

public class Polygon {
    private final List<Point> _vertices;

    /**
     * Main class constructor method.
     * @param vertices list of polygon edge points.
     */
    public Polygon(List<Point> vertices){
        _vertices = vertices;
    }

    // Polygon vertices getter method.
    public List<Point>getVertices(){
        return _vertices;
    }
}
