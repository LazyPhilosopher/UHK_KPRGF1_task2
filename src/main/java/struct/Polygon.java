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

    public Polygon(Point a, Point b){
        this._vertices = new ArrayList<>();
        this._vertices.add(a);
        this._vertices.add(new Point(a.X(), b.Y()));
        this._vertices.add(new Point(b.X(), b.Y()));
        this._vertices.add(new Point(b.X(), a.Y()));

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

    public Point getLeftUpPoint(){
        Point out = this._vertices.get(0);
        for (Point point : this._vertices){
            if (out.X() > point.X() || out.Y() > point.Y()){
                out = point;
            }
        }
        return out;
    }

    public Point getBottomRightPoint(){
        Point out = this._vertices.get(0);
        for (Point point : this._vertices){
            if (out.X() < point.X() || out.Y() < point.Y()){
                out = point;
            }
        }
        return out;
    }

    public Point getBottomLeftPoint(){
        Point out = this._vertices.get(0);
        for (Point point : this._vertices){
            if (out.X() > point.X() || out.Y() < point.Y()){
                out = point;
            }
        }
        return out;
    }

    public Point getRightUpPoint(){
        Point out = this._vertices.get(0);
        for (Point point : this._vertices){
            if (out.X() < point.X() || out.Y() > point.Y()){
                out = point;
            }
        }
        return out;
    }

    public Point getLeftRightPoint(){
        Point out = this._vertices.get(0);
        for (Point point : this._vertices){
            if (out.X() > point.X() || out.Y() < point.Y()){
                out = point;
            }
        }
        return out;
    }

    public Point getMiddlePoint(){
        int x = 0;
        int y = 0;
        for (Point point : this._vertices){
            x += point.X();
            y += point.Y();
        }
        x /= this._vertices.size();
        y /= this._vertices.size();
        return new Point(x, y);
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
