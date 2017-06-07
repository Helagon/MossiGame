package com.example.sven.m335_mossigame_block05;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener,Runnable{

    int timeRemaining;
    int mosquitos;
    int mosquitosCatched;
    int mosquitosToCatch;
    int level;
    int score;
    private static final int GAMETIME = 60;
    private static final int MOSSI_LIFETIME = 2;
    Handler handler;
    long timeSlot = 1000;

    TextView txtLevel, txtScore, txtTimeRemaining,
            txtMossisCatched;

    Random random;
    FrameLayout barTime, barMosquitos;
    ViewGroup gameZone;
    Date tStart;

    private void startGame(){
        level = 0;
        score = 0;
        startLevel();
    }

    private void startLevel(){
        mosquitosCatched = 0;
        level = level+1;
        mosquitos = level * 10;
        mosquitosToCatch = level *10;
        tStart = new Date();

        refreshScreen();
        //showMosquito();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // init textView variables
        random = new Random();

        txtLevel = (TextView)findViewById(R.id.txtViewLevel);
        txtScore = (TextView) findViewById(R.id.txtViewScore);
        txtMossisCatched = (TextView) findViewById(R.id.textViewCatches);
        txtTimeRemaining = (TextView) findViewById(R.id.textViewTimeLeft);
        barTime = (FrameLayout)findViewById(R.id.bar_time);
        barMosquitos = (FrameLayout)findViewById(R.id.bar_catches);
        gameZone = (ViewGroup)findViewById(R.id.gameZone);



        handler = new Handler();
        handler.postDelayed(this, timeSlot);

        startGame();

    }

    /**
     * display current game information
     */
    private void refreshScreen(){

        int dTime = (int)(new Date().getTime() - tStart.getTime())/1000;
        timeRemaining = GAMETIME - dTime;
        txtLevel.setText(Integer.toString(level));
        txtScore.setText(Integer.toString(score));
        txtTimeRemaining.setText(Integer.toString(timeRemaining));
        txtMossisCatched.setText(Integer.toString(mosquitosCatched));

        // time bar handling
        ViewGroup.LayoutParams params = barTime.getLayoutParams();
        int fullWidth = gameZone.getMeasuredWidth();
        params.width = fullWidth / GAMETIME * (GAMETIME - dTime);
        // mosquito bar handling
        params = barMosquitos.getLayoutParams();
        params.width = fullWidth / mosquitos * mosquitosCatched;

    }

    private void showMosquito(){
        gameZone = (ViewGroup)findViewById(R.id.gameZone);
        int width = gameZone.getMeasuredWidth();
        int height = gameZone.getMeasuredHeight();
        Log.d("MOSSI_GAMEACTIVITY", "width:"+width+", height: "+height);
        int mossiSize = 150;

        int distTop = random.nextInt(height-mossiSize);
        int distLeft = random.nextInt(width-mossiSize);
        //neuen imageView für mossi anlegen
        ImageView newMossi = new ImageView(this);
        newMossi.setImageResource(R.drawable.mosquito);
        newMossi.setTag(R.id.bday, new Date());

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(mossiSize, mossiSize);
        params.topMargin = distTop;
        params.leftMargin = distLeft;
        gameZone.addView(newMossi, params);

        newMossi.setOnClickListener(this);

    }

    private void hideMossi(){
        int currentIndex = 0;
        while(currentIndex < gameZone.getChildCount()){
            ImageView currentMossi = (ImageView)gameZone.getChildAt(currentIndex);
            Date bday = (Date)(currentMossi.getTag(R.id.bday));
            if(new Date().getTime()-bday.getTime()>MOSSI_LIFETIME){
                gameZone.removeView(currentMossi);
                score -= 10;
            }
            else{
                currentIndex++;
            }
        }
    }

    private void decrementTime(){
        timeRemaining = (int) (GAMETIME - (new Date().getTime() - tStart.getTime())/1000);
        if(!gameFinished()){
            if(!levelFinished()){
                if(!loseScore()){
                    showMosquito();
                    refreshScreen();
                    handler.postDelayed(this,timeSlot);
                }
                else{
                    showGameOver();
                }

            }
            else{
                handler.postDelayed(this,timeSlot);
                startLevel();
            }
        }

        else {
            showGameOver();
        }

    }

    private boolean gameFinished(){
        if(timeRemaining <= 0 && mosquitosCatched < mosquitosToCatch){
            return true;
        }
        else{
            return false;
        }
    }
    private boolean levelFinished(){
        if(mosquitosCatched >= mosquitosToCatch){
            return true;
        }
        else{
            return false;
        }
    }
    private void showGameOver(){
        Intent i = new Intent(this, GameOverActivity.class);
        startActivity(i);
    }
    private boolean loseScore(){
        if(score <0){
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        ImageView mossi = (ImageView)v;
        score += 100;
        mosquitosCatched++;
        gameZone.removeView(v);
        refreshScreen();
    }

    public void run(){
        decrementTime();
        hideMossi();
    }
}
