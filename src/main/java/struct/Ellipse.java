package struct;

import java.util.Arrays;
import java.util.List;

public class Ellipse {

    private Point _center_point;
    int _semi_minor, _semi_major;

    public Ellipse(Point center_point, int semi_minor, int semi_major){
        this._center_point = center_point;
        this._semi_minor = semi_minor;
        this._semi_major = semi_major;
    }

    public Point center_point(){
        return this._center_point;
    }

    public int semi_minor(){
        return this._semi_minor;
    }

    public int semi_major(){
        return this._semi_major;
    }
}
