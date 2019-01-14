package com.example.alexf.studylockmobile;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        global.client = new OkHttpClient();
        try {
            Main();
        } catch (Exception e) {
            int line = e.getStackTrace()[2].getLineNumber();
            int ttt=0;
        }
    }
    public void onClick(View v) {if(((Button)findViewById(R.id.button)).getText().equals("Study")){startActivity(new Intent(this, StudyScreen.class));}}

    public void Main() throws java.io.IOException, java.text.ParseException, java.lang.InterruptedException
    {
        LoadStuff();
        Card.LoadCards(true);
    }


    public void LoadStuff() throws java.io.IOException, java.text.ParseException, java.lang.InterruptedException
    {
        new Thread(new Runnable() {public void run() {try
        {
            global.SRD = global.client.newCall(new Request.Builder().url(global.url+"/StudyLock/studylockfiles/srd.cfg").build()).execute().body().string().split("\r\n");;
            global.CorrectSound = new MediaPlayer();
            global.CorrectSound.setDataSource("http://freesound.org/data/previews/131/131660_2398403-lq.mp3");
            global.CorrectSound.prepare();
            global.WrongSound = new MediaPlayer();
            global.WrongSound.setDataSource("http://freesound.org/data/previews/342/342756_5260872-lq.mp3");
            global.WrongSound.setVolume(100,100);
            global.WrongSound.prepare();
            while(Card.c1 == null || Card.c2 == null){Thread.sleep(1000);}
            runOnUiThread(new Runnable() {public void run() {((Button)findViewById(R.id.button)).setText("Study");}});

        }
        catch(Exception e){}}}).start();

        ((TextView)findViewById(R.id.reg)).setText("Hello alexfevery@hotmail.com");
        String[] result = global.MakeRequest("DAI","","","","","","").split("↔");
        DateFormat DF = new SimpleDateFormat("{yyyy-MM-dd_HH:mm:ss}");
        Date d1 = DF.parse(result[0]);
        ((TextView)findViewById(R.id.reg1)).setText("Registered:");
        ((TextView)findViewById(R.id.dat1)).setText(d1.toString().split(" ")[1] +" "+ d1.toString().split(" ")[2]+",  "+d1.toString().split(" ")[5]);
        int day = (int)TimeUnit.SECONDS.toDays(Long.parseLong(result[1]));
        long hours = TimeUnit.SECONDS.toHours(Long.parseLong(result[1])) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(Long.parseLong(result[1])) - (TimeUnit.SECONDS.toHours(Long.parseLong(result[1]))* 60);
        ((TextView)findViewById(R.id.reg2)).setText("Last Session:");
        ((TextView)findViewById(R.id.dat2)).setTextColor(Color.GREEN);
        if(hours > 2){((TextView)findViewById(R.id.dat2)).setTextColor(Color.YELLOW);}
        if(day > 1){((TextView)findViewById(R.id.dat2)).setTextColor(Color.RED);}
        ((TextView)findViewById(R.id.dat2)).setText(day+" days "+hours+" hours "+minute+" minutes");
        ((TextView)findViewById(R.id.reg3)).setText("Total Account Cards:");
        ((TextView)findViewById(R.id.dat3)).setText(result[2]);
        ((TextView)findViewById(R.id.reg4)).setText("Total Mastered Cards:");
        ((TextView)findViewById(R.id.dat4)).setText(result[3]);
        ((TextView)findViewById(R.id.reg5)).setText("Total Cards Answered:");
        ((TextView)findViewById(R.id.dat5)).setText(result[4]);
        ((TextView)findViewById(R.id.reg6)).setText("Total Correct Answers:");
        ((TextView)findViewById(R.id.dat6)).setText(result[5]);
        ((TextView)findViewById(R.id.reg7)).setText("Total Incorrect Answers:");
        ((TextView)findViewById(R.id.dat7)).setText(result[6]);
        ((TextView)findViewById(R.id.reg8)).setText("Accuracy ratio:");
        ((TextView)findViewById(R.id.dat8)).setText(result[7]);
    }



}
