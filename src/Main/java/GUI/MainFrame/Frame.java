package GUI.MainFrame;

import GUI.Utils.Utils;
import GUI.Variant.Vika_Panel;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame{

    public Frame(){
        setTitle("Обработка агрегатов");
        setSize(new Dimension(Utils.WIDTH,Utils.HEIGHT));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        getContentPane().add(new Panel());
        getContentPane().add(new Vika_Panel());
        setResizable(false);
        setVisible(true);
    }
}
