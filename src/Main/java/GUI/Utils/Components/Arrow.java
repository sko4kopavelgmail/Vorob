package GUI.Utils.Components;

import GUI.Utils.Utils;
import lombok.Data;

import java.awt.*;
import java.util.ArrayList;

@Data()
public class Arrow {
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;
    private Point firstPoint;
    private Point lastPoint;
    private Point leftConnection;
    private Point rightConnection;
    private int direction;
    ArrayList<Point> points;

    private Graphics graphics;

    public Arrow(Point firstPoint, Point lastPoint, int direction) {
        this.firstPoint = new Point(firstPoint.getX(),firstPoint.getY());
        this.lastPoint = new Point(lastPoint.getX(),lastPoint.getY());
        this.direction = direction;
        init();
    }

    public Arrow(ArrayList<Point> points, int direction) {
        this.points = new ArrayList<>();
        this.points.addAll(points);
        this.direction = direction;
        init();
    }

    private void init() {
        if (points == null) {
            points = new ArrayList<>();
            points.add(firstPoint);
            points.add(lastPoint);
        }else {
            firstPoint = points.get(0);
            lastPoint = points.get(points.size() - 1);
        }
        leftConnection = firstPoint;
        rightConnection = lastPoint;
    }

    public void paint(Graphics graphics) {
        this.graphics = graphics;
        for (int i = 0; i < points.size() - 1; i++) {
            graphics.drawLine(points.get(i).getX(), points.get(i).getY(),
                    points.get(i + 1).getX(), points.get(i + 1).getY());
        }
        drawArrow();
    }

    // TODO: 07.12.2018 доделать направления стрелочек
    private void drawArrow() {
        switch (direction) {
            case UP:
                break;
            case DOWN:
                break;
            case LEFT:
                break;
            case RIGHT:
                graphics.drawLine(lastPoint.getX(), lastPoint.getY(),
                        lastPoint.getX() - Utils.BACKLASH, lastPoint.getY() - Utils.BACKLASH);
                graphics.drawLine(lastPoint.getX(), lastPoint.getY(),
                        lastPoint.getX() - Utils.BACKLASH, lastPoint.getY() + Utils.BACKLASH);
                break;
        }
    }
}
