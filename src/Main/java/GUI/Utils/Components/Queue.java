package GUI.Utils.Components;

import GUI.Utils.Utils;
import lombok.Data;

import java.awt.*;

@Data
public class Queue {
    private Point leftConnection;
    private Point rightConnection;
    private Point center;
    private int maxTransactCount;
    private int currentTransactCount;

    public void currentTransactCountInc(){
        currentTransactCount++;
    }

    public void currentTransactCountDec(){
        if (currentTransactCount > 0)
        currentTransactCount--;
    }

    public Queue(Point leftConnection) {
        this.leftConnection = new Point(leftConnection.getX(),leftConnection.getY());
        init();
    }

    private void init(){
        rightConnection = new Point();
        rightConnection.setX(leftConnection.getX()+Utils.SIZE);
        rightConnection.setY(leftConnection.getY());
        center = new Point(leftConnection.getX()+Utils.SIZE/2,leftConnection.getY());
    }

    public void paint(Graphics graphics){
        graphics.drawString(currentTransactCount+"/"+maxTransactCount,center.getX()-Utils.BACKLASH,center.getY()-Utils.SIZE/2-Utils.BACKLASH);
        graphics.drawPolyline(
                new int[]{
                        leftConnection.getX()-Utils.BACKLASH,
                        leftConnection.getX()+Utils.SIZE,
                        leftConnection.getX()+Utils.SIZE,
                        leftConnection.getX()-Utils.BACKLASH,
                },
                new int[]{
                        leftConnection.getY()-Utils.SIZE/2,
                        leftConnection.getY()-Utils.SIZE/2,
                        leftConnection.getY()+Utils.SIZE/2,
                        leftConnection.getY()+Utils.SIZE/2,

                },
                4);
        graphics.drawLine(leftConnection.getX(),leftConnection.getY()-Utils.SIZE/2,
                leftConnection.getX(),leftConnection.getY()+Utils.SIZE/2);

        graphics.drawLine(leftConnection.getX()+Utils.SIZE/4,leftConnection.getY()-Utils.SIZE/2,
                leftConnection.getX()+Utils.SIZE/4,leftConnection.getY()+Utils.SIZE/2);

        graphics.drawLine(leftConnection.getX()+Utils.SIZE/2,leftConnection.getY()-Utils.SIZE/2,
                leftConnection.getX()+Utils.SIZE/2,leftConnection.getY()+Utils.SIZE/2);

    }
}