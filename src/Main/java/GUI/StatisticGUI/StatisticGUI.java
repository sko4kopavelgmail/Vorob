package GUI.StatisticGUI;

import GUI.Utils.Components.Circle;
import GUI.Utils.Utils;
import Statictic.Statistic;

import javax.swing.*;

public class StatisticGUI {

    public StatisticGUI() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Статистика");
        dialog.setSize(400, 370);
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.append("Количество моделирований: " + Utils.RUN_COUNT + "\n");
        textArea.append("Общее время моделирования: " + Utils.TIME*Utils.RUN_COUNT+"\n");
        textArea.append("Среднее количество сгенерированных транзактов = " + Statistic.instance.getTransactCount() + "\n");
        textArea.append("Среднее количество отклоненных транзактов = " + Statistic.instance.getDeclineCount() + "\n");
        textArea.append("Среднее количество обработанных транзактов = " + Statistic.instance.getDoneTransacts() + "\n");
        textArea.append("\n");
        for (Circle circle : Statistic.instance.getCircles()) {
            textArea.append("\t" + circle.getTitle() + "\n");
            textArea.append("Среднее время работы  :  " + circle.getStatistic_workTime() / Utils.RUN_COUNT + "\n");
            double i = (circle.getStatistic_workTime()/((double)Utils.RUN_COUNT * Utils.TIME)) * 100;
            if (i > 100)
                i = 100;
            textArea.append("Среднее время работы (%) :  " + String.format("%.2f%n", i));
            textArea.append("Среднее количество транзактов  :  " + circle.getTransact_count() / Utils.RUN_COUNT + "\n");
            textArea.append("****\n");
        }
        dialog.add(textArea);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
