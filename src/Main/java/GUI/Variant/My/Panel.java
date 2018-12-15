package GUI.Variant.My;

import GUI.StatisticGUI.StatisticGUI;
import GUI.Utils.Components.*;
import GUI.Utils.Components.Point;
import GUI.Utils.Components.Queue;
import GUI.Utils.Utils;
import Statictic.Statistic;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Panel extends JPanel implements Runnable {

    private int loop;
    int time = 0;
    //components
    private Circle source;
    private Triangle firstTriangle;
    private Circle k1;
    private Circle k2;
    private Circle k3;
    private Queue queue;
    private Triangle secondTriangle;
    private End end;
    private ArrayList<Transact> transacts;
    private Trace first = new Trace();
    private Trace second = new Trace();
    private Trace third = new Trace();
    private Trace fourth = new Trace();
    private Arrow betweenSAndFT;
    private Arrow betweenFTAndk1;
    private Arrow betweenK1AndQueue;
    private Arrow betweenQAndST;
    private Arrow betweenSTAndK2;
    private Arrow betweenSTAndK3;
    private Arrow betweenFTAndQ;
    private Arrow betweenK2AndEnd;
    private Arrow betweenK3AndEnd;
    //solution components
    private Queue solutionQueue;
    private Arrow betweenSAndSQ;
    private Arrow betweenSQAndFT;
    //***
    private boolean firstInit = true;
    private int transactCount = 0;
    private int doneTransactCount = 0;
    private int declineCount = 0;
    private int current_run;

    public Panel() {
        setBackground(Color.WHITE);
        transacts = new ArrayList<>();
        (new Thread(this)).start();
    }

    @Override
    public void run() {
        for (int i = 0; i < Utils.RUN_COUNT; i++) {
            current_run = i + 1;
            loop = Utils.TIME;

            //надо каждое новое моделирование менять время обработки, кроме первого раза. Они еще не инициализированы
            if (!firstInit) {
                k1.setWorkTime((int) (30 - Utils.DEVIATION + Math.random() * Utils.DEVIATION * 2));
                k2.setWorkTime((int) (30 - Utils.DEVIATION + Math.random() * Utils.DEVIATION * 2));
                k3.setWorkTime((int) (100 - Utils.DEVIATION + Math.random() * Utils.DEVIATION * 2));
            }

            //loop - время моделирования
            while (loop != 0) {
                super.repaint();
                try {
                    Thread.sleep(Utils.DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (Transact transact : transacts) {
                if (transact.getPosition().getX() > end.getX())
                    doneTransactCount++;
                if (transact.isDecline())
                    declineCount++;
            }
            reInit();
        }

        Statistic.instance.addTransactCount(transactCount);
        Statistic.instance.addDoneTransacts(doneTransactCount);
        Statistic.instance.addDeclineTransactCount(declineCount);
        Statistic.instance.addCircle(k1);
        Statistic.instance.addCircle(k2);
        Statistic.instance.addCircle(k3);
        new StatisticGUI();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (firstInit) {
            init();
        }
        printStatistic(g);
        printMyComponents(g);

        //генерируем новый транзакт
        if (time % (int) (Utils.GENERATE_TIME - Utils.DEVIATION + Math.random() * Utils.DEVIATION * 2) == 0 || time == 0) {
            Transact newTransact = new Transact(source.getRightConnection(), 1);
            transacts.add(newTransact);
            transactCount++;
            time = 0;
        }
        //если мы не сгенерировали вовремя, делаем это принудительно
        if (time > (int) (Utils.GENERATE_TIME - Utils.DEVIATION + Math.random() * Utils.DEVIATION * 2)) {
            Transact newTransact = new Transact(source.getRightConnection(), 1);
            transacts.add(newTransact);
            transactCount++;
            time = 0;
        }

        time++;
        loop--;

        for (Transact transact : transacts) {

            //отлисовываем транзакт
            if (transact.getPosition().getX() < end.getX())
                transact.print(g, transacts.indexOf(transact));


            //дополнительная логика в случае запуска решения
            if (Utils.IS_SOLUTION) {
                if (transact.getPosition().equals(solutionQueue.getCenter())) {
                    if (k1.isBusy()) {
                        if (solutionQueue.getMaxTransactCount() > solutionQueue.getCurrentTransactCount()) {
                            if (!transact.isWait()) {
                                transact.setWait(true);
                                solutionQueue.currentTransactCountInc();
                            }
                        } else {
                            System.out.println("Переполнение первой очереди");

                        }
                    } else {
                        transact.setWait(false);
                        solutionQueue.currentTransactCountDec();
                        transact.setPosition(firstTriangle.getRightConnection());
                    }
                }
            }

            //первая развилка
            if (transact.getPosition().equals(firstTriangle.getRightConnection())) {
                if (k1.isBusy()) {
                    transact.setTrace(second);
                    transact.setWorkPlace(null);
                    transact.setDecline(true);
                } else {
                    transact.setTrace(first);
                    transact.setWorkPlace(k1);
                    k1.setBusy(true);
                }

            }

            //на очереди проверяем состояния k2 и k3. если они заняты, надо ожидать
            if (transact.getPosition().equals(queue.getCenter())) {
                if ((transact.getPriority() == 2 && k2.isBusy()) || (transact.getPriority() == 1 && k3.isBusy()))
                    if (queue.getMaxTransactCount() >= queue.getCurrentTransactCount()) {
                        if (!transact.isWait()) {
                            transact.setWait(true);
                            queue.currentTransactCountInc();
                        }
                    } else {
                        System.out.println("Переполнение второй очереди");
                    }

                if ((transact.getPriority() == 2 && !k2.isBusy()) || (transact.getPriority() == 1 && !k3.isBusy())) {
                    transact.setWait(false);
                    queue.currentTransactCountDec();
                    transact.setPosition(secondTriangle.getRightConnection());
                }
            }

            //если ожидаем, то пропускаем все следующие шаги
            if (transact.isWait())
                continue;

            //вторая развилка
            if (transact.getPosition().equals(secondTriangle.getRightConnection())) {
                if (transact.getPriority() == 2) {
                    transact.setTrace(third);
                    transact.setWorkPlace(k2);
                    k2.setBusy(true);
                }
                if (transact.getPriority() == 1) {
                    transact.setTrace(fourth);
                    transact.setWorkPlace(k3);
                    k3.setBusy(true);
                }

            }

            //если у транзакта нет своего пути, то просто смещаем его на шаг
            if (transact.getTrace() == null)
                transact.updateX(Transact.PLUS);
            else
                runTrace(transact, transact.getWorkPlace(), g);

        }

        //ебаный костыль. Иногда (хуй пойми почему) канал не освобождается
        kostil();

    }

    private void kostil(){
        boolean bK1 = false;
        boolean bK2 = false;
        boolean bK3 = false;
        for (Transact transact: transacts){
            if (transact.getPosition().equals(k1.getCenter())){
                bK1 = true;
                break;
            }
            if (transact.getPosition().equals(k2.getCenter())){
                bK2 = true;
                break;
            }
            if (transact.getPosition().equals(k3.getCenter())){
                bK3 = true;
                break;
            }
        }
        if (bK1)
            k1.setBusy(false);
        if (bK2)
            k2.setBusy(false);
        if (bK3)
            k3.setBusy(false);
    }

    private void printStatistic(Graphics g) {
        String s;
        g.drawString("Время обработки k1: " + k1.getWorkTime(), 10, 20);
        g.drawString("Время обработки k2: " + k2.getWorkTime(), 10, 30);
        g.drawString("Время обработки k3: " + k3.getWorkTime(), 10, 40);
        s = "Оставшееся время: " + loop;
        g.drawString(s, 10, 10);
        g.drawString("МОДЕЛИРОВАНИЕ № " + current_run, Utils.WIDTH / 2, 10);
    }

    private void init() {
        source = new Circle(Utils.SIZE, (int) (Utils.HEIGHT * 0.1), (int) (Utils.WIDTH / 4), "И");

        if (Utils.IS_SOLUTION) {
            solutionQueue = new Queue(new Point(source.getRightConnection().getX() + Utils.DIFFERENCE, source.getRightConnection().getY()));
            solutionQueue.setMaxTransactCount(Utils.QUEUE_TRANSACT_COUNT);
            firstTriangle = new Triangle(
                    new Point(solutionQueue.getRightConnection().getX() + Utils.DIFFERENCE,
                            solutionQueue.getRightConnection().getY() - Utils.SIZE / 2),
                    new Point(solutionQueue.getRightConnection().getX() + Utils.DIFFERENCE,
                            solutionQueue.getRightConnection().getY() + Utils.SIZE / 2),
                    new Point(solutionQueue.getRightConnection().getX() + Utils.DIFFERENCE + Utils.SIZE,
                            solutionQueue.getRightConnection().getY()),
                    Triangle.NONE
            );
        } else {
            firstTriangle = new Triangle(
                    new Point(source.getRightConnection().getX() + Utils.DIFFERENCE,
                            source.getRightConnection().getY() - Utils.SIZE / 2),
                    new Point(source.getRightConnection().getX() + Utils.DIFFERENCE,
                            source.getRightConnection().getY() + Utils.SIZE / 2),
                    new Point(source.getRightConnection().getX() + Utils.DIFFERENCE + Utils.SIZE,
                            source.getRightConnection().getY()),
                    Triangle.NONE
            );

        }


        k1 = new Circle(Utils.SIZE, firstTriangle.getRightConnection().getX() + Utils.DIFFERENCE,
                firstTriangle.getRightConnection().getY() - Utils.SIZE / 2, "K1");
        queue = new Queue(new Point(k1.getRightConnection().getX() + Utils.DIFFERENCE, k1.getRightConnection().getY()));
        queue.setMaxTransactCount(150);
        secondTriangle = new Triangle(
                new Point(queue.getRightConnection().getX() + Utils.DIFFERENCE, queue.getRightConnection().getY() - Utils.SIZE / 2),
                new Point(queue.getRightConnection().getX() + Utils.DIFFERENCE, queue.getRightConnection().getY() + Utils.SIZE / 2),
                new Point(queue.getRightConnection().getX() + Utils.DIFFERENCE + Utils.SIZE, queue.getRightConnection().getY()),
                Triangle.NONE);
        k2 = new Circle(Utils.SIZE, secondTriangle.getRightConnection().getX() + Utils.DIFFERENCE,
                secondTriangle.getRightConnection().getY() - Utils.SIZE / 2, "К2");
        k3 = new Circle(Utils.SIZE, secondTriangle.getRightConnection().getX() + Utils.DIFFERENCE,
                secondTriangle.getRightConnection().getY() - Utils.SIZE * 2, "К3");
        end = new End(k2.getRightConnection().getX() + Utils.DIFFERENCE, k2.getRightConnection().getY());

        if (Utils.IS_SOLUTION) {
            betweenSAndSQ = new Arrow(source.getRightConnection(), solutionQueue.getLeftConnection(), Arrow.RIGHT);
            betweenSQAndFT = new Arrow(solutionQueue.getRightConnection(), firstTriangle.getLeftConnection(), Arrow.RIGHT);
        } else
            betweenSAndFT = new Arrow(source.getRightConnection(), firstTriangle.getLeftConnection(), Arrow.RIGHT);

        betweenFTAndk1 = new Arrow(firstTriangle.getRightConnection(), k1.getLeftConnection(), Arrow.RIGHT);
        betweenK1AndQueue = new Arrow(k1.getRightConnection(), queue.getLeftConnection(), Arrow.RIGHT);
        betweenQAndST = new Arrow(queue.getRightConnection(), secondTriangle.getLeftConnection(), Arrow.RIGHT);
        betweenSTAndK2 = new Arrow(secondTriangle.getRightConnection(), k2.getLeftConnection(), Arrow.RIGHT);
        ArrayList<Point> pointsK3 = new ArrayList<>();
        pointsK3.add(secondTriangle.getRightConnection());
        pointsK3.add(new Point(secondTriangle.getRightConnection().getX(), k3.getLeftConnection().getY()));
        pointsK3.add(k3.getLeftConnection());
        betweenSTAndK3 = new Arrow(pointsK3, Arrow.RIGHT);

        ArrayList<Point> pointsQueue = new ArrayList<>();
        pointsQueue.add(firstTriangle.getRightConnection());
        pointsQueue.add(new Point(firstTriangle.getRightConnection().getX(), firstTriangle.getRightConnection().getY() - Utils.SIZE));
        pointsQueue.add(new Point(queue.getLeftConnection().getX() - Utils.BACKLASH * 3, queue.getLeftConnection().getY() - Utils.SIZE));
        pointsQueue.add(new Point(queue.getLeftConnection().getX() - Utils.BACKLASH * 3, queue.getLeftConnection().getY()));
        pointsQueue.add(queue.getLeftConnection());
        betweenFTAndQ = new Arrow(pointsQueue, Arrow.RIGHT);

        betweenK2AndEnd = new Arrow(k2.getRightConnection(), end.getConnection(), Arrow.RIGHT);

        ArrayList<Point> pointsK3AndEnd = new ArrayList<>();
        pointsK3AndEnd.add(k3.getRightConnection());
        pointsK3AndEnd.add(new Point(k3.getRightConnection().getX() + Utils.BACKLASH * 3, k3.getRightConnection().getY()));
        pointsK3AndEnd.add(new Point(k3.getRightConnection().getX() + Utils.BACKLASH * 3, k2.getRightConnection().getY()));
        pointsK3AndEnd.add(end.getConnection());
        betweenK3AndEnd = new Arrow(pointsK3AndEnd, Arrow.RIGHT);

        first.addArrow(betweenFTAndk1);
        first.addArrow(betweenK1AndQueue);

        second.addArrow(betweenFTAndQ);

        third.addArrow(betweenSTAndK2);
        third.addArrow(betweenK2AndEnd);

        fourth.addArrow(betweenSTAndK3);
        fourth.addArrow(betweenK3AndEnd);

        firstInit = false;

        k1.setWorkTime((int) (30 - Utils.DEVIATION + Math.random() * Utils.DEVIATION * 2));
        k2.setWorkTime((int) (30 - Utils.DEVIATION + Math.random() * Utils.DEVIATION * 2));
        k3.setWorkTime((int) (100 - Utils.DEVIATION + Math.random() * Utils.DEVIATION * 2));
    }

    private void reInit() {
        transacts = new ArrayList<>();
        k1.setBusy(false);
        k2.setBusy(false);
        k3.setBusy(false);
        queue.setCurrentTransactCount(0);
        if (Utils.IS_SOLUTION)
            solutionQueue.setCurrentTransactCount(0);
    }

    private void printMyComponents(Graphics g) {
        g.setColor(Color.BLACK);
        source.paint(g);
        firstTriangle.paint(g);
        k1.paint(g);
        k2.paint(g);
        k3.paint(g);
        queue.paint(g);
        secondTriangle.paint(g);
        end.paint(g);
        if (Utils.IS_SOLUTION) {
            betweenSAndSQ.paint(g);
            betweenSQAndFT.paint(g);
            solutionQueue.paint(g);
        } else
            betweenSAndFT.paint(g);
        betweenFTAndk1.paint(g);
        betweenK1AndQueue.paint(g);
        betweenQAndST.paint(g);
        betweenSTAndK2.paint(g);
        betweenSTAndK3.paint(g);
        betweenFTAndQ.paint(g);
        betweenK2AndEnd.paint(g);
        betweenK3AndEnd.paint(g);
    }

    private void runTrace(Transact transact, Circle circle, Graphics g) {
        //если у нас в пути ничего нет, то помечаем его нулем и выходим
        if (transact.getTrace().getPoints().size() == 0) {
            transact.setTraceToNull();
            return;
        }

        if (circle != null) {

            if (k1.equals(circle)) {
                transact.setPriority(2);
                transact.setPosition(circle.getCenter());
            }
            if (k2.equals(circle)) {
                transact.setPosition(circle.getCenter());
            }
            if (k3.equals(circle)) {
                transact.setPosition(circle.getCenter());
            }

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
                    circle.setBusy(false);
                    transact.setWorkTime(0);
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
}
