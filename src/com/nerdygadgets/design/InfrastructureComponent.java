package com.nerdygadgets.design;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class InfrastructureComponent  extends JLabel {
    private volatile int screenX = 0;
    private volatile int screenY = 0;
    private volatile int myX = 0;
    private volatile int myY = 0;
    BufferedImage icon;
    JLabel jlIcon;

    public InfrastructureComponent(String componentType) {
        try{
            // Determine icon
            if(componentType.equals("firewall")){
                icon = ImageIO.read(this.getClass().getResource("icons/firewall.png"));
            } else if(componentType.equals("databaseserver")){
                icon = ImageIO.read(this.getClass().getResource("icons/databaseserver.png"));
            } else if(componentType.equals("webserver")){
                icon = ImageIO.read(this.getClass().getResource("icons/webserver.png"));
            }
        } catch(IOException e){
            System.err.println("File not found");
        }

        // Assign icon
        setIcon(new ImageIcon(icon));
        setBounds(0, 0, 64, 64);
        setOpaque(false);

        // Drag and drop functionality
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }

            @Override
            public void mousePressed(MouseEvent e) {
                screenX = e.getXOnScreen();
                screenY = e.getYOnScreen();

                myX = getX();
                myY = getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }
        });
        addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                int deltaX = e.getXOnScreen() - screenX;
                int deltaY = e.getYOnScreen() - screenY;

                setLocation(myX + deltaX, myY + deltaY);
            }

            @Override
            public void mouseMoved(MouseEvent e) { }
        });
    }

}
