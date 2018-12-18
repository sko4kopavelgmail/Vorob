package GUI.Utils.Components;

import GUI.Utils.Utils;
import lombok.Data;

import java.awt.*;

@Data
public class Triangle {

    private Point leftTop;
    private Point leftDown;
    private Point right;
    private Point leftConnection;
    private Point rightConnection;
    private int direction;
    private int directPercent;
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int NONE = 3;


    public Triangle(Point leftTop, Point leftDown, Point right, int direction) {
        this.leftTop = leftTop;
        this.leftDown = leftDown;
        this.right = right;
        this.direction = direction;
        init();
    }

    private void init() {
        leftConnection = new Point(leftDown.getX(), right.getY());
        rightConnection = right;

    }

    public void paint(Graphics graphics) {
        graphics.drawPolygon(
                new int[]{leftTop.getX(), leftDown.getX(), right.getX()},
                new int[]{leftTop.getY(), leftDown.getY(), right.getY()},
                3);
        switch (direction) {
            case UP:
                graphics.drawString(directPercent+"", rightConnection.getX(), rightConnection.getY()+Utils.SIZE/3);
                graphics.drawString((100 - directPercent)+"", rightConnection.getX()-Utils.SIZE/2, rightConnection.getY() - Utils.SIZE/3);
                break;
            case DOWN:
                graphics.drawString(directPercent+"", rightConnection.getX(), rightConnection.getY()-Utils.BACKLASH);
                graphics.drawString((100 - directPercent)+"", rightConnection.getX()-Utils.SIZE/2, rightConnection.getY() + Utils.SIZE/2);
                break;
            case NONE:
                break;
        }
    }
}

