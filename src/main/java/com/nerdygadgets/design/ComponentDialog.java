package com.nerdygadgets.design;

import com.nerdygadgets.design.components.DatabaseServer;
import com.nerdygadgets.design.components.InfrastructureComponent;
import com.nerdygadgets.design.components.WebServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ComponentDialog extends JDialog implements ActionListener {
    private JLabel jlComponent;
    private JComboBox jcComponent;
    private InfrastructureComponent[] components;
    private JButton jbOK, jbCancel;
    private boolean isOK;

    public ComponentDialog(JFrame frame, JPanel parentPanel, String componentType){
        super(frame, true);
        setTitle("Choose component");
        setSize(350,200);
        setResizable(false);
        setLayout(new FlowLayout());

        if(componentType.equals("databaseserver")){
            jlComponent = new JLabel("Database Server");
            DatabaseServer db1 = new DatabaseServer(parentPanel, "HAL9001DB", 90, 5100);
            DatabaseServer db2 = new DatabaseServer(parentPanel, "HAL9002DB", 95, 7700);
            DatabaseServer db3 = new DatabaseServer(parentPanel, "HAL9003DB", 98, 12200);
            components = new InfrastructureComponent[]{db1, db2, db3};
        } else if(componentType.equals("webserver")){
            jlComponent = new JLabel("Web Server");
            WebServer w1 = new WebServer(parentPanel, "HAL9001W", 80, 2200);
            WebServer w2 = new WebServer(parentPanel, "HAL9002W", 90, 3200);
            WebServer w3 = new WebServer(parentPanel, "HAL9003W", 95, 5100);
            components = new InfrastructureComponent[]{w1, w2, w3};
        }
        add(jlComponent);
        jcComponent = new JComboBox(components);
        add(jcComponent);

        jbOK = new JButton("OK");
        add(jbOK);
        jbOK.addActionListener(this);

        jbCancel = new JButton("Cancel");
        add(jbCancel);
        jbCancel.addActionListener(this);

        pack();

        setLocationRelativeTo(frame); // Centers the frame
        setVisible(true);
    }

    public boolean getIsOk() {
        return isOK;
    }

    public InfrastructureComponent getComponent(){
        return (InfrastructureComponent) jcComponent.getItemAt(jcComponent.getSelectedIndex());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbOK) {
            isOK = true;
        } else {
            isOK = false;
        }
        setVisible(false);
    }
}
