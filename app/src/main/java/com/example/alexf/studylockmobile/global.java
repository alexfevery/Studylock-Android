package com.example.alexf.studylockmobile;

import android.media.MediaPlayer;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class global
{
    public static OkHttpClient client;
    public static String[] SRD;
    //port :8001
    public static String url = "http://fevery.us";
    //public static String url ="http://fevery.us";
    public static boolean Correct = false;
    public static android.media.MediaPlayer CorrectSound;
    public static android.media.MediaPlayer WrongSound;

    public static String MakeRequest(String request, String pass, String responsetime, String useranswer, String userlanguage, String deckname, String existingforeign) throws java.io.IOException, java.lang.InterruptedException
    {
        //STUDYLOCK USER AND PASSWORD REQURED
            Request.Builder requeststruct = new Request.Builder().url(url+":8001").addHeader("user", "alexfevery@hotmail.com").addHeader("password", "****");
            requeststruct.post(new FormBody.Builder().add("request", request)
                    .add("pass", pass)
                    .add("responsetime", responsetime)
                    .add("useranswer", useranswer)
                    .add("userlanguage", userlanguage)
                    .add("deckname", deckname)
                    .add("existingforeign", existingforeign).build());
            if(!request.equals("DAI")){while(global.SRD == null){Thread.sleep(100);}}
            Response response = client.newCall(requeststruct.build()).execute();
            return response.body().string();
    }

}


