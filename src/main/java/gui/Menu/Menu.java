package gui.Menu;

import rasterize.struct.PolygonRasterizer;
import struct.Polygon;
import struct.Point;
import gui.Panel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Menu {

    private HashMap<Integer, Button> _buttons = new HashMap<Integer, Button>();
    private int _font_size = 20;
    private int _margin = 10;
    private Polygon _menu_boundaries;
    private HashMap<Integer, Polygon> _button_boundaries = new HashMap<Integer, Polygon>();

    public Menu(){

    }
    public Menu(int font_size, int margin){
        this.setFontSize(font_size);
        this.setMargin(margin);
    }

    public void addButton(Integer button_key, String text){
        _buttons.put(button_key, new Button(text, _font_size));
    }

    public HashMap<Integer, Button> getButtons(){
        return _buttons;
    }

    public int getFontSize(){
        return _font_size;
    }

    public void setFontSize(int font_size){
        this._font_size = font_size;
    }

    public int getMargin(){
        return _margin;
    }

    public void setMargin(int margin){
        this._margin = margin;
    }

    public Button getButton(Integer button_key){
        return _buttons.get(button_key);
    }

    public void setMenuBoundaries(Polygon menu_boundaries){
        _menu_boundaries = menu_boundaries;
    }

    public Polygon getMenuBoundaries(){
        return _menu_boundaries;
    }

    public void addButtonBoundaries(Integer button_key, Polygon button_boundaries){
        _button_boundaries.put(button_key, button_boundaries);
    }

    public Polygon getButtonBoundaries(Integer button_key){
        return _button_boundaries.get(button_key);
    }

    public void resetButtonBoundaries(){
        _button_boundaries = new HashMap<Integer, Polygon>();
    }

    public int[] getMenuDimensions(){
        int max_button_x_size = _buttons.values().stream()
                .mapToInt(Button::getSizeX)
                .max()
                .orElse(0);
        max_button_x_size += 2*_margin;

        int sum_button_y_size = _buttons.values().stream()
                .mapToInt(Button::getSizeY)
                .sum();
        sum_button_y_size += (int) ((_buttons.size()+1) * _margin);
        return new int[]{max_button_x_size, sum_button_y_size};
    }

    public void draw(int active_mode_key, Map<Integer, String> modes_map, Panel panel){
        PolygonRasterizer polygon_rasterizer = new PolygonRasterizer(panel.getRaster());

        for (Integer key : modes_map.keySet()) {
            this.addButton(key, modes_map.get(key));
        }

        int margin = this.getMargin();
        int[] menu_dimensions = this.getMenuDimensions();
        int menu_x_dimension = menu_dimensions[0];
        int menu_y_dimension = menu_dimensions[1];

        int menu_padding = 10;
        int y = panel.getHeight()-menu_padding;

        // Draw menu box
        java.util.List<Point> menu_vertices = java.util.List.of(new Point(menu_padding, y - menu_y_dimension),
                new Point(menu_padding + menu_x_dimension, y - menu_y_dimension),
                new Point(menu_padding + menu_x_dimension, y),
                new Point(menu_padding, y));
        this.setMenuBoundaries(new Polygon(menu_vertices));
        polygon_rasterizer.drawFilledPolygon(this.getMenuBoundaries(), 0x333333);

        // Draw each menu button
        int start_x_pos = menu_padding + margin;
        int start_y_pos = y - menu_y_dimension + this.getFontSize() + margin;
        for (Integer mode : modes_map.keySet()){

            java.util.List<Point> button_vertices = List.of(new Point(start_x_pos, start_y_pos - this.getFontSize()),
                    new Point(start_x_pos + menu_dimensions[0] - 2*margin, start_y_pos - this.getFontSize()),
                    new Point(start_x_pos + menu_dimensions[0] - 2*margin, start_y_pos + margin),
                    new Point(start_x_pos, start_y_pos + margin));
            this.addButtonBoundaries(mode, new Polygon(button_vertices));
            polygon_rasterizer.drawFilledPolygon(this.getButtonBoundaries(mode), Objects.equals(mode, active_mode_key) ? 0x00AA00 : 0x000000);
            panel.writeText(this.getButton(mode).getText(),
                    this.getFontSize(), start_x_pos + margin, start_y_pos, 0xFFFFFF);

            start_y_pos += this.getButton(mode).getSizeY() + margin;
        }
    }
}
