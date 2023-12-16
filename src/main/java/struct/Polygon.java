package struct;

import java.util.ArrayList;
import java.util.List;

public class Polygon implements Struct {
    private final List<Point> _vertices;
    private List<Struct> _related_objects = new ArrayList<>();

    /**
     * Main class constructor method.
     * @param vertices list of polygon edge points.
     */
    public Polygon(List<Point> vertices){
        this._vertices = vertices;
        for (Point point : this._vertices){
            point.addRelatedStruct(this);
            this.addRelatedStruct(point);
        }
    }

    // Polygon vertices getter method.
    public List<Point>getVertices(){
        return _vertices;
    }

    public void removePoint(Point point){
        this._vertices.remove(point);
        this._related_objects.remove(point);
    }

    @Override
    public void addRelatedStruct(Struct struct) {
        this._related_objects.add(struct);
    }

    @Override
    public void removeRelatedStruct(Struct struct) {
        this._related_objects.remove(struct);
    }

    @Override
    public List<Struct> getRelatedStructs() {
        return this._related_objects;
    }
}
