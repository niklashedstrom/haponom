package com.example.haponom;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Button bpmButton;
    Button incButton;
    Button decButton;
    ImageButton vibButton;
    ImageButton lightButton;
    ImageButton soundButton;
    int bpm;
    TextView ProximitySensor;
    SensorManager mySensorManager;
    Sensor myProximitySensor;
    MetronomeMonitor metronomeMonitor;
    boolean flag = false;
    CameraManager cameraManager;

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
        bpmButton = (Button) findViewById(R.id.BPM);
        incButton = (Button) findViewById(R.id.inc);
        decButton = (Button) findViewById(R.id.dec);
        bpm = 100;
        bpmButton.setText(Integer.toString(bpm));
        myChoice = Choice.VIBRATION;
        vibButton = (ImageButton)findViewById(R.id.vibBtn);
        lightButton = (ImageButton)findViewById(R.id.lightBtn);
        soundButton = (ImageButton)findViewById(R.id.soundBtn);


        //compass parts begin ----------------------------------------------
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //compass parts end ---------------------------------------------

        // proximity part begin --------------------------------------------
        ProximitySensor = (TextView) findViewById(R.id.proximitySensor);

        mySensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        myProximitySensor = mySensorManager.getDefaultSensor(
                Sensor.TYPE_PROXIMITY);
        if (myProximitySensor == null) {
            ProximitySensor.setText("No Proximity Sensor!");
        } else {
            mySensorManager.registerListener(this,
                    myProximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        // proximity part end --------------------------------------------

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

        MetronomeThread metronomeThread = new MetronomeThread(metronomeMonitor, vib, sound, cameraManager, this);
        metronomeThread.start();
    }


    public void setToVibration(View view){
        myChoice = Choice.VIBRATION;
        //vibButton.setImageResource(R.drawable.light);
    }

    public void setToLight(View view){ myChoice = Choice.LIGHT; }

    public void setToSound(View view){ myChoice = Choice.SOUND; }

    public void startMetronome(View view){
        if(flag){
            flag = false;
        } else {
            flag = true;
        }

        if(flag) {
            metronomeMonitor.setChoice(myChoice);
            metronomeMonitor.setBool();
            metronomeMonitor.start();
        } else {
            metronomeMonitor.stop();
        }
    }

    public void increase(View view){
        bpm++;
        bpmButton.setText(Integer.toString(bpm));
        metronomeMonitor.setBPM(bpm);

    }

    public void decrease(View view){
        bpm--;
        bpmButton.setText(Integer.toString(bpm));
        metronomeMonitor.setBPM(bpm);
    }


    public void onLegClick(View view) {
        Intent intent = new Intent(this, LegMechanicActivity.class);
        startActivity(intent);
    }

    //compass parts begin ----------------------------------------------
    public void onToggleButtonClicked(View v) {
        //Check, is the toggle is on?
        boolean on = ((ToggleButton) v).isChecked();

        if (on) {
            start();
        }else{
            stop();
            pastDeg = 0;
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

        System.out.println(deg);
        System.out.println(pastDeg);
        if (pastDeg != 0) {
            int diff = deg - pastDeg;
            bpm += diff;
            bpmButton.setText(Integer.toString(bpm));
            metronomeMonitor.setBPM(bpm);

        }
        pastDeg = deg;

        // proximity code ------------------------------------------------------------
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] == 0) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    //cameraManager.setTorchMode(cameraId, true);
                } catch (CameraAccessException e) {
                }

            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    //cameraManager.setTorchMode(cameraId, false);
                } catch (CameraAccessException e) {
                }

            }
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

}