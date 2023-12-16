package struct;

import java.util.ArrayList;
import java.util.List;

public class Ellipse implements Struct{

    private final Point _center_point, _semi_minor_point, _semi_major_point;
    private final List<Struct> _related_objects = new ArrayList<>();

    public Ellipse(Point center_point, Point semi_major_point, Point semi_minor_point){
        this._center_point = center_point;
        this._semi_minor_point = semi_minor_point;
        this._semi_major_point = semi_major_point;
        addRelatedStruct(this._center_point);
        addRelatedStruct(this._semi_minor_point);
        addRelatedStruct(this._semi_major_point);
    }

    public Point center_point(){
        return this._center_point;
    }

    public int semi_minor(){
        return Math.abs(this._center_point.X() - this._semi_minor_point.X());
    }

    public int semi_major(){
        return Math.abs(this._center_point.Y() - this._semi_major_point.Y());
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
