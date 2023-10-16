package com.example.atarigame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayer2;
    Button play;
    GameSurface gameSurface;
    int score;
    int time = 0;
    int left;
    int randomLeft;
    int top;
    int i = 1;
    int hit;
    boolean crashed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = findViewById(R.id.id_play);
        mediaPlayer = MediaPlayer.create(this, R.raw.race);
        mediaPlayer2 = MediaPlayer.create(this, R.raw.crash);
        gameSurface = new GameSurface(this);
        setContentView(gameSurface);
        mediaPlayer.start();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);


    }
    public class GameSurface extends SurfaceView implements Runnable {

        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Bitmap trailer;
        Bitmap car;
        Bitmap broken;

        Paint paintProperty;
        Paint paintProperty2;
        Paint paintProperty3;
        Paint paintProperty4;
        Paint paintProperty5;
        Paint paintProperty6;
        Rect rectCar;
        Rect rectTrailer;

        int screenWidth;
        int screenHeight;

        public GameSurface(Context context) {
            super(context);
            holder=getHolder();
            trailer = BitmapFactory.decodeResource(getResources(),R.drawable.newtrailer);
            car = BitmapFactory.decodeResource(getResources(),R.drawable.newcar);
            broken = BitmapFactory.decodeResource(getResources(), R.drawable.newbroken);
            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth=sizeOfScreen.x;
            screenHeight=sizeOfScreen.y;

            paintProperty= new Paint();
            paintProperty2 = new Paint();
            paintProperty3 = new Paint();
            paintProperty4 = new Paint();
            paintProperty5 = new Paint();
            paintProperty6 = new Paint();


        }

        @Override
        public void run() {
            time = (int)(System.currentTimeMillis());
            while (running == true){
                if (holder.getSurface().isValid() == false)
                    continue;
                int runTime = (int) (System.currentTimeMillis() - time);
                runTime/=1000;
                Log.d("runtime", String.valueOf(runTime));
                Canvas canvas= holder.lockCanvas();
                canvas.drawColor(Color.GRAY);
                if(runTime <= 60)
                {

                    paintProperty.setColor(Color.YELLOW);
                    paintProperty.setTextSize(50);
                    paintProperty.setStyle(Paint.Style.FILL);
                    paintProperty3.setColor(Color.BLACK);
                    paintProperty2.setColor(Color.YELLOW);
                    paintProperty2.setTextSize(50);
                    paintProperty4.setTextSize(50);
                    paintProperty4.setColor(Color.GRAY);
                    paintProperty6.setColor(Color.BLACK);
                    paintProperty6.setTextSize(150);

                    if(i==1)
                    {
                        randomLeft = (int)(Math.random()*10)+1;
                        i = 2;
                    }
                    rectTrailer = new Rect(randomLeft*100,top+24,randomLeft*100+250,top+410);
                    rectCar = new Rect(left+30,1720,left+240,1700+480);
                    canvas.drawRect(rectCar,paintProperty4);
                    canvas.drawRect(rectTrailer,paintProperty4);
                    canvas.drawBitmap(car,left,1700,null);
                    canvas.drawBitmap(trailer,randomLeft*100,top+=24,null);
                    canvas.drawRect(0,2200,1600,3000,paintProperty3);
                    canvas.drawText("Score: " + score, 650, 2300, paintProperty);
                    canvas.drawText("Time: " + (60-runTime), 650, 2400, paintProperty2);
                    canvas.drawText("Trucks Hit: " + hit, 650, 2500, paintProperty2);
                    if(rectTrailer.intersect(rectCar)&&!crashed)
                    {
                        crashed = true;
                        mediaPlayer2.start();

                    }
                    if(crashed)
                    {
                        canvas.drawBitmap(broken, rectCar.left, rectCar.top, null);
                        Log.d("left coordinate", String.valueOf(rectCar.left));
                        Log.d("right coordinate", String.valueOf(rectCar.top));
                    }
                    if(top>=2400)
                    {
                        top = -1000;
                        i = 1;
                        canvas.drawBitmap(trailer,randomLeft*100,top+=5,null);
                        if(crashed)
                            hit++;
                        else
                            score++;
                        crashed = false;
                        mediaPlayer2.stop();
                        try {
                            mediaPlayer2.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }

                }
                else
                {
                    mediaPlayer.stop();
                    canvas.drawText("Game Over", 100, 200,paintProperty6);
                    canvas.drawText("Score: " + score, 100, 500,paintProperty6);
                    canvas.drawText("Trucks Hit: " + hit, 100, 800,paintProperty6);
                }
                holder.unlockCanvasAndPost(canvas);
            }
        }

        public void resume(){
            running=true;
            gameThread=new Thread(this);
            gameThread.start();
        }

        public void pause() {
            running = false;
            while (true) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                }
            }
        }


    }//GameSurface

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.values[0] > 0.5)
        {
            left-=15;
            if(sensorEvent.values[0] > 2)
                left-=30;
            if(left < 0)
                left = 0;
        }
        if(sensorEvent.values[0] < -0.5)
        {
            left+=15;
            if(sensorEvent.values[0] < -2)
                left+=30;


            if(left > 1230)
                left = 1230;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    protected void onPause(){
        super.onPause();
        gameSurface.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameSurface.resume();
    }
}