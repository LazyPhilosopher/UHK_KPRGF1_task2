package utils.gui;

import struct.Polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        int max_button_y_size = _buttons.values().stream()
                .mapToInt(Button::getSizeY)
                .sum();
        max_button_y_size += (int) ((_buttons.size()+1) * _margin);
        return new int[]{max_button_x_size, max_button_y_size};
    }
}
