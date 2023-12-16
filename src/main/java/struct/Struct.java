package struct;

import java.util.ArrayList;
import java.util.List;

public interface Struct {
    List<Point> _vertices = new ArrayList<>();
    List<Struct> _related_objects = new ArrayList<>();

    void addRelatedStruct(Struct struct);

    void removeRelatedStruct(Struct struct);

    List<Struct> getRelatedStructs();
}
