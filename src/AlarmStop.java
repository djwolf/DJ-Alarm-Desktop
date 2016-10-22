import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Thread;
import java.lang.Math;
import java.util.InputMismatchException;

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

    public AlarmStop(Thread aLoop) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        audioLoop = aLoop;
        generateValues();
        setTitle("Stop Alarm");
        problemText.setText(firstValue + " + " + secondValue);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        /*
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        */

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        /*
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        */

        // call onCancel() on ESCAPE
        /*
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        */
    }

    private void generateValues()
    {
        firstValue = (int)Math.floor(Math.random() * 51);
        secondValue = (int)Math.floor(Math.random() * 51);
    }

    private void onOK() {
        int answer = -1;
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
            audioLoop.interrupt();
            dispose();
        } else {
            instructionText.setText("Wrong answer! Please try again");
            instructionText.setForeground(Color.red);
        }
    }

    /*
    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
    */

    public static void main(String[] args) {
        //AlarmStop dialog = new AlarmStop();
        //dialog.pack();
        //dialog.setVisible(true);
        //System.exit(0);
    }
}
