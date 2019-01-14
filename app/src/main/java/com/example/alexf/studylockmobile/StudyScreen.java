package com.example.alexf.studylockmobile;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StudyScreen extends AppCompatActivity {

    public Boolean wrong = false;
    public Boolean CurrentlyAudio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_screen);
        ((EditText)findViewById(R.id.foreignbox)).setTextColor(Color.RED);

        ((EditText)findViewById(R.id.abox)).addTextChangedListener(new TextWatcher()
        {
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            public void onTextChanged(CharSequence s, int start,int before, int count) {}
            public void afterTextChanged(Editable s) {CheckAnswer(false);}
        });
        Start(true);
    }
    public void onClick1(View v){CheckAnswer(true);}
    public void onClick2(View v)
    {
       Card.c1.MediaPlayer.start();
    }

    public void Start(Boolean FirstCard)
    {
        wrong = false;
        try
        {
            Card.LoadCards(FirstCard);
            if(Card.c1.MediaPlayer != null)
            {
                CurrentlyAudio = true;
                SetFields(true);
                Card.c1.MediaPlayer.start();
            }
            else
            {
                CurrentlyAudio = false;
                SetFields(false);
            }
            if(!Card.c1.ExtraForeign.equals("")){Toast.makeText(this, Card.c1.ExtraForeign,Toast.LENGTH_LONG).show();}
        }
        catch (Exception e)
        {
            int line = e.getStackTrace()[2].getLineNumber();
            int ttt=0;
        }
    }

    private String cap(final String line) { return Character.toUpperCase(line.charAt(0)) + line.substring(1);}

    public void SetFields(Boolean AudioQ)
    {
        Card t1 = Card.c1;
        ((TextView)findViewById(R.id.qlang)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.qlang)).setText(cap(Card.c1.QuestionLanguage));
        ((TextView)findViewById(R.id.alang)).setText(cap(Card.c1.AnswerLanguage));
        ((EditText)findViewById(R.id.qbox)).setText(Card.c1.Question);
        ((EditText)findViewById(R.id.foreignbox)).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.pronunciationbox)).setVisibility(View.INVISIBLE);
        ((EditText)findViewById(R.id.abox)).setText("");
        if(AudioQ)
        {
            ((Button)findViewById(R.id.audiobutton)).setVisibility(View.VISIBLE);
            ((EditText)findViewById(R.id.qbox)).setVisibility(View.INVISIBLE);
        }
        else
            {
                ((Button)findViewById(R.id.audiobutton)).setVisibility(View.INVISIBLE);
                ((EditText)findViewById(R.id.qbox)).setVisibility(View.VISIBLE);
            }
    }

    public void SwapQuestionFields(Boolean AudioQ)
    {
        if(CurrentlyAudio){Card.c1.MediaPlayer = null;}
        CurrentlyAudio = !CurrentlyAudio;
        String t = Card.c1.Question;
        Card.c1.Question = Card.c1.Answer;
        Card.c1.Answer = t;
        t = Card.c1.QuestionLanguage;
        Card.c1.QuestionLanguage = Card.c1.AnswerLanguage;
        Card.c1.AnswerLanguage = t;
        try {
            Card.c1.CleanAnswer = Arrays.asList(Card.c1.Answer.split("\\|")).stream().map(x -> Card.CleanAnswer(x, Card.c1.AnswerLanguage, Card.c1.Decktype)).toArray(String[]::new);
        }catch(Exception e)
        {
            int i = 0;
        }
        SetFields(AudioQ);
    }


    public void CheckAnswer(Boolean submitpressed)
    {
        try
        {
            if (Arrays.asList(Card.c1.CleanAnswer).contains(Card.CleanAnswer(((EditText)findViewById(R.id.abox)).getText().toString(), Card.c1.AnswerLanguage, Card.c1.Decktype)))
            {
                if (Card.c1.MediaPlayer == null)
                {
                    String[] results = global.MakeRequest("RR", wrong?"F":"P", "5", ((EditText)findViewById(R.id.abox)).getText().toString(), "english", Card.c1.Deckname, Card.c1.Foreign).split("↔");
                    String toast = Card.c1.Foreign + " "  +results[0] + ">" +results[1]+"("+(!wrong ? "+" + (Integer.parseInt(results[1]) - Integer.parseInt(results[0])) : "-" + (Integer.parseInt(results[0]) - Integer.parseInt(results[1])))+")";
                    if(results.length >2)
                    {
                        if (results[2].equals("cardunlocked")) {toast += "Card Unlocked!";}
                        if (results[2].equals("masterylost")) { toast += "Mastery Lost!"; }
                    }
                    Toast.makeText(this, toast,Toast.LENGTH_LONG).show();
                    if(!wrong){global.CorrectSound.start();}
                    Start(false);
                }
                else{SwapQuestionFields(false);}
            }
            else if(submitpressed){Learn();}
        }
        catch(Exception e)
        {
            int i = 0;
        }
    }

    public void Learn()
    {
        global.WrongSound.start();
        wrong = true;
        if(CurrentlyAudio){SwapQuestionFields(false);}
        ((TextView)findViewById(R.id.qlang)).setVisibility(View.INVISIBLE);

        ((EditText)findViewById(R.id.qbox)).setText(Card.c1.Familiar);
        ((EditText)findViewById(R.id.foreignbox)).setText(Card.c1.Foreign);
        ((EditText)findViewById(R.id.foreignbox)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.pronunciationbox)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.pronunciationbox)).setText(Card.c1.Pronunciation);
        Card.c1.CleanAnswer = new String[]{Card.CleanAnswer(Card.c1.Foreign,Card.c1.Deckname.split("\\^")[1],Card.c1.Decktype)};
        ((EditText)findViewById(R.id.abox)).setText("");

    }



}
