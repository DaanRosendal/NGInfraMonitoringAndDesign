package com.nerdygadgets.design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static javax.swing.JOptionPane.showMessageDialog;

public class OptimizationDialog extends JDialog implements ActionListener {
    private JLabel jlDesiredAvailability;
    private JTextField jtfDesiredAvailability, jtfCustomServerLimit;
    private JCheckBox jcCustomServerLimit;
    private JButton jbOk, jbCancel;
    private boolean isOk;
    private double desiredAvailability;
    private static final int defaultServerLimit = 10;
    private int customServerLimit;

    public OptimizationDialog(JFrame frame){
        super(frame,true);
        setTitle("Optimize Design");
        setSize(300, 150);
        setLayout(new GridLayout(3,2, 0, 5));

        jlDesiredAvailability = new JLabel("Desired Availability (%): ");
        add(jlDesiredAvailability);
        jtfDesiredAvailability = new JTextField(20);
        add(jtfDesiredAvailability);

        jcCustomServerLimit = new JCheckBox("Server Limit");
        jcCustomServerLimit.setSelected(false);
        add(jcCustomServerLimit);
        jcCustomServerLimit.addActionListener(this);
        jtfCustomServerLimit = new JTextField(20);
        jtfCustomServerLimit.setText("Default: " + defaultServerLimit);
        jtfCustomServerLimit.setEditable(false);
        add(jtfCustomServerLimit);

        jbOk = new JButton("Optimize");
        jbOk.addActionListener(this);
        add(jbOk);

        jbCancel = new JButton("Cancel");
        jbCancel.addActionListener(this);
        add(jbCancel);

        setLocationRelativeTo(frame); // Centers the frame
        setVisible(true);
    }

    public void setDesiredAvailability(double desiredAvailability) {
        this.desiredAvailability = desiredAvailability;
    }

    public double getDesiredAvailability() {
        return this.desiredAvailability;
    }

    public static int getDefaultServerLimit() {
        return defaultServerLimit;
    }

    public void setCustomServerLimit(int customServerLimit) {
        this.customServerLimit = customServerLimit;
    }

    public int getCustomServerLimit() {
        return customServerLimit;
    }

    public boolean isCustomServerLimitEnabled(){
        if(jcCustomServerLimit.isSelected()){
            return true;
        } else {
            return false;
        }
    }

    public boolean isOk() {
        return isOk;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbOk) {
            // Check if values are empty
            if (jtfDesiredAvailability.getText().equals("")) {
                showMessageDialog(this, "Desired availability is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else if(jtfCustomServerLimit.getText().equals("") && jcCustomServerLimit.isSelected()) {
                showMessageDialog(this, "Custom server limit is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Convert desired availability to a double with error handling
            try {
                setDesiredAvailability(Double.parseDouble(jtfDesiredAvailability.getText().replaceAll(",", ".")));
            } catch (NumberFormatException nfe) {
                showMessageDialog(this, "Invalid desired availability!", "Error", JOptionPane.ERROR_MESSAGE);
                jtfDesiredAvailability.setText("");
                return;
            }

            // Convert custom server limit to an int with error handling
            // if a custom server limit was given
            if(jcCustomServerLimit.isSelected()){
                try {
                    setCustomServerLimit(Integer.parseInt(jtfCustomServerLimit.getText()));
                } catch (NumberFormatException nfe) {
                    showMessageDialog(this, "Invalid server limit!", "Error", JOptionPane.ERROR_MESSAGE);
                    jtfCustomServerLimit.setText("");
                    return;
                }
            }

            // Check if desired availability is a reasonable value
            if (getDesiredAvailability() < 0 || getDesiredAvailability() > 100) {
                showMessageDialog(this, "Desired availability must be between 0% and 100%!", "Error", JOptionPane.ERROR_MESSAGE);
                jtfDesiredAvailability.setText("");
                return;
            }

            isOk = true;
            setVisible(false);
        } else if(e.getSource() == jcCustomServerLimit){
            if(jcCustomServerLimit.isSelected()){
                jtfCustomServerLimit.setEditable(true);
                jtfCustomServerLimit.setText("");
            } else {
                jtfCustomServerLimit.setEditable(false);
                jtfCustomServerLimit.setText("Default: " + defaultServerLimit);
            }
        } else if (e.getSource() == jbCancel) {
            isOk = false;
            setVisible(false);
        }
    }
}
