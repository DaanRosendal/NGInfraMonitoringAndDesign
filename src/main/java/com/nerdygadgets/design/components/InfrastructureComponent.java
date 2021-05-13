package com.nerdygadgets.design.components;

import com.google.gson.annotations.Expose;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import static javax.swing.JOptionPane.showMessageDialog;

public abstract class InfrastructureComponent extends JLabel {
    private BufferedImage icon;
    private JPanel parentPanel;
    private volatile int screenX = 0, screenY = 0, myX = 0, myY = 0;
    @Expose
    private String name, type;
    @Expose
    private double availability, annualPrice;
    @Expose
    private int panelX, panelY;

    public InfrastructureComponent(JPanel parentPanel, String name, double availability, double annualPrice) {
        this.parentPanel = parentPanel;
        this.name = name;
        this.availability = availability;
        this.annualPrice = annualPrice;
        assignIconAndType();

        // Drag and drop functionality
        addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                screenX = e.getXOnScreen();
                screenY = e.getYOnScreen();

                myX = getX();
                myY = getY();

                // Remove this component if right mouse button is pressed
                if(e.getButton() == MouseEvent.BUTTON3){
                    suicide();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) { }

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
                // Remove component on right click
                if(e.getButton() == MouseEvent.BUTTON3){ // Right mouse button click
                    suicide();
                }

                // Drag and drop
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

    public InfrastructureComponent(JPanel parentPanel, String name, double availability, double annualPrice, int panelX, int panelY){
        this(parentPanel, name, availability, annualPrice);
        this.panelX = panelX;
        this.panelY = panelY;
        setLocation(panelX, panelY);
    }

    // Removes this JLabel from the DesignPanel
    public void suicide(){
        // Check if JLabel isnt a Firewall
        if(!(this instanceof Firewall)){
            parentPanel.remove(this);
            repaintParentPanel();
        }
    }



    public void assignIconAndType(){
        try{
            // Determine icon and type
            if(this instanceof Firewall){
                icon = ImageIO.read(this.getClass().getResource("/icons/firewall.png"));
                type = "Firewall";
            } else if(this instanceof DatabaseServer){
                icon = ImageIO.read(this.getClass().getResource("/icons/databaseserver.png"));
                type = "Database Server";
            } else if(this instanceof WebServer){
                icon = ImageIO.read(this.getClass().getResource("/icons/webserver.png"));
                type = "Web Server";
            } else {
                System.err.println("Invalid component type");
            }
        } catch(IOException e){
            System.err.println("File not found");
        } catch(Exception e){
            System.err.println("Something went wrong while loading the icons");
        }

        // Assign icon
        setIcon(new ImageIcon(icon));
        setBounds(getParentPanelWidth()/2, getParentPanelHeight()/2, 64, 64);
        setOpaque(false);
    }

    // Remove trailing zeros from a double, example: 90.0 becomes 90
    public String removeTrailingZeros(double number){
        if(number % 1 == 0){
            return String.format("%.0f", number);
        } else {
            return String.valueOf(number);
        }
    }

    // Getters & Setters
    public int getParentPanelWidth(){
        return parentPanel.getWidth();
    }
    public int getParentPanelHeight(){
        return parentPanel.getHeight();
    }
    public String getComponentName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public double getAvailability() {
        return availability;
    }
    public double getAnnualPrice() {
        return annualPrice;
    }
    public JPanel getParentPanel() {
        return parentPanel;
    }
    public int getPanelX() {
        return panelX;
    }
    public void setPanelX(int panelX) {
        this.panelX = panelX;
    }
    public int getPanelY() {
        return panelY;
    }
    public void setPanelY(int panelY) {
        this.panelY = panelY;
    }
    public void repaintParentPanel(){
        parentPanel.repaint();
    }

    @Override
    public String toString() {
        return name + " (" + removeTrailingZeros(availability) + "%) €" + removeTrailingZeros(annualPrice);
    }

    public static String convertToPriceFormat(String price){
        return String.format("%.2f", price);
    }
}
