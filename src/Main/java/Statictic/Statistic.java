package Statictic;

import GUI.Utils.Components.Circle;
import GUI.Utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Statistic {

    public static final Statistic instance = new Statistic();

    @Getter
    private double transactCount = 0;
    @Getter
    private double doneTransacts = 0;
    @Getter
    private ArrayList<Circle> circles;
    @Getter
    private int withPamv;
    @Getter
    private int withoutPamv;

    public void setWithPamv(int withPamv) {
        this.withPamv = withPamv / Utils.RUN_COUNT;
    }

    public void setWithoutPamv(int withoutPamv) {
        this.withoutPamv = withoutPamv/Utils.RUN_COUNT;
    }

    @Getter
    private int declineCount = 0;

    public void addCircle(Circle circle) {
        if (circles == null)
            circles = new ArrayList<>();
        circles.add(circle);
    }

    public void addDeclineTransactCount(int declineCount){
        this.declineCount += declineCount / Utils.RUN_COUNT;
    }

    public void addTransactCount(int transactCount) {
        this.transactCount += transactCount / Utils.RUN_COUNT;
    }

    public void addDoneTransacts(int doneTransacts) {
        this.doneTransacts += doneTransacts / Utils.RUN_COUNT;
    }
}
