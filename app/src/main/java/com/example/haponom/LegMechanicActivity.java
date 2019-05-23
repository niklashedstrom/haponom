package com.example.haponom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

public class LegMechanicActivity extends AppCompatActivity implements SensorEventListener {

    public SensorManager sensorManager;
    public Sensor stepDetector;
    private LegMechanicMonitor monitor;
    private StepCounterHandler sch;
    private MetronomeMonitor metronomeMonitor;
    private MetronomeThread metronomeThread;
    private Vibrator vib;
    private SoundPool sound;
    private CountdownThread countdownThread;
    public static TextView countdown;
    public static TextView bpmOnScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leg_mechanic_activity);

        countdown = findViewById(R.id.countdown);
        bpmOnScreen = findViewById(R.id.bpmOnScreen);

        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        sound = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //Flashlight
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_NORMAL);

        CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        monitor = new LegMechanicMonitor();
        metronomeMonitor = new MetronomeMonitor();


        sch = new StepCounterHandler(monitor, metronomeMonitor, this);
        metronomeThread = new MetronomeThread(metronomeMonitor, vib, sound, cameraManager, this);
        countdownThread = new CountdownThread(monitor, this);

        sch.start();
        metronomeThread.start();
        countdownThread.start();
    }

    public void backToMain(View view){
        metronomeThread.interrupt();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void startLegButton(View view){
        monitor.press();
        monitor.startLeg();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.values[0] == 1.0){
            monitor.incStep();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
