package com.nerdygadgets.design;

import javax.swing.*;
import java.awt.*;

public class DesignPanel extends JPanel {

    public DesignPanel(){

        setPreferredSize(new Dimension(700, 500));
        setBackground(Color.white);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
