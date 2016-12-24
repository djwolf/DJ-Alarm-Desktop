package com.djsoft.djalarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.Thread;
import java.util.Arrays;

public class DJA_MAIN extends AppCompatActivity {
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat getHour = new SimpleDateFormat("HH");
    private SimpleDateFormat getMinute = new SimpleDateFormat("mm");
    private TextView timeText;
    private String[] curTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dja_main);
        timeText = (TextView) findViewById(R.id.timeText);
        curTimeText = new String[]{"12", "00", "A"};
        DJA_MAIN_FUNC();
    }

    private void updateClock(final boolean semiOn)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (semiOn)
                {
                    timeText.setText(curTimeText[0] + ":" + curTimeText[1] + " " + curTimeText[2] + "M");
                } else {
                    timeText.setText(curTimeText[0] + " " + curTimeText[1] + " " + curTimeText[2] + "M");
                }
            }
        });
    }

    public void DJA_MAIN_FUNC() {
        new Thread()
        {
            public void run()
            {
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
                    curTimeText[0] = "" + curHour;
                    curTimeText[1] = "" + curMinuteSTR;
                    curTimeText[2] = amPM;
                    updateClock(true);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateClock(false);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}