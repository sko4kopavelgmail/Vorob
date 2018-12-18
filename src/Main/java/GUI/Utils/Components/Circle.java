package GUI.Utils.Components;

import GUI.Utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Setter @Getter
public class Circle {
    private int d;
    private int x;
    private int y;
    private Point rightConnection;
    private Point leftConnection;
    private Point center;
    private Graphics graphics;
    private String title;
    private int workTime;
    private boolean busy;
    private int statistic_workTime = 0;
    private int transact_count = 0;

    public void workTime_statistic_inc(){
        statistic_workTime++;
    }

    public void workTime_statistic_dec(){
        statistic_workTime--;
    }

    public void transact_count_int(){
        transact_count++;
    }

    public Circle( int d, int x, int y, String title) {
        this.d = d;
        this.x = x;
        this.y = y;
        this.title = title;
        init();
    }

    private void init() {
        busy = false;
        rightConnection = new Point(x+d,y+d/2);
        leftConnection = new Point(x,y+d/2);
        center = new Point(x+d/2,y+d/2);
    }

    public void paint(Graphics graphics){
        graphics.drawOval(x,y,d,d);
        graphics.drawString(title, center.getX()-Utils.BACKLASH, center.getY()+Utils.BACKLASH);
    }

    public void paint(Graphics graphics, int i){
        graphics.drawString(busy?"busy (" + i +"/"+workTime+")":"free",x-Utils.BACKLASH,y+Utils.SIZE+Utils.BACKLASH*2);
        graphics.drawOval(x,y,d,d);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Circle &&
                ((Circle) obj).x == this.x &&
                ((Circle) obj).y == this.y &&
                ((Circle) obj).title.equals(this.title);
    }
}
