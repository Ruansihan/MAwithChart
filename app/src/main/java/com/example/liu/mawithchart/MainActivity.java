package com.example.liu.mawithchart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity{

    Calendar calender;

    //static float HOCH_LEVEL=10.5f, NIEDER_LEVEL=9.95f;
    //static float OVER_HOCH_LEVEL=12.4f, OVER_NIEDER_LEVEL=7.85f;
    boolean HOCH_SCHALTER=false, NIEDER_SCHALTER=false;
    boolean OVER_HOCH_SCHALTER=false, OVER_NIEDER_SCHALTER=false;

    static int PUNKT;

    long preTime, nowTime;

    final String FILE_NAME = "/sensordata4MA";

    private SharedPreferences sharedPreferences;

    private SensorManager sensorManager;
    static int index;
    TextView textView, stepscount, runText, joggencount, email;

    String schritt, laufen;
    String steps, runs;

    static int STEPCOUNT, JOGGENCOUNT;

    double x;
    double y;
    double z;
    double tri;

    MovingAverage movingAverageTri;
    MovingAverage movingAverageTri2;
    MovingAverage movingAverageTri3;
    MovingAverage movingAverageTri4;

    Sensor acc;
    public SensorEventListener sensorEventListener;


    LineChart lineChart;
    ArrayList<String> xVals;
    ArrayList<Entry> yVals1;
    ArrayList<Entry> yVals2;
    ArrayList<Entry> yVals3;
    ArrayList<Entry> yVals4;
    ArrayList<Entry> yVals5;

    LineDataSet set1;
    LineDataSet set2;
    LineDataSet set3;
    LineDataSet set4;
    LineDataSet set5;

    ArrayList<LineDataSet> dataSets;

    boolean weekSchalter;
    int datum;
    int today;

    boolean JOGGEN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepscount = (TextView)findViewById(R.id.stepscount);
        joggencount = (TextView)findViewById(R.id.joggencount);

        sharedPreferences = getSharedPreferences("ruansihanDoc", 0);
        schritt = sharedPreferences.getString("ANZAHL", "0");
        laufen = sharedPreferences.getString("RUN_ANZAHL", "0");

        calender = Calendar.getInstance();
        datum = calender.get(Calendar.DAY_OF_WEEK)-1;

        weekSchalter = sharedPreferences.getBoolean("WEEK_SCHALTER", false);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(datum==1){
            if(weekSchalter){
                this.zuNull();
                this.newWeek();
                editor.putBoolean("WEEK_SCHALTER", false);
            }
        }else{
            editor.putBoolean("WEEK_SCHALTER", true);
        }

        today = sharedPreferences.getInt("HEUTER",0);
        if(today != datum){
            this.zuNull();
            //SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ANZAHL", steps);
            editor.putString("RUN_ANZAHL", runs);
            editor.putInt("HEUTER", datum);
        }
        editor.commit();


        //timer = new Timer();
        //timer.schedule(new ReportGenerator(), this.getTime(0), 1000 * 60 * 60 * 24);

        /**
         * calender = Calendar.getInstance();
         int date = calender.get(Calendar.DATE);

         reportDatum = sharedPreferences.getString("DATUM", "0");
         if(Integer.parseInt(reportDatum) == date){
         SharedPreferences.Editor editor = sharedPreferences.edit();
         editor.putString("DATUM", date+"");
         editor.commit();

         }else{
         int weekDay = calender.get(Calendar.DAY_OF_WEEK)-1;
         String TAG = weekDay+"";

         SharedPreferences.Editor editor = sharedPreferences.edit();
         editor.putString("DATUM", date+"");
         editor.commit();
         }
         */

        lineChart = (LineChart)findViewById(R.id.chart);
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMaxValue(30);
        yAxis.setAxisMinValue(0);
        yAxis = lineChart.getAxisRight();
        yAxis.setAxisMaxValue(30);
        yAxis.setAxisMinValue(0);

        xVals = new ArrayList<String>();
        yVals1 = new ArrayList<Entry>();
        yVals2 = new ArrayList<Entry>();
        yVals3 = new ArrayList<Entry>();
        yVals4 = new ArrayList<Entry>();
        yVals5 = new ArrayList<Entry>();
        set1 = new LineDataSet(yVals1, "");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(Color.BLUE);
        set1.setLineWidth(1f);
        set1.setCircleSize(1f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        set2 = new LineDataSet(yVals2, "");

        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setColor(ColorTemplate.getHoloBlue());
        set2.setCircleColor(Color.RED);
        set2.setLineWidth(1f);
        set2.setCircleSize(1f);
        set2.setFillAlpha(65);
        set2.setFillColor(ColorTemplate.getHoloBlue());
        set2.setHighLightColor(Color.rgb(244, 117, 117));
        set2.setDrawCircleHole(false);

        set3 = new LineDataSet(yVals3, "");

        set3.setAxisDependency(YAxis.AxisDependency.LEFT);
        set3.setColor(ColorTemplate.getHoloBlue());
        set3.setCircleColor(Color.GREEN);
        set3.setLineWidth(1f);
        set3.setCircleSize(1f);
        set3.setFillAlpha(65);
        set3.setFillColor(ColorTemplate.getHoloBlue());
        set3.setHighLightColor(Color.rgb(244, 117, 117));
        set3.setDrawCircleHole(false);

        set4 = new LineDataSet(yVals4, "");

        set4.setAxisDependency(YAxis.AxisDependency.LEFT);
        set4.setColor(ColorTemplate.getHoloBlue());
        set4.setCircleColor(Color.YELLOW);
        set4.setLineWidth(1f);
        set4.setCircleSize(1f);
        set4.setFillAlpha(65);
        set4.setFillColor(ColorTemplate.getHoloBlue());
        set4.setHighLightColor(Color.rgb(244, 117, 117));
        set4.setDrawCircleHole(false);

        set5 = new LineDataSet(yVals5, "");

        set5.setAxisDependency(YAxis.AxisDependency.LEFT);
        set5.setColor(ColorTemplate.getHoloBlue());
        set5.setCircleColor(Color.BLACK);
        set5.setLineWidth(1f);
        set5.setCircleSize(1f);
        set5.setFillAlpha(65);
        set5.setFillColor(ColorTemplate.getHoloBlue());
        set5.setHighLightColor(Color.rgb(244, 117, 117));
        set5.setDrawCircleHole(false);

        dataSets = new ArrayList<LineDataSet>();

        //sharedPreferences = getSharedPreferences("myDoc", 0);

        STEPCOUNT = Integer.parseInt(schritt);
        JOGGENCOUNT = Integer.parseInt(laufen);
        stepscount.setText(schritt);
        joggencount.setText(laufen);
    }

    public void start(View view){

        if(view.getId()==R.id.button2){
            //Log.i("ASD","true");
            JOGGEN = false;
        }else{
            //Log.i("ASD","false");
            JOGGEN = true;
        }

        textView = (TextView)findViewById(R.id.textView);
        runText = (TextView)findViewById(R.id.runText);

        movingAverageTri = new MovingAverage(5);
        movingAverageTri2 = new MovingAverage(15);
        movingAverageTri3 = new MovingAverage(20);
        movingAverageTri4 = new MovingAverage(50);

        Long tsLong = System.currentTimeMillis()/1000;
        while (System.currentTimeMillis()/1000-tsLong<3){ }

        if(!JOGGEN){
            textView.setText("Start!");
        }else{
            runText.setText("Start!");
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                tri = Math.sqrt(x * x + y * y + z * z);
                float mAtri1 = movingAverageTri.lowPass((float)tri);
                float mAtri2 = movingAverageTri2.lowPass((float)tri);
                float mAtri3 = movingAverageTri3.lowPass((float)tri);
                float mAtri4 = movingAverageTri4.lowPass((float)tri);

                if(!JOGGEN){
                    STEPCOUNT = schrittRechner(false, mAtri3, 10.5f, 9.95f, 12.4f, 7.85f, 388);
                    steps = STEPCOUNT+"";
                    stepscount.setText(steps);
                }else{
                    JOGGENCOUNT = schrittRechner(true, mAtri3, 13f, 8.8f, 18f, 5.5f, 900);
                    runs = JOGGENCOUNT+"";
                    joggencount.setText(runs);
                }


                xVals.add(index + "");
                yVals1.add(new Entry(mAtri1, index));
                yVals2.add(new Entry(mAtri2, index));
                yVals3.add(new Entry(mAtri3, index));
                yVals4.add(new Entry(mAtri4, index));
                yVals5.add(new Entry((float)tri, index));

                index++;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener, acc, SensorManager.SENSOR_DELAY_GAME);

    }


    public void stop(View view) throws IOException {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        //int ruansihan1 = sharedPreferences.getInt("DONNERSTAG", 0);
        //int ruansihan2 = sharedPreferences.getInt("FREITAG", 0);
        //Log.i("DD",ruansihan1+"");
        //Log.i("FF",ruansihan2+"");
        if(!JOGGEN){
            editor.putString("ANZAHL", steps);
            String weekday = this.statistik();
            editor.putInt(weekday, Integer.parseInt(steps));
            textView.setText("Stoped!");
        }else{
            editor.putString("RUN_ANZAHL", runs);

            runText.setText("   Stoped!");
        }

        editor.commit();

        sensorManager.unregisterListener(sensorEventListener);
        setData();

        lineChart.saveToGallery(FILE_NAME, 60);
    }

    public void initation(View view){
        this.zuNull();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ANZAHL", steps);
        editor.putString("RUN_ANZAHL", runs);
        //Die Anzähle am Tag nicht zurück
        editor.commit();
    }

    private void setData(){
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);
        dataSets.add(set4);
        dataSets.add(set5);

        LineData data = new LineData(xVals, dataSets);

        lineChart.setData(data);
    }

    public String statistik(){
        String tag ="";

        switch (datum){
            case 1: tag = "MONTAG"; break;
            case 2: tag = "DIENSTAG"; break;
            case 3: tag = "MITTWOCHE"; break;
            case 4: tag = "DONNERSTAG"; break;
            case 5: tag = "FREITAG"; break;
            case 6: tag = "SAMSTAG"; break;
            case 7: tag = "SONNTAG"; break;
        }
        return tag;
    }

    public void newWeek(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("MONTAG", 0);
        editor.putInt("DIENSTAG", 0);
        editor.putInt("MITTWOCHE", 0);
        editor.putInt("DONNERSTAG", 0);
        editor.putInt("FREITAG", 0);
        editor.putInt("SAMSTAG", 0);
        editor.putInt("SONNTAG", 0);
        editor.commit();
    }

    public void zuNull(){
        STEPCOUNT = 0;
        JOGGENCOUNT = 0;
        steps = "0";
        runs = "0";
        schritt = "0";
        laufen = "0";
        stepscount.setText("0");
        joggencount.setText("0");
    }

    public void emailsenden(View view){
        Intent i = new Intent(Intent.ACTION_SEND);
         i.setType("message/rfc822");
         i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"ruansihan@gmail.com"});
         i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
         i.putExtra(Intent.EXTRA_TEXT   , "body of email");
         try {
         startActivity(Intent.createChooser(i, "Send mail..."));
         } catch (Exception ex) {
         ex.printStackTrace();
         }
         /**
         *new Thread(){
        @Override
        public void run() {
        super.run();
        try {
        GMailSender sender = new GMailSender("ruansihan@gmail.com", "Maanchi23102");
        //Log.i("EMAIL","HIER!!!!!");
        sender.sendMail("This is Subject", "This is Body", "ruansihan@gmail.com", "ruansihan@hotmail.de");
        } catch (Exception e) {
        e.printStackTrace();
        }
        }
        };
         */

    }

    public int schrittRechner(Boolean joggen, float tri, float HOCH_LEVEL, float NIEDER_LEVEL, float OVER_HOCH_LEVEL, float OVER_NIEDER_LEVEL, long timeInterval){

        int COUNT=0;
        if(tri>HOCH_LEVEL){
            PUNKT=0;
            HOCH_SCHALTER=true;
        }
        if(tri>OVER_HOCH_LEVEL){
            OVER_HOCH_SCHALTER=true;
        }
        if(tri<NIEDER_LEVEL){
            NIEDER_SCHALTER=true;
        }
        if(tri<OVER_NIEDER_LEVEL){
            OVER_NIEDER_SCHALTER=true;
        }

        if(HOCH_SCHALTER && !OVER_HOCH_SCHALTER){
            if(tri<10f){
                PUNKT++;
                preTime=System.currentTimeMillis();
                HOCH_SCHALTER=false;
            }
        }
        if(NIEDER_SCHALTER && !OVER_NIEDER_SCHALTER){
            if(tri>10f){
                PUNKT++;
                nowTime=System.currentTimeMillis();
                NIEDER_SCHALTER=false;
            }
        }
        if(PUNKT==2 && (Math.abs(nowTime-preTime)<timeInterval)){
            //STEPCOUNT++;
            COUNT++;
            PUNKT=0;
        }
        if(OVER_HOCH_SCHALTER){
            if(tri<10f){
                HOCH_SCHALTER=false;
                OVER_HOCH_SCHALTER=false;
            }
        }
        if(OVER_NIEDER_SCHALTER){
            if(tri>10f){
                NIEDER_SCHALTER=false;
                OVER_NIEDER_SCHALTER=false;
            }
        }

        if(!joggen){
            return STEPCOUNT+COUNT;
        }else{
            return JOGGENCOUNT+COUNT;
        }
    }
}
