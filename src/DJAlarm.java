import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.Thread;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

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
        new javafx.embed.swing.JFXPanel(); //force jfx init
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
                                private String defaultAudioPath = getClass().getResource("res/Triangle Dinner Bell Sound Effect small.wav").toString();
                                Media mediaPath;
                                public void run()
                                {
                                    if (!(getAudioPath().equals("")))
                                    { //file selected
                                        System.out.println(getAudioPath());

                                        String quickPath = getAudioPath();
                                        if (quickPath.substring(0,4).equals("file"))
                                        {
                                            quickPath = quickPath.substring(5);
                                        }
                                        path = new File(quickPath);
                                        if (path.exists())
                                        {
                                            defaultAudio = false;
                                            mediaPath = new Media(path.toURI().toString());
                                        }
                                    }

                                    if (defaultAudio) {mediaPath = new Media(defaultAudioPath);}
                                    MediaPlayer audioStream = new MediaPlayer(mediaPath);
                                    System.out.println("awaiting stream availability");
                                    while (audioStream.getStatus().toString().equals("UNKNOWN")) {
                                        try
                                        {
                                            System.out.println("stream currently unavailable");
                                            Thread.sleep(1000);
                                        } catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                    System.out.println("Stream is ready");
                                    while (running)
                                    {
                                        try
                                        {
                                            audioStream.play();
                                            Thread.sleep((long)audioStream.getTotalDuration().toMillis());
                                        } catch (InterruptedException e)
                                        {
                                            audioStream.stop();
                                            running = false;
                                        } catch (Exception e)
                                        {
                                            running = false;
                                        }
                                        audioStream.stop();
                                    }
                                    audioStream.dispose();
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
                chooser.setFileFilter(new FileNameExtensionFilter(".wav, .mp3 files", "wav", "mp3"));
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

    //set methods
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
