package GUI.Utils.Components;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class Trace {
    private ArrayList<Point> points;

    public void addArrow(Arrow arrow) {
        if (points == null)
            points = new ArrayList<>();

        points.addAll(arrow.getPoints());
    }

    public Trace(ArrayList<Point> points){
        if (this.points == null)
            this.points = new ArrayList<>();
        this.points.addAll(points);
    }

}
