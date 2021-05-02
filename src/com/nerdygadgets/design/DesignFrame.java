package com.nerdygadgets.design;

import com.nerdygadgets.design.components.DatabaseServer;
import com.nerdygadgets.design.components.Firewall;
import com.nerdygadgets.design.components.InfrastructureComponent;
import com.nerdygadgets.design.components.WebServer;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class DesignFrame extends JFrame implements ActionListener, WindowStateListener {
    private JButton jbAddWebServer, jbAddDatabaseServer, jbSave;
    private DesignPanel designPanel;

    public DesignFrame() {
        setTitle("NerdyGadgets Infrastructure Design Tool");
        setSize(800, 600);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        jbSave = new JButton("Save File");
        jbSave.addActionListener(this);
        add(jbSave);

        jbAddWebServer = new JButton("Add Web Server");
        jbAddWebServer.addActionListener(this);
        add(jbAddWebServer);

        jbAddDatabaseServer = new JButton("Add Database Server");
        jbAddDatabaseServer.addActionListener(this);
        add(jbAddDatabaseServer);

        designPanel = new DesignPanel(this);
        add(designPanel);

        addWindowStateListener(this);

        setVisible(true);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == jbAddDatabaseServer){
            ComponentDialog cd = new ComponentDialog(this, designPanel, "databaseserver");
            if(cd.getIsOk()){
                InfrastructureComponent dbComponent = cd.getComponent();
                designPanel.add(dbComponent);
            }
        } else if (e.getSource() == jbAddWebServer){
            ComponentDialog cd = new ComponentDialog(this, designPanel, "webserver");
            if(cd.getIsOk()){
                InfrastructureComponent wComponent = cd.getComponent();
                designPanel.add(wComponent);
            }
        } else if(e.getSource() == jbSave){
            ArrayList<InfrastructureComponent> components = designPanel.getInfrastructureComponents();

            // TODO Create json file

            // TODO Save file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save your Infrastructure Design File");

            int userSelection = fileChooser.showSaveDialog(this);
            
            if(userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            }
        }
        designPanel.repaint();
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        System.out.println("a");
        designPanel.setResponsiveSize();
    }
}
