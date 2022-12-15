package ru.vsu.cs.course2.g9.oop.poltavskii_r_a.dominoes;

import java.awt.*;

public class Main {
    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setSize(1024, 768);
                frame.setLocation(100, 50);//?!
                frame.setVisible(true);
            }
        });
    }
}