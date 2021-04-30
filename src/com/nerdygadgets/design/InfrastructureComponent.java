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

public abstract class InfrastructureComponent extends JLabel {
    private volatile int screenX = 0;
    private volatile int screenY = 0;
    private volatile int myX = 0;
    private volatile int myY = 0;
    private BufferedImage icon;
    JPanel parentPanel;


    public InfrastructureComponent(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        assignIcon();

        // Drag and drop functionality
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3){ // Right mouse button click
                    suicide();
                }
            }

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

                int newX = myX + deltaX;
                int newY = myY + deltaY;

                // Bottom boundary
                if(newX >= 0 && newX <= getParentPanelWidth()-getWidth() && newY >= 0){
                    setLocation(newX, getParentPanelHeight()-getHeight());
                }

                // Top boundary
                if(newX >= 0 && newX <= getParentPanelWidth()-getWidth() && newY <= getParentPanelHeight()-getHeight()){
                    setLocation(newX, 0);
                }

                // Left boundary
                if(newY >= 0 && newY <= getParentPanelHeight()-getHeight() && newX <= 0){
                    setLocation(0, newY);
                }

                // Right boundary
                if(newY >= 0 && newY <= getParentPanelHeight()-getHeight() && newX >= getParentPanelWidth()-getWidth()){
                    setLocation(getParentPanelWidth()-getWidth(), newY);
                }

                // Set location if cursor is inside all boundaries
                if(newX <= getParentPanelWidth()-getWidth() && newX >= 0 && newY >= 0 && newY <= getParentPanelHeight()-getHeight()){
                    setLocation(newX, newY);
                }

                // Redraw line(s)
                repaintParentPanel();
            }

            @Override
            public void mouseMoved(MouseEvent e) { }
        });
    }

    // Removes this JLabel from the DesignPanel
    public void suicide(){
        // Check if JLabel isnt a Firewall
        if(!(this instanceof Firewall)){
            parentPanel.remove(this);
            repaintParentPanel();
        }
    }

    public void repaintParentPanel(){
        parentPanel.repaint();
    }

    public void assignIcon(){
        try{
            // Determine icon
            if(this instanceof Firewall){
                icon = ImageIO.read(this.getClass().getResource("icons/firewall.png"));
            } else if(this instanceof DatabaseServer){
                icon = ImageIO.read(this.getClass().getResource("icons/databaseserver.png"));
            } else if(this instanceof WebServer){
                icon = ImageIO.read(this.getClass().getResource("icons/webserver.png"));
            }
        } catch(IOException e){
            System.err.println("File not found");
        }

        // Assign icon
        setIcon(new ImageIcon(icon));
        setBounds(0, 0, 64, 64);
        setOpaque(false);
    }

    public int getParentPanelWidth(){
        return parentPanel.getWidth();
    }

    public int getParentPanelHeight(){
        return parentPanel.getHeight();
    }
}
