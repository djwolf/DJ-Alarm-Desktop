import javax.swing.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

public class Settings extends JDialog {
    public static String ID1 = "DJA_DIFF";
    public static String ID2 = "DJA_PASS";
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox difficultyBar;
    private JTextField textField1;
    private JLabel passBlankLabel;
    private static Preferences prefs;

    public Settings() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        prefs = Preferences.userRoot().node(this.getClass().getName());

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
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
    }

    public static void prefs_setDiff(int value) {prefs.putInt(ID1,value);}

    public static int prefs_getDiff() {return prefs.getInt(ID1,-1);}

    public static void prefs_setPass(String value) {prefs.put(ID2,value);}

    public static String prefs_getPass() {
        try
        {
            System.out.println(prefs.get(ID2,""));
            return prefs.get(ID2,"");
        } catch (NullPointerException e)
        {
            return "";
        }
    }

    private void onOK() {
        String password = textField1.getText();
        int difficulty = difficultyBar.getSelectedIndex();
        prefs_setDiff(difficulty);
        prefs_setPass(password);
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
