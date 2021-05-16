import com.nerdygadgets.design.DesignFrame;
import com.nerdygadgets.monitoring.MonitoringFrame;
import com.nerdygadgets.monitoring.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {
    private JButton jbMonitoring, jbDesign;
    private JFrame monitoringFrame, designFrame;

    public MainFrame() {
        setTitle("NerdyGadgets Infra Monitoring & Design");
        setSize(600, 300);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        jbMonitoring = new JButton("Monitoring");
        jbMonitoring.addActionListener(this);
        add(jbMonitoring);

        jbDesign = new JButton("Design");
        jbDesign.addActionListener(this);
        add(jbDesign);

        setVisible(true);
        setLocationRelativeTo(null); // centers the frame
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbMonitoring) {
            // Check if frame has been instantiated
            if(monitoringFrame == null){
                monitoringFrame = new MonitoringFrame(new Server("WS1", "192.168.2.2"), new Server("WS2", "192.168.2.3"), new Server("DB1", "192.168.3.2"), new Server("DB2", "192.168.3.3"));
            } else {
                monitoringFrame.toFront();
                monitoringFrame.setVisible(true);
            }
        } else if (e.getSource() == jbDesign) {
            // Check if frame has been instantiated
            if(designFrame == null){
                designFrame = new DesignFrame();
            } else {
                designFrame.toFront();
                designFrame.setVisible(true);
            }
        }
    }
}