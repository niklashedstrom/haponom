package com.example.haponom;

import android.content.Context;
import android.content.DialogInterface;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    ImageView bpmButton;
    ImageView incButton;
    ImageView decButton;
    ImageView vibButton;
    ImageView lightButton;
    ImageView soundButton;
    ImageView rotateButton;
    ImageView arrows;
    ImageView overlay;
    static TextView BPMText;
    static TextView countdown;

    static int bpm;
    SensorManager mySensorManager;
    Sensor myProximitySensor;
    Sensor stepDetector;
    MetronomeMonitor metronomeMonitor;
    LegMechanicMonitor legMechanicMonitor;
    StepCounterHandler stepCounterHandler;
    boolean flag = false;
    CameraManager cameraManager;
    boolean compassChecked = false;
    boolean isClicked = false;

    enum Choice {
        VIBRATION,
        LIGHT,
        SOUND
    }

    Choice myChoice;

    //compass parts begin ---------------------------------------------
    int deg;
    int pastDeg = 0;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    //compass parts end ---------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bpmButton = findViewById(R.id.playpause);
        incButton = findViewById(R.id.inc);
        decButton = findViewById(R.id.dec);
        bpm = 120;
        BPMText = findViewById(R.id.bpmText);
        myChoice = Choice.VIBRATION;
        vibButton = findViewById(R.id.vibBtn);
        lightButton = findViewById(R.id.lightBtn);
        soundButton = findViewById(R.id.soundBtn);
        rotateButton = findViewById(R.id.rotateBtn);
        arrows = findViewById(R.id.arrows);
        overlay = findViewById(R.id.overlay);

        //compass parts begin ----------------------------------------------
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //compass parts end ---------------------------------------------

        // proximity part begin --------------------------------------------

        mySensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        myProximitySensor = mySensorManager.getDefaultSensor(
                Sensor.TYPE_PROXIMITY);
        if (myProximitySensor == null) {

        } else {
            mySensorManager.registerListener(this,
                    myProximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        // proximity part end --------------------------------------------

        stepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_NORMAL);

        //Vibrator
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //Sound
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        SoundPool sound = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //Flashlight
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        //Metronome monitor
        metronomeMonitor = new MetronomeMonitor();
        metronomeMonitor.setChoice(myChoice);
        metronomeMonitor.setBPM(bpm);

        legMechanicMonitor = new LegMechanicMonitor();

        stepCounterHandler = new StepCounterHandler(legMechanicMonitor, metronomeMonitor, this);
        stepCounterHandler.start();

        MetronomeThread metronomeThread = new MetronomeThread(metronomeMonitor, vib, sound, cameraManager, this);
        metronomeThread.start();
    }


    public void setToVibration(View v){
        myChoice = Choice.VIBRATION;
        metronomeMonitor.setChoice(myChoice);

        vibButton.setImageResource(R.drawable.navbar_vib2_activated);
        lightButton.setImageResource(R.drawable.navbar_torch2);
        soundButton.setImageResource(R.drawable.navbar_sound);
    }

    public void setToLight(View view){
        myChoice = Choice.LIGHT;
        metronomeMonitor.setChoice(myChoice);

        vibButton.setImageResource(R.drawable.navbar_vib2);
        lightButton.setImageResource(R.drawable.navbar_torch2_activated);
        soundButton.setImageResource(R.drawable.navbar_sound);

    }

    public void setToSound(View view){
        myChoice = Choice.SOUND;
        metronomeMonitor.setChoice(myChoice);

        vibButton.setImageResource(R.drawable.navbar_vib2);
        lightButton.setImageResource(R.drawable.navbar_torch2);
        soundButton.setImageResource(R.drawable.navbar_sound_activated);

    }

    public void startMetronome(View view){
        if(flag){
            flag = false;
        } else {
            flag = true;
        }

        if(flag) {
            bpmButton.setImageResource(R.drawable.pause);
            metronomeMonitor.setChoice(myChoice);
            metronomeMonitor.setBool();
            metronomeMonitor.start();
        } else {
            bpmButton.setImageResource(R.drawable.play);
            metronomeMonitor.stop();
        }
    }

    public void increase(View view){
        incButton.setImageResource(R.drawable.plus_orange);
        bpm++;
        BPMText.setText(Integer.toString(bpm));
        metronomeMonitor.setBPM(bpm);
        incButton.setImageResource(R.drawable.plus_blue);

    }

    public void decrease(View view){
        decButton.setImageResource(R.drawable.minus_orange);
        bpm--;
        BPMText.setText(Integer.toString(bpm));
        metronomeMonitor.setBPM(bpm);
        decButton.setImageResource(R.drawable.minus_blue);
    }


    //compass parts begin ----------------------------------------------
    public void onCompassClicked(View view){

        if (!compassChecked){
            incButton.setVisibility(View.GONE);
            decButton.setVisibility(View.GONE);
            arrows.setVisibility(View.VISIBLE);
            compassChecked = true;
            rotateButton.setImageResource(R.drawable.rotate_activated);
            start();

        }else{
            arrows.setVisibility(View.GONE);
            incButton.setVisibility(View.VISIBLE);
            decButton.setVisibility(View.VISIBLE);
            compassChecked = false;
            stop();
            pastDeg = 0;
            rotateButton.setImageResource(R.drawable.rotate_unacti);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            deg = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            deg = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        if (pastDeg != 0) {
            int diff = deg - pastDeg;
            if(bpm + diff < 0){
                bpm = 0;
            } else {
                bpm += diff;
            }
            BPMText.setText(Integer.toString(bpm));
            metronomeMonitor.setBPM(bpm);

        }
        pastDeg = deg;

        // proximity code ------------------------------------------------------------
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] == 0) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                metronomeMonitor.setChoice(myChoice);
            }
        }

        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            if(event.values[0] == 1.0) legMechanicMonitor.incStep();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    public void start() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                noSensorsAlert();
            }
            else {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else{
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void noSensorsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void stop() {
        if(haveSensor && haveSensor2){
            mSensorManager.unregisterListener(this,mAccelerometer);
            mSensorManager.unregisterListener(this,mMagnetometer);
        }
        else{
            if(haveSensor)
                mSensorManager.unregisterListener(this,mRotationV);
        }
    }

//compass parts end ---------------------------------------------

    public void pocketModeButton(View view){
        if(!isClicked){
            isClicked = true;
            overlay.setVisibility(View.VISIBLE);
            legMechanicMonitor.press();
            legMechanicMonitor.startLeg();
        } else {
            isClicked = false;
            overlay.setVisibility(View.GONE);
        }
    }
}