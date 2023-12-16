package struct;

import java.util.ArrayList;
import java.util.List;

public class Line implements Struct{

    private final Point _start_point, _end_point;
    private final List<Struct> _related_objects = new ArrayList<>();

    /**
     * Main class constructor method.
     * @param start_point line start point.
     * @param end_point line end point.
     */
    public Line(Point start_point, Point end_point) {
        this._start_point = start_point;
        this._end_point = end_point;
        this._start_point.addRelatedStruct(this);
        this._end_point.addRelatedStruct(this);
        addRelatedStruct(this._start_point);
        addRelatedStruct(this._end_point);
    }

    /**line start point getter method.*/
    public Point start_point(){
        return _start_point;
    }

    /**line end point getter method.*/
    public Point end_point(){
        return _end_point;
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
