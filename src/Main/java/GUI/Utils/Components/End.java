package GUI.Utils.Components;

import GUI.Utils.Utils;
import lombok.Data;

import java.awt.*;

@Data
public class End {
    private Point connection;
    private int x;
    private int y;


    public End(int x, int y) {
        this.x = x;
        this.y = y;
        init();
    }

    private void init() {
        connection = new Point(x, y);
    }

    public void paint(Graphics graphics) {
        graphics.drawLine(x,y- Utils.SIZE/2,x,y+Utils.SIZE/2);
    }
}
