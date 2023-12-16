package gui.Menu;

public class Button {
    private String _text;
    private int _font_size;
    private int _x_size;
    private int _y_size;
    private int _margin;

    public Button(String text){
        this.setText(text);
    }

    public Button(String text, int font_size){
        this(text);
        this.setTextSize(font_size);
    }

    public Button(String text, int font_size, int margin){
        this(text, font_size);
        this.setMargin(margin);
    }

    public void setText(String text) {
        this._text = text;
    }

    public String getText(){
       return _text;
    }

    public void setMargin(int margin){
        this._margin = margin;
    }

    public void setTextSize(int font_size) {
        this._font_size = font_size;
        this._x_size = (int) (_text.length() * (font_size+1) / 2);
        this._y_size = (int) (font_size * 2);
    }

    public int getSizeX(){
        return  _x_size;
    }

    public int getSizeY(){
        return  _y_size;
    }
}
