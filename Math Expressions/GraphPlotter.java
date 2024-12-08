import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import java.util.ArrayList;

public class GraphPlotter extends JFrame {
    ArrayList<ArrayList<Double>> points;
    private double scale = 20.0;

    public GraphPlotter(ArrayList<ArrayList<Double>> points) {    
        this.points = points;
        setTitle("Plot of the Expression");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(Color.WHITE);

        JPanel drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPlot(g);
            }
        };

        getContentPane().add(drawingPanel);
        setVisible(true);

        drawingPanel.setBackground(Color.WHITE);

        drawingPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int rotation = e.getWheelRotation();
                if (rotation < 0) {
                    scale *= 1.1; 
                } else {
                    scale /= 1.1; 
                }
                repaint();
            }
        });
    }

    private void drawPlot(Graphics g) {
        int originX = getWidth() / 2;
        int originY = getHeight() / 2;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawLine(0, originY, getWidth(), originY);
        g2d.drawLine(originX, 0, originX, getHeight());

        for (int i = 0; i < points.size(); i++) {
            ArrayList<Double> point = points.get(i);

            int pixelX = originX + (int) (point.get(0) * scale); 
            int pixelY = originY - (int) (point.get(1) * scale);

            g2d.fillOval(pixelX - 3, pixelY - 3, 6, 6);

            if (i > 0) {
                ArrayList<Double> prevPoint = points.get(i - 1);
                int prevPixelX = originX + (int) (prevPoint.get(0) * scale);
                int prevPixelY = originY - (int) (prevPoint.get(1) * scale);

                g2d.draw(new Line2D.Double(prevPixelX, prevPixelY, pixelX, pixelY));
            }
        }
    }
}