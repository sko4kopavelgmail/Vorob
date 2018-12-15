package GUI.Utils.Components;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor
public class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Point
                && ((Point) obj).getX() == this.getX()
                && ((Point) obj).getY() == this.getY();
    }
}
