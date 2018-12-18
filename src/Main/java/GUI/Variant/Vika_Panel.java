package GUI.Variant;

import GUI.StatisticGUI.StatisticGUI;
import GUI.Utils.Components.*;
import GUI.Utils.Components.Point;
import GUI.Utils.Components.Queue;
import GUI.Utils.Utils;
import Statictic.Statistic;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Vika_Panel extends JPanel implements Runnable {

    //tools
    private boolean firstInit = true;
    private int currectRun;
    private int transactCount = 0;
    private int doneTransactCount = 0;
    private int declineCount = 0;
    private int genereteTime = 0;
    private int withPamv = 0;
    private int withoutPamv;
    //run time
    private int loop;
    //transacts
    private ArrayList<Transact> transacts;
    //components
    private Circle source;
    private Circle pamv;
    private Circle pc1;
    private Circle pc2;
    private Queue queue;
    private Triangle firstTriangle;
    private Triangle secondTriangle;
    private End end;
    //arrows
    private Arrow betweenSAndQ;
    private Arrow betweenQAndFT;
    private Arrow betweenFTAndPamv;
    private Arrow betweenFTAndPc2;
    private Arrow betweenPamvAndPc1;
    private Arrow betweenPc1AndST;
    private Arrow betweetPc2AndST;
    private Arrow betweenSTAndPamv;
    private Arrow betweenSTAndEnd;
    //traces
    private Trace fromStToFt = new Trace();
    private Trace fromFtToSt = new Trace();
    private Trace fromFtToEnd = new Trace();


    public Vika_Panel() {
        setBackground(Color.WHITE);
        transacts = new ArrayList<>();
        (new Thread(this)).start();
    }

    @Override
    public void run() {
        for (int i = 0; i < Utils.RUN_COUNT; i++) {
            currectRun = i + 1;
            loop = Utils.TIME;

            if (!firstInit) {
                pamv.setWorkTime((int) (18 + Math.random() * 1));
            }

            while (loop != 0) {
                super.repaint();
                try {
                    Thread.sleep(Utils.DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            reInit();
        }

        Statistic.instance.addTransactCount(transactCount);
        Statistic.instance.addDoneTransacts(doneTransactCount);
        Statistic.instance.addDeclineTransactCount(declineCount);
        Statistic.instance.addCircle(pc1);
        Statistic.instance.addCircle(pc2);
        Statistic.instance.addCircle(pamv);
        Statistic.instance.setWithPamv(withPamv);
        Statistic.instance.setWithoutPamv(withoutPamv);
        new StatisticGUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (firstInit)
            init();

        printMyComponents(g);
        printStatistic(g);

        //генерируем новый транзакт
        if (genereteTime == 0 || genereteTime % (int)(8 + Math.random()*2) == 0 || genereteTime > 10) {
            Transact newTransact = new Transact(firstTriangle.getRightConnection(), 1);
            newTransact.setWorkTime(-1);
            transacts.add(newTransact);
            transactCount++;
            genereteTime = 0;
        }

        genereteTime++;
        loop--;
        for (Transact transact : transacts) {

            //отрисовываем транзакт
            if (transact.getPosition().getX() < end.getX())
                transact.print(g, transacts.indexOf(transact));


            //первая развилка
            if (firstTriangle.getRightConnection().equals(transact.getPosition()) && transact.getWorkPlace() == null) {
                double i = Math.random();
                //определение куда идти
                if (i < 0.3) {
                    transact.setTrace(fromFtToEnd);
                    transact.setWorkPlace(pamv);
                    withPamv++;
                } else {
                    transact.setTrace(fromFtToSt);
                    transact.setWorkPlace(pc2);
                    withoutPamv++;
                }
                //можно ли идти прямо сейчас
                if (transact.getWorkPlace().isBusy()) {
                    queueCheck(transact);
                } else {
                    transact.getWorkPlace().setBusy(true);
                }
            }



            //попытка выйти из очереди
            if (queue.getCenter().equals(transact.getPosition())) {
                if (!transact.getWorkPlace().isBusy()) {
                    transact.setWait(false);
                    queue.currentTransactCountDec();
                    transact.setPosition(transact.getWorkPlace().getCenter());
                    transact.getWorkPlace().setBusy(true);
                }
            }



            //вторая развилка
            if (transact.getPosition().equals(secondTriangle.getRightConnection())) {
                double i = Math.random();
                if (i <= 0.2) {
                    transact.setWorkPlace(pamv);
                    transact.setTrace(fromFtToEnd);
                    if (pamv.isBusy()){
                        queueCheck(transact);
                    }else {
                        transact.setPosition(pamv.getCenter());
                        pamv.setBusy(true);
                    }
                }
            }

            //ускоряем транзакт после pc2
            if (transact.getPosition().equals(pc2.getRightConnection())){
                transact.setPosition(secondTriangle.getLeftConnection());
                transact.setTraceToNull();
            }


            //если ожидаем, то пропускаем все следующие шаги
            if (transact.isWait())
                continue;


            //если у транзакта нет своего пути, то просто смещаем его на шаг
            if (transact.getTrace() == null)
                transact.updateX(Transact.PLUS);
            else
                runTrace(transact, g);

            if (transact.getPosition().equals(end.getConnection())) {
                transact.setDone(true);
                doneTransactCount++;
            }

        }
    }


    private void printStatistic(Graphics g) {
        g.drawString("Оставшееся время " + loop, 10, 10);
        g.drawString("Отклоненные транзакты " + declineCount, 10, 20);
        g.drawString("Выполенные транзакты " + doneTransactCount, 10, 30);
        g.drawString("Состояние PAMV " + pamv.isBusy(), 10, 40);
        g.drawString("Состояние Pc1 " + pc1.isBusy(), 10, 50);
        g.drawString("Состояние Pc2 " + pc2.isBusy(), 10, 60);
        g.drawString("МОДЕЛИРОВАНИЕ № " + currectRun, Utils.WIDTH / 2, 10);
    }

    private void init() {
        source = new Circle(Utils.SIZE,
                (int) (Utils.WIDTH * 0.1),
                (int) (Utils.HEIGHT / 3), "И");

        queue = new Queue(
                new Point(
                        source.getRightConnection().getX() + Utils.DIFFERENCE,
                        source.getRightConnection().getY()));
        queue.setMaxTransactCount(3);
        firstTriangle = new Triangle(
                new Point(queue.getRightConnection().getX() + Utils.DIFFERENCE, queue.getRightConnection().getY() - Utils.SIZE / 2),
                new Point(queue.getRightConnection().getX() + Utils.DIFFERENCE, queue.getRightConnection().getY() + Utils.SIZE / 2),
                new Point(queue.getRightConnection().getX() + Utils.DIFFERENCE + Utils.SIZE, queue.getRightConnection().getY()),
                Triangle.DOWN
        );
        firstTriangle.setDirectPercent(30);
        pamv = new Circle(
                Utils.SIZE,
                firstTriangle.getRightConnection().getX() + Utils.DIFFERENCE,
                firstTriangle.getRightConnection().getY() - Utils.SIZE / 2,
                "ПП"
        );

        pc1 = new Circle(
                Utils.SIZE,
                pamv.getRightConnection().getX() + Utils.DIFFERENCE,
                pamv.getRightConnection().getY() - Utils.SIZE / 2,
                "PC1");
        secondTriangle = new Triangle(
                new Point(pc1.getRightConnection().getX() + Utils.DIFFERENCE, pc1.getRightConnection().getY() - Utils.SIZE / 2),
                new Point(pc1.getRightConnection().getX() + Utils.DIFFERENCE, pc1.getRightConnection().getY() + Utils.SIZE / 2),
                new Point(pc1.getRightConnection().getX() + Utils.DIFFERENCE + Utils.SIZE, pc1.getRightConnection().getY()),
                Triangle.UP);
        secondTriangle.setDirectPercent(80);
        end = new End(secondTriangle.getRightConnection().getX() + Utils.DIFFERENCE, secondTriangle.getRightConnection().getY());
        pc2 = new Circle(Utils.SIZE,
                pc1.getLeftConnection().getX(),
                pc1.getLeftConnection().getY() + Utils.DIFFERENCE,
                "PC2");

        //arrows
        betweenSAndQ = new Arrow(source.getRightConnection(), queue.getLeftConnection(), Arrow.RIGHT);
        betweenFTAndPamv = new Arrow(firstTriangle.getRightConnection(), pamv.getLeftConnection(), Arrow.RIGHT);
        betweenQAndFT = new Arrow(queue.getRightConnection(), firstTriangle.getLeftConnection(), Arrow.RIGHT);
        betweenPamvAndPc1 = new Arrow(pamv.getRightConnection(), pc1.getLeftConnection(), Arrow.RIGHT);
        betweenPc1AndST = new Arrow(pc1.getRightConnection(), secondTriangle.getLeftConnection(), Arrow.RIGHT);
        betweenSTAndEnd = new Arrow(secondTriangle.getRightConnection(), end.getConnection(), Arrow.RIGHT);
        ArrayList<Point> points = new ArrayList<>();
        points.add(firstTriangle.getRightConnection());
        points.add(new Point(firstTriangle.getRightConnection().getX(), pc2.getLeftConnection().getY()));
        points.add(pc2.getLeftConnection());
        betweenFTAndPc2 = new Arrow(points, Arrow.RIGHT);
        points = new ArrayList<>();
        points.add(pc2.getRightConnection());
        points.add(new Point(pc2.getRightConnection().getX() + Utils.BACKLASH * 2, pc2.getRightConnection().getY()));
        points.add(new Point(pc2.getRightConnection().getX() + Utils.BACKLASH * 2, pc1.getRightConnection().getY()));
        points.add(secondTriangle.getLeftConnection());
        betweetPc2AndST = new Arrow(points, Arrow.RIGHT);
        points = new ArrayList<>();
        points.add(secondTriangle.getRightConnection());
        points.add(new Point(secondTriangle.getRightConnection().getX(), secondTriangle.getRightConnection().getY() - Utils.DIFFERENCE));
        points.add(new Point(firstTriangle.getRightConnection().getX(), secondTriangle.getRightConnection().getY() - Utils.DIFFERENCE));
        points.add(firstTriangle.getRightConnection());
        betweenSTAndPamv = new Arrow(points, Arrow.DOWN);

        pamv.setWorkTime((int) (18 + Math.random() * 1));
        pc1.setWorkTime(27);
        pc2.setWorkTime(27);
        firstInit = false;
        fromFtToSt.addArrow(betweenFTAndPc2);
        fromFtToSt.addArrow(betweetPc2AndST);
        fromStToFt.addArrow(betweenSTAndPamv);
        fromFtToEnd.addArrow(betweenFTAndPamv);
        fromFtToEnd.addArrow(betweenPamvAndPc1);
        fromFtToEnd.addArrow(betweenPc1AndST);
        fromFtToEnd.addArrow(betweenSTAndEnd);
    }

    private void reInit() {
        transacts = new ArrayList<>();
        pc1.setBusy(false);
        pc2.setBusy(false);
        pamv.setBusy(false);
        queue.setCurrentTransactCount(0);
    }

    private void printMyComponents(Graphics g) {
        source.paint(g);
        queue.paint(g);
        firstTriangle.paint(g);
        pamv.paint(g);
        pc1.paint(g);
        secondTriangle.paint(g);
        end.paint(g);
        pc2.paint(g);

        //arrows
        betweenSAndQ.paint(g);
        betweenFTAndPamv.paint(g);
        betweenQAndFT.paint(g);
        betweenPamvAndPc1.paint(g);
        betweenPc1AndST.paint(g);
        betweenSTAndEnd.paint(g);
        betweenFTAndPc2.paint(g);
        betweetPc2AndST.paint(g);
        betweenSTAndPamv.paint(g);
    }

    private void runTrace(Transact transact, Graphics g) {
        if (transact.isDecline())
            return;

        Circle circle = transact.getWorkPlace();

        //если у нас в пути ничего нет, то помечаем его нулем и выходим
        if (transact.getTrace().getPoints().size() == 0) {
            transact.setTraceToNull();
            return;
        }

        if (circle != null) {

            transact.setPosition(circle.getCenter());

            for (int i = 0; i < transact.getTrace().getPoints().size(); i++) {
                if (transact.getPosition().getX() > transact.getTrace().getPoints().get(0).getX()) {
                    transact.deleteFirstTracePoint();
                    i--;
                }
            }


            if (circle.getCenter().equals(transact.getPosition())) {
                transact.workTimeInc();
                circle.paint(g, transact.getWorkTime());
                circle.workTime_statistic_inc();
                if (transact.getWorkTime() != circle.getWorkTime()) {
                    return;
                } else {
                    circle.transact_count_int();
                    circle.workTime_statistic_dec();
                    circle.setBusy(false);
                    transact.setWorkTime(0);
                    if (transact.getWorkPlace().equals(pamv)) {
                        transact.setWorkPlace(pc1);
                        if (pc1.isBusy()) {
                            queueCheck(transact);
                        } else {
                            transact.setPosition(pc1.getCenter());
                            pc1.setBusy(true);
                        }
                        return;
                    } else
                        transact.setWorkPlace(null);
                }
            }
        }


        //Если мы достигли какой-то точки из пути, нам нужно ее удалить из оставшегося пути
        if (transact.getTrace().getPoints().get(0).equals(transact.getPosition())) {
            transact.deleteFirstTracePoint();
        }

        if (transact.getTrace().getPoints().size() == 0) {
            transact.setTraceToNull();
            return;
        }

        if (transact.getTrace().getPoints().get(0).getX() > transact.getPosition().getX()) {
            transact.updateX(Transact.PLUS);
            return;
        }

        if (transact.getTrace().getPoints().get(0).getX() < transact.getPosition().getX()) {
            transact.updateX(Transact.MINUS);
            return;
        }

        if (transact.getTrace().getPoints().get(0).getY() > transact.getPosition().getY()) {
            transact.updateY(Transact.PLUS);
            return;
        }

        if (transact.getTrace().getPoints().get(0).getY() < transact.getPosition().getY()) {
            transact.updateY(Transact.MINUS);
        }
    }

    private void queueCheck(Transact transact){
        if (queue.getMaxTransactCount() > queue.getCurrentTransactCount()){
            transact.setWait(true);
            transact.setPosition(queue.getCenter());
            queue.currentTransactCountInc();
        }else {
            transact.setPosition(new Point(end.getX()+1,end.getY()));
            transact.setDecline(true);
            declineCount++;
        }
    }

}