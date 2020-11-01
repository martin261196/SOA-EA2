package com.example.whoisapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.TextView;
import android.widget.Toast;

public class PrepararseJugadorActivity extends Activity {

    TextView tv_jugador;
    SensorManager sensorManager;
    Sensor sProximidad;
    SensorEventListener sEventoListener;
    HandlerThread mSensorThread;
    Handler mSensorHandler;
    boolean cambioAEsperaComienzo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepararse_jugador);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sProximidad = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        mSensorThread = new HandlerThread("Sensor thread", Thread.NORM_PRIORITY);
        mSensorThread.start();
        mSensorHandler = new Handler(mSensorThread.getLooper());


        SharedPreferences sharedPreferences = getSharedPreferences("Preferencias", MODE_PRIVATE);

        int nroJugador = sharedPreferences.getInt("SiguienteJugador", -1);
        String name = sharedPreferences.getString("Jugador "+nroJugador, "");
        tv_jugador = findViewById(R.id.textViewJugador);
        tv_jugador.setText(name);

        sEventoListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Intent intentEspera = new Intent(PrepararseJugadorActivity.this, EsperaComienzoActivity.class);

                final float valorDeSensor = event.values[0];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "El valor del sensor de proximidad es "+valorDeSensor, Toast.LENGTH_SHORT).show();
                    }
                });

                if(event.values[0] < event.sensor.getMaximumRange()){
                    cambioAEsperaComienzo = true;
                    startActivity(intentEspera);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        };

        sensorManager.registerListener(sEventoListener, sProximidad, SensorManager.SENSOR_DELAY_FASTEST, mSensorHandler);

    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sEventoListener);
        if(!cambioAEsperaComienzo) {
            Intent intent = new Intent(PrepararseJugadorActivity.this, BienvenidoActivity.class);
            startActivity(intent);
        }
        finish();
        super.onPause();
    }

    @Override
    protected void onResume() {
        MainActivity.hilo.setContext(this);
        super.onResume();
    }
}
