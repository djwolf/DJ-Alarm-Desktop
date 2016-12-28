import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Thread;
import java.lang.Math;
import java.util.prefs.Preferences;

public class AlarmStop extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel instructionText;
    private JTextField problemEntry;
    private JLabel problemText;
    private JButton buttonCancel;
    private Thread audioLoop;
    private int firstValue;
    private int secondValue;
    private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
    private ActionListener mathConfirm = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            onOK(true);
        }
    };
    private ActionListener passConfirm = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            onOK(false);
        }
    };

    public AlarmStop(Thread aLoop) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        audioLoop = aLoop;
        generateValues();
        setTitle("Stop Alarm");
        problemText.setText(firstValue + " + " + secondValue);

        buttonOK.addActionListener(mathConfirm);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void generateValues()
    {
        firstValue = (int)Math.floor(Math.random() * 51);
        secondValue = (int)Math.floor(Math.random() * 51);
    }

    private void onOK(boolean mode) {
        if (mode)
        { //math
            int answer;
            try {
                answer = Integer.parseInt(problemEntry.getText());
            } catch (NumberFormatException e)
            {
                instructionText.setText("Please enter a numerical value");
                instructionText.setForeground(Color.red);
                return;
            }
            if (answer == (firstValue + secondValue))
            {
                buttonOK.removeActionListener(mathConfirm);
                if (!(Settings.prefs_getPass().equals("")))
                {
                    instructionText.setText("Please enter the password you have set");
                    instructionText.setForeground(Color.black);
                    buttonOK.addActionListener(passConfirm);
                    problemText.setText("");
                    problemEntry.setText("");
                } else {
                    stopAlarm();
                }
            } else {
                instructionText.setText("Wrong answer! Please try again");
                instructionText.setForeground(Color.red);
            }
        } else { //password
            if (problemEntry.getText().equals(Settings.prefs_getPass()))
            {
                stopAlarm();
            } else {
                instructionText.setText("Wrong password!");
                instructionText.setForeground(Color.red);
            }
        }
    }

    public void stopAlarm()
    {
        dispose();
        audioLoop.interrupt();
    }
}
