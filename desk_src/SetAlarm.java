import javax.swing.*;
import java.awt.event.*;

public class SetAlarm extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSpinner setHour;
    private JSpinner setMinute;
    private JComboBox amPMSelect;
    private JCheckBox alarmONCheckBox;

    public SetAlarm(JCheckBox aON, DJAlarm DJA) {
        setContentPane(contentPane);
        setModal(true);
        setTitle("Set Alarm");
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK(aON,DJA);
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        //setup the spinner models
        SpinnerNumberModel hourModel = new SpinnerNumberModel(DJAlarm.getAlarmHour(),1,12,1);
        SpinnerNumberModel minuteModel = new SpinnerNumberModel(DJAlarm.getAlarmMinute(),0,59,1);
        setHour.setModel(hourModel);
        setMinute.setModel(minuteModel);

        //set the amPM current selection in the drop down
        if (DJAlarm.getAM())
            amPMSelect.setSelectedIndex(0);
        else
            amPMSelect.setSelectedIndex(1);
        pack();
    }

    private void onOK(JCheckBox aON, DJAlarm DJA) {
        DJAlarm.setAlarmHour((int)setHour.getValue());
        DJAlarm.setAlarmMinute((int)setMinute.getValue());
        if (amPMSelect.getSelectedIndex() == 0)
            DJAlarm.setAM(true);
        else
            DJAlarm.setAM(false);
        aON.setSelected(true);
        DJA.alarmStateChange();
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
