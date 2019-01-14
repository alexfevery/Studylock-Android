package com.example.alexf.studylockmobile;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Arrays;
import java.util.stream.Stream;

public class Card
{
    public static Card c1 = null;
    public static Card c2 = null;


    public String Question;
    public String Answer;
    public String QuestionLanguage;
    public String AnswerLanguage;
    public String OtherForms;
    public String ExtraForeign;
    public String Pronunciation;
    public String Foreign;
    public String Familiar;
    public String Deckname;
    public String Decktype;
    public String[] CleanAnswer;

    public android.media.MediaPlayer MediaPlayer = null;

    public Card(String data) throws java.io.IOException
    {
        String[] split = data.split("↔", -1);
        Question = split[0];
        Answer = split[1];
        QuestionLanguage = split[2];
        AnswerLanguage = split[3];
        OtherForms = split[4];
        ExtraForeign =split[5];
        Pronunciation = split[6];
        Foreign = split[7];
        Familiar = split[8];
        Deckname = split[9];
        Decktype = split[10];
        CleanAnswer = Stream.concat(Arrays.stream(Answer.split("\\|")),Arrays.stream(OtherForms.split("\\|"))).filter(t -> !t.equals("")).map(x -> Card.CleanAnswer(x, AnswerLanguage, Decktype)).toArray(String[]::new);
        if(!split[11].equals(""))
        {
            MediaPlayer = new MediaPlayer();
            MediaPlayer.setDataSource(global.url+split[11]);
            MediaPlayer.prepare();
        }
    }

    public static void LoadCards(Boolean FirstCard) throws java.lang.InterruptedException
    {
        if(!FirstCard)
        {
            c1 = c2;
            c2 = null;
        }
        if(c1 == null){new Thread(new Runnable() {public void run() {try {Card.c1 = new Card(global.MakeRequest("NC", "", "", "", "english", "", ""));}catch(Exception e){}}}).start();}
        if(c2 == null){new Thread(new Runnable() {public void run() {try {Card.c2 = new Card(global.MakeRequest("NC", "", "", "", "english", "", ""));}catch(Exception e){}}}).start();}
    }

    public static String CleanAnswer(String input, String language, String decktype)
    {
        try {
            input = input.toLowerCase();
            input = input.replaceAll("\\p{P}", "");
            input = " " + input + " ";
            boolean spacebased = Arrays.asList(input).stream().allMatch(val -> GetScriptType(val.charAt(0)) == Script.Latin);
            if (spacebased) {
                input = input.replaceAll("\\s+", " ");
            } else {
                input = input.replaceAll("\\s+", "");
            }
            boolean universal = false;
            boolean read = false;

            for (String c1 : global.SRD) {
                if (c1.trim().equals("")) {
                    continue;
                }
                String[] line = c1.toLowerCase().split("=");
                if (!spacebased) {
                    line[0] = line[0].replaceAll("\\s+", "");
                    if (line.length == 2) {
                        line[1] = line[1].replaceAll("\\s+", "");
                    }
                }
                if (Arrays.asList(line).contains("#language") || Arrays.asList(line).contains("#//language")) {
                    if (Arrays.asList(line).contains(language) || Arrays.asList(line).contains("universal")) {
                        read = true;
                        if (Arrays.asList(line).contains("universal")) {
                            universal = true;
                        } else {
                            universal = false;
                        }
                    } else {
                        read = false;
                    }
                    continue;
                }
                if (read) {
                    if (line.length == 2 && universal) {
                        if (input.contains(line[0])) {
                            input = input.replaceAll(line[0], line[1]);
                        }
                    }
                    Boolean t1 = input.trim().split(" ").length > 1;
                    if (decktype.equals("phrases") || (decktype.equals("vocab") && input.trim().split(" ").length > 1)) {
                        if (line.length == 1) {
                            if (input.contains(line[0])) {
                                input = input.replaceAll(line[0], spacebased ? " " : "");
                            }
                        } else if (decktype.equals("phrases")) {
                            if (input.contains(line[0])) {
                                input = input.replaceAll(line[0], line[1]);
                            }
                        }
                    }
                }
            }
            input = input.replaceAll("\\s+", " ");
            input = input.trim();
            if (language.equals("korean") && decktype.equals("vocab")) {
                String[] specs = new String[]{"히", "한"};
                if (input.length() > 1 && Arrays.asList(specs).contains(input.substring(input.length() - 1).trim())) {
                    input = input.substring(0, input.length() - 1);
                }
                if (input.length() > 2 && input.substring(input.length() - 2).trim().equals("하다")) {
                    input = input.substring(0, input.length() - 2);
                }
                input = input.replaceAll("을", "").replaceAll("를", "").replaceAll("이", "").replaceAll("가", "");
            }
            if (language.equals("japanese") && decktype.equals("vocab") && input.length() > 1) {
                if (input.charAt(0) == 'お' || input.charAt(0) == '御') {
                    input = input.substring(1);
                }
            }
            if (language.equals("english") && decktype.equals("vocab") && input.length() > 1) {
                if (input.length() > 1 && Arrays.asList(new String[]{"s"}).contains(input.substring(input.length() - 1).trim())) {
                    input = input.substring(0, input.length() - 1);
                }
            }
            return input.trim();
        }
        catch(Exception e)
        {
            return "";
        }
    }

    public enum Script { Latin, Cyrillic, Arabic, Hangul, Kana, CJKCharacters, Hebrew, Zhuyin, Unknown };
    public static Script GetScriptType(char c1)
    {
        if (c1 >= 65 && c1 <= 90) { return Script.Latin; }
        if (c1 >= 97 && c1 <= 122) { return Script.Latin; }
        if (c1 >= 192 && c1 < 255) { return Script.Latin; }
        if (c1 >= 256 && c1 < 383) { return Script.Latin; }
        if (c1 >= 384 && c1 < 591) { return Script.Latin; }
        if (c1 >= 592 && c1 < 687) { return Script.Latin; }
        if (c1 >= 688 && c1 < 767) { return Script.Latin; }
        if (c1 >= 1024 && c1 < 1279) { return Script.Cyrillic; }
        if (c1 >= 1280 && c1 < 1327) { return Script.Cyrillic; }
        if (c1 >= 1424 && c1 < 1535) { return Script.Hebrew; }
        if (c1 >= 1536 && c1 < 1791) { return Script.Arabic; }
        if (c1 >= 1872 && c1 < 1919) { return Script.Arabic; }
        if (c1 >= 2208 && c1 < 2303) { return Script.Arabic; }
        if (c1 >= 4352 && c1 <= 4607) { return Script.Hangul; }
        if (c1 >= 7424 && c1 < 7551) { return Script.Latin; }
        if (c1 == 7467) { return Script.Cyrillic; }
        if (c1 == 7544) { return Script.Cyrillic; }
        if (c1 >= 7552 && c1 < 7615) { return Script.Latin; }
        if (c1 >= 7680 && c1 < 7935) { return Script.Latin; }
        if (c1 >= 8448 && c1 < 8527) { return Script.Latin; }
        if (c1 >= 8528 && c1 < 8591) { return Script.Latin; }
        if (c1 >= 11360 && c1 < 11391) { return Script.Latin; }
        if (c1 >= 11744 && c1 < 11775) { return Script.Cyrillic; }
        if (c1 >= 12352 && c1 <= 12543) { return Script.Kana; }
        if (c1 >= 12549 && c1 <= 12589) { return Script.Zhuyin; }
        if (c1 >= 12592 && c1 <= 12687) { return Script.Hangul; }
        if (c1 >= 13312 && c1 <= 19903) { return Script.CJKCharacters; }
        if (c1 >= 19968 && c1 <= 40959) { return Script.CJKCharacters; }
        if (c1 >= 42560 && c1 < 42655) { return Script.Cyrillic; }
        if (c1 >= 42784 && c1 < 43007) { return Script.Latin; }
        if (c1 >= 43360 && c1 <= 43391) { return Script.Hangul; }
        if (c1 >= 43824 && c1 < 43887) { return Script.Latin; }
        if (c1 >= 44032 && c1 <= 55215) { return Script.Hangul; }
        if (c1 >= 55216 && c1 <= 55295) { return Script.Hangul; }
        if (c1 >= 63744 && c1 <= 64255) { return Script.CJKCharacters; }
        if (c1 >= 64256 && c1 < 64335) { return Script.Latin; }
        if (c1 >= 64285 && c1 < 64335) { return Script.Hebrew; }
        if (c1 >= 64336 && c1 < 65023) { return Script.Arabic; }
        if (c1 >= 65070 && c1 < 65071) { return Script.Cyrillic; }
        if (c1 >= 65136 && c1 < 65279) { return Script.Arabic; }
        if (c1 >= 65313 && c1 < 65339) { return Script.Latin; }
        if (c1 >= 65345 && c1 < 65371) { return Script.Latin; }
        if (c1 >= 69216 && c1 < 69247) { return Script.Arabic; }
        if (c1 >= 126464 && c1 < 126719) { return Script.Arabic; }
        if (c1 >= 131072 && c1 <= 173791) { return Script.CJKCharacters; }
        if (c1 >= 173824 && c1 <= 177983) { return Script.CJKCharacters; }
        if (c1 >= 177984 && c1 <= 178207) { return Script.CJKCharacters; }
        if (c1 >= 178208 && c1 <= 183983) { return Script.CJKCharacters; }
        if (c1 >= 194560 && c1 <= 195103) { return Script.CJKCharacters; }
        return Script.Unknown;
    }

}