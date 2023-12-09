package gui.Canvas;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Canvas {
    private JFrame frame;
    private JPanel panel;
    private BufferedImage img;
    private List<CanvasListener> canvasListeners = new ArrayList<>();

    public Canvas(int width, int height) {
        frame = new JFrame();

        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1)
                    img.setRGB(e.getX(), e.getY(), 0xffff00);
                if (e.getButton() == MouseEvent.BUTTON2)
                    img.setRGB(e.getX(), e.getY(), 0xff00ff);
                if (e.getButton() == MouseEvent.BUTTON3)
                    img.setRGB(e.getX(), e.getY(), 0xffffff);
                panel.repaint();

                Map<String, Integer> dictionary = new HashMap<>();

                // Add key-value pairs
                dictionary.put("X", e.getX());
                dictionary.put("Y", e.getY());
                notifyCanvasListeners(dictionary);
            }
        });

        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public void clear() {
        Graphics gr = img.getGraphics();
        gr.setColor(new Color(0x2f2f2f));
        gr.fillRect(0, 0, img.getWidth(), img.getHeight());
    }

    public void present(Graphics graphics) {
        graphics.drawImage(img, 0, 0, null);
    }

    public void draw() {
        clear();
        img.setRGB(10, 10, 0xffff00);
    }

    public void start() {
        draw();
        panel.repaint();
    }

    public void notifyCanvasListeners(Map<String, Integer> additional_data) {
        CanvasEvent event = new CanvasEvent(this, additional_data);
        for (CanvasListener listener : canvasListeners) {
            listener.canvasClicked(event);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas(800, 600).start());
    }

}
