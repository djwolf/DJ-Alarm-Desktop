import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.Thread;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Created by djwolf on 9/30/2016.
**/

public class DJAlarm {
    private javax.swing.JPanel JPanel;
    private JCheckBox alarmOnCheckBox;
    private JButton setAlarmButton;
    private JButton setTrackButton;
    private JLabel Time;
    private JButton SettingsButton;
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

                    if (alarmON)
                    {
                        boolean isAM;
                        if (amPM == "A")
                            isAM = true;
                        else
                            isAM = false;
                        if ((curHour == getAlarmHour()) && (curMinute == getAlarmMinute()) && (isAM == AM))
                        {
                            Thread audioLoop = new Thread()
                            {
                                private boolean running = true;
                                private boolean defaultAudio = true;
                                private File path;
                                public void run()
                                {
                                    if (!(getAudioPath().equals("")))
                                    {
                                        String quickPath = getAudioPath();
                                        if (getAudioPath().substring(0,4).equals("file"))
                                        {
                                            quickPath = getAudioPath().substring(5);
                                        }
                                        path = new File(quickPath);
                                        if (path.exists())
                                        {
                                            defaultAudio = false;
                                        }
                                    }
                                    Clip clip= null;
                                    while (running)
                                    {
                                        InputStream defaultAudioStream = getClass().getResourceAsStream("res/Triangle Dinner Bell Sound Effect small.wav");
                                        InputStream bufferedStream = new BufferedInputStream(defaultAudioStream);
                                        try
                                        {
                                            clip = AudioSystem.getClip();
                                            if (defaultAudio)
                                            {
                                                clip.open(AudioSystem.getAudioInputStream(bufferedStream));
                                            } else {
                                                clip.open(AudioSystem.getAudioInputStream(path));
                                            }
                                            clip.start();
                                            Thread.sleep((long)(clip.getMicrosecondLength() * 0.001));
                                            Thread.sleep(500);
                                        } catch (InterruptedException e)
                                        {
                                            clip.stop();
                                            running = false;
                                        } catch (Exception e)
                                        {
                                            running = false;
                                        } finally
                                        {
                                            try {
                                                //drain streams
                                                defaultAudioStream.skip(defaultAudioStream.available());
                                                bufferedStream.skip(bufferedStream.available());
                                                //handle the clip
                                                clip.stop();
                                                clip.flush();
                                                clip.close();
                                                //close off all other streams
                                                bufferedStream.close();
                                                defaultAudioStream.close();
                                                defaultAudioStream = null;
                                                bufferedStream = null;
                                                clip = null;
                                                System.gc();
                                            } catch (Exception e) {}
                                        }
                                    }
                                }
                            };
                            audioLoop.start();
                            alarmOnCheckBox.setSelected(false);
                            alarmStateChange();
                            AlarmStop stopAlarm = new AlarmStop(audioLoop);
                            stopAlarm.setLocationRelativeTo(JPanel);
                            stopAlarm.pack();
                            stopAlarm.setVisible(true);
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
        SettingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Settings settingsDialog = new Settings();
                settingsDialog.setLocationRelativeTo(JPanel);
                settingsDialog.pack();
                settingsDialog.setVisible(true);
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
