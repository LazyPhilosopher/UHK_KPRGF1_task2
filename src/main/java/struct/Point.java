package struct;


import java.util.ArrayList;
import java.util.List;

/**
 * Point object class.
 */
public class Point implements Struct{

    public int _x, _y;
    public List<Struct> _related_objects = new ArrayList<>();
    private final List<Point> _vertices = null;

    /**
     * Main class constructor method.
     * @param x point X coordinate.
     * @param y point Y coordinate.
     */
    public Point(int x, int y) {
        this._x = x;
        this._y = y;
    }

    public Point(double x, double y) {
        this._x = (int) Math.round(x);
        this._y = (int) Math.round(y);
    }

    // point X coordinate getter.
    public int X(){
        return _x;
    }

    // point Y coordinate getter.
    public int Y(){
        return _y;
    }

    // Coordinate setter method
    public void setCoordinates(int x, int y){
        _x = x;
        _y = y;
    }

    public void popPointFromRelatedObjects(){
        for (Struct object : this._related_objects){
            object.removeRelatedStruct(this);
        }
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
