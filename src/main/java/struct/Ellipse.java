package struct;

import java.util.Arrays;
import java.util.List;

public class Ellipse {

    private Point _center_point, _semi_minor_point, _semi_major_point;

    public Ellipse(Point center_point, Point semi_minor_point, Point semi_major_point){
        this._center_point = center_point;
        this._semi_minor_point = semi_minor_point;
        this._semi_major_point = semi_major_point;
    }

    public Point center_point(){
        return this._center_point;
    }

    public int semi_major(){
        return Math.abs(this._center_point.X() - this._semi_major_point.X());
    }

    public int semi_minor(){
        return Math.abs(this._center_point.Y() - this._semi_minor_point.Y());
    }
}
