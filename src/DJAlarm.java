import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.Thread;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by djwolf on 9/30/2016.
 */
public class DJAlarm {
    private javax.swing.JPanel JPanel;
    private JCheckBox alarmOnCheckBox;
    private JButton setAlarmButton;
    private JButton setTrackButton;
    private JLabel Time;
    private static JFrame frame = new JFrame("DJAlarm");
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat getHour = new SimpleDateFormat("HH");
    private SimpleDateFormat getMinute = new SimpleDateFormat("mm");
    private static int alarmHour = 12;
    private static int alarmMinute = 0;
    private static boolean AM = true;
    private static boolean alarmON;
    private static String audioPath = "";


    public DJAlarm() {
        DJAlarm DJA = this;
        setAlarmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SetAlarm alarmDialog = new SetAlarm(alarmOnCheckBox,DJA);
                alarmDialog.setLocationRelativeTo(JPanel);
                alarmDialog.setVisible(true);
            }
        });

        //set application icon
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("res/UoZJe3T.png"));
        ImageIcon icon = new ImageIcon(image);
        frame.setIconImage(icon.getImage());


        //start main clock thread
        Thread clock = new Thread() {
            public void run() {
                while (true) {
                    Calendar cal = Calendar.getInstance();
                    String curHourSTR = getHour.format(cal.getTime());
                    String curMinuteSTR = getMinute.format(cal.getTime());
                    int curHour = Integer.parseInt(curHourSTR);
                    int curMinute = Integer.parseInt(curMinuteSTR);
                    String amPM;
                    if (curHour >= 12) {
                        amPM = "P";
                        if (curHour != 12)
                            curHour -= 12;
                    } else {
                        amPM = "A";
                        if (curHour == 0)
                            curHour = 12;
                    }
                    Time.setText(curHour + ":" + curMinuteSTR + " " + amPM + "M");

                    //is the alarm supposed to go off here?
                    if (alarmON)
                    {
                        boolean isAM;
                        if (amPM == "A")
                            isAM = true;
                        else
                            isAM = false;
                        if ((curHour == getAlarmHour()) && (curMinute == getAlarmMinute()) && (isAM == AM))
                        {
                            System.out.println("ALARM OFF!!!!!");
                            Thread audioLoop = new Thread()
                            {
                                public void run()
                                {
                                    File path = new File(getAudioPath());
                                    Clip clip = null;
                                    while (true)
                                    {
                                        try
                                        {
                                            clip = AudioSystem.getClip();
                                            clip.open(AudioSystem.getAudioInputStream(path));
                                            clip.start();
                                            Thread.sleep((long)(clip.getMicrosecondLength() * 0.001));
                                        } catch (Exception e)
                                        {
                                            System.out.println(e);
                                        }
                                    }
                                }
                            };
                            audioLoop.start();
                            alarmOnCheckBox.setSelected(false);
                            alarmStateChange();
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("An error has occurred.");
                        e.printStackTrace();
                    }
                    Time.setText(curHour + " " + curMinuteSTR + " " + amPM + "M");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("An error has occurred.");
                        e.printStackTrace();
                    }
                }
            }
        };
        clock.start();
        alarmOnCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                alarmStateChange();
            }
        });

        setTrackButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter(".wav files", "wav"));
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Select Alarm Audio");
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                    audioPath = chooser.getSelectedFile().toString();
            }
        });
    }

    public void alarmStateChange() {alarmON = alarmOnCheckBox.isSelected();}
    //set method
    public static void setAlarmHour(int h) {alarmHour = h;}

    public static void setAlarmMinute(int m) {alarmMinute = m;}

    public static void setAM(boolean b) {AM = b;}

    public static void setAudioPath(String audioP) {audioPath = audioP;}

    //get methods
    public static int getAlarmHour() {return alarmHour;}

    public static int getAlarmMinute() {return alarmMinute;}

    public static boolean getAM() {return AM;}

    public static String getAudioPath() {return audioPath;}

    public static void main(String[] args) {
        frame.setContentPane(new DJAlarm().JPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
