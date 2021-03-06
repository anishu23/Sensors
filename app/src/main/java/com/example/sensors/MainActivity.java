package com.example.sensors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    PostToDB postToDB;
    //public int c = 0;

public ImageView imageView;

    public int port = 9001;
    public String ip = "192.168.1.36";

    private float lastX, lastY, lastZ;

    public boolean connected = false;

    private SensorManager sensorManager;
    private SensorManager getSensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    public boolean clicked = false;

    public float deltaX = 0;
    public float deltaY = 0;
    public float deltaZ = 0;

    Thread newThread = new Thread(new ClientThre());


    public float delGX=0;
    public float delGY=0;
    public float delGZ=0;

    //public Button btn;

    private float vibrateThreshold = 0;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ;

    private TextView gyroX,gyroY,gyroZ;

    public Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseViews();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fail! we do not have a sensor
        }
        // initialise vibration
            getSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(getSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!=null)
        {
            gyroscope = getSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            getSensorManager.registerListener(this,gyroscope,SensorManager.SENSOR_DELAY_NORMAL);
        }
        //btn = (Button) findViewById(R.id.btn);

//        if(!clicked)
//        {
            //btn.setText("1");


//            clicked = true;
//        }
//        else if(newThread!=null)
//        {
            //btn.setText("0");
            //newThread.interrupt();
//            clicked = false;
//        }
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//
//            }
//        });
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

    }

    private Button.OnClickListener connectListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!connected) {
                if (!ip.equals("")) {
                    //connectPhones.setText("Stop Streaming");
                    Thread cThread = new Thread(new ClientThre());
                    cThread.start();
                }
            }
            else{
                //connectPhones.setText("Start Streaming");
                connected=false;
               // acc_disp=false;
            }
        }
    };

    public void initialiseViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);

        gyroX = (TextView) findViewById(R.id.GyroX);
        gyroY = (TextView) findViewById(R.id.GyroY);
        gyroZ = (TextView) findViewById(R.id.GyroZ);
    }

    //register the sensor onResume
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        getSensorManager.registerListener(this,gyroscope,SensorManager.SENSOR_DELAY_NORMAL);
    }

    // unregister in onPause

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        getSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //clean current values
        //displayCleanValues();
        // display the current values
        displayCurrentValues();
        // display the max values
        //displayMaxValues();
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            // get the change in x,y,z values of the accelerometer
            deltaX = Math.abs(lastX - sensorEvent.values[0]);
            deltaY = Math.abs(lastY - sensorEvent.values[1]);
            deltaZ = Math.abs(lastZ - sensorEvent.values[2]);
//            Thread newThread = new Thread(new ClientThread(deltaX,deltaY,deltaZ));
//            newThread.start();
        }
        else if(sensorEvent.sensor.getType()==Sensor.TYPE_GYROSCOPE)
        {
           delGX = Math.abs(sensorEvent.values[0]);
           delGY = Math.abs(sensorEvent.values[1]);
           delGZ = Math.abs(sensorEvent.values[2]);
        }
        // if the change is below 2, it is just plain noise

//        if (deltaX < 2)
//            deltaX = 0;
//        if (deltaY < 2)
//            deltaY = 0;
        //if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold))
            //v.vibrate(600);
    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");



        gyroX.setText("0.0");
        gyroY.setText("0.0");
        gyroZ.setText("0.0");
    }

    public void displayCurrentValues() {
        //c++;
        String curx = Float.toString(deltaX);
        String cury = Float.toString(deltaY);
        String curz = Float.toString(deltaZ);
        currentX.setText(curx);
        currentY.setText(cury);
        currentZ.setText(curz);

        String curgx = Float.toString(delGX);
        String curgy = Float.toString(delGY);
        String curgz = Float.toString(delGZ);

        gyroX.setText(curgx);
        gyroY.setText(curgy);
        gyroZ.setText(curgz);

        //String time = new SimpleDateFormat("DD/MM/YYYY.HH.mm.ss").format(new Timestamp(System.currentTimeMillis()));
        //Log.v("time", time);
        //postToDB = new PostToDB(curx, cury, curz, time);
        //if(c%678==0)
        //postToDB.post();
    }

    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }

    }

    public class ClientThre extends Thread
    {
        PrintWriter out; // used to send sensor data
        Socket socket;
        BufferedReader in; // used to get response
        String msg; // this variable stores the response as a string.
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(ip);
                Log.v("boo","hii");
                socket = new Socket(serverAddr, port);
                Log.v("boo","kjkh");
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                Log.v("boo","gfds");
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                msg = in.toString();
                String imgReq = msg.substring(msg.indexOf(",")+1);
                InputStream stream = new ByteArrayInputStream(Base64.decode(imgReq.getBytes(),Base64.DEFAULT));
                final Bitmap bmp = BitmapFactory.decodeStream(stream);
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView = (ImageView)findViewById(R.id.img);
                            imageView.setImageBitmap(bmp);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true) {
                    out.printf("%10.2f\t %10.2f\t %10.2f\n", deltaX, deltaY, deltaZ);
                    Log.v("boo", "ijo");
                    out.flush();
                    Thread.sleep(2);
                }
            } catch (Exception e) {
                Log.v("boo","uidfn");
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (Exception e) {
                    Log.v("boo","fiajcak");
                    e.printStackTrace();
                }
            }
        }
    };

    }




