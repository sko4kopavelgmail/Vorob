package GUI.Dialog;

import GUI.MainFrame.Frame;
import GUI.Utils.Utils;

import javax.swing.*;
import java.awt.*;

public class Dialog extends JDialog {

    private JPanel grid;

    public Dialog() {
        setTitle("Моделирование");
        setSize(new Dimension(400, 200));
        setLocationRelativeTo(null);
        setResizable(false);

        //глобальные настройки
        grid = new JPanel();
        GridLayout layout = new GridLayout(3, 0, 5, 12);
        grid.setLayout(layout);

        //Задержка
        JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel delayLabel = new JLabel("Задержка моделирования (мс):");
        delayPanel.add(delayLabel);
        JTextField delayText = new JTextField(15);
        delayPanel.add(delayText);
        grid.add(delayPanel);

        //количество запусков
        JPanel runCountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel runCountLabel = new JLabel("Количество моделирований   :");
        runCountPanel.add(runCountLabel);
        JTextField runCountText = new JTextField(15);
        runCountPanel.add(runCountText);
        grid.add(runCountPanel);

        //Кнопочка
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton button = new JButton("Запуск");
        buttonPanel.add(button);
        grid.add(buttonPanel);

        button.addActionListener(l -> {
            if (!delayText.getText().isEmpty()) {
                Utils.DELAY = Integer.parseInt(delayText.getText());
            }

            if (!runCountText.getText().isEmpty()) {
                Utils.RUN_COUNT = Integer.parseInt(runCountText.getText());
            }

            new Frame();
            setVisible(false);
        });

        getContentPane().add(grid);
        setVisible(true);
    }


}
