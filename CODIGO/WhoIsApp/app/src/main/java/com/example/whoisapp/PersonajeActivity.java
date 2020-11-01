package com.example.whoisapp;


import androidx.room.Room;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.TextView;

import com.example.whoisapp.bd.Personaje;
import com.example.whoisapp.bd.PersonajeBD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class PersonajeActivity extends Activity {

    TextView tv_personaje;
    TextView tv_espera;
    TextView tv_acelerometro;
    SensorManager sensorManager;
    Sensor sAcelerometro;
    SensorEventListener sEventoListener;
    HandlerThread mSensorThread;
    Handler mSensorHandler;
    ArrayList<Personaje> personajes;
    int puntajeJugador = 0;
    boolean cambio = false;
    CountDownTimer timer;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personaje);

        sharedPreferences =  getSharedPreferences("Preferencias", Context.MODE_PRIVATE);

        tv_personaje = findViewById(R.id.textViewPersonaje);
        tv_espera = findViewById(R.id.textViewTimeout);
        tv_acelerometro = findViewById(R.id.textViewAcelerómetro);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sAcelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorThread = new HandlerThread("Sensor thread", Thread.NORM_PRIORITY);
        mSensorThread.start();
        mSensorHandler = new Handler(mSensorThread.getLooper());

        sEventoListener = new SensorEventListener() {
            String colorAnterior = "";
            @Override
            public void onSensorChanged(SensorEvent event) {

                double ejeX = event.values[0];
                double ejeY = event.values[1];
                double ejeZ = event.values[2];

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("X",(float)ejeX);
                editor.putFloat("Y",(float)ejeY);
                editor.putFloat("Z",(float)ejeZ);
                editor.commit();

                final double x = ejeX;
                final double y = ejeY;
                final double z = ejeZ;

                double norm_Of_g = Math.sqrt(ejeX * ejeX + ejeY * ejeY + ejeZ * ejeZ);
                ejeZ = ejeZ / norm_Of_g;
                String color = "";

                int inclination = (int) Math.round(Math.toDegrees(Math.acos(ejeZ)));

                Boolean cambiarDePersonaje = false;

                if(inclination<50){
                    color = "Verde";
                    cambiarDePersonaje = true;
                }else if (inclination > 100){
                    color = "Rojo";
                    cambiarDePersonaje = true;
                } else if (inclination > 60 && inclination < 90){
                    color = "Azul";
                } else {
                    color = colorAnterior;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_acelerometro.setText("Los valores del acelerometro son X : "+String.format("%.2f", x)+" Y : "+String.format("%.2f", y)+" Z : "+String.format("%.2f", z));
                    }
                });

                if(!color.equals(colorAnterior)) {
                    if(cambiarDePersonaje){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cambiarPersonaje();
                            }
                        });
                    }
                    if(color.equals("Verde")){
                        puntajeJugador++;
                    }
                    colorAnterior = color;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        final String tematica = sharedPreferences.getString("Tematica", "");
        Thread hilo = new Thread(){
                @Override
                    public void run() {
                        PersonajeBD db = Room.databaseBuilder(getApplicationContext(),
                                PersonajeBD.class, "personaje-bd").allowMainThreadQueries().fallbackToDestructiveMigration().build();

                        personajes = (ArrayList<Personaje>) db.personajeDAO().getPorTematica(tematica);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cambiarPersonaje();
                            }
                        });

            }};
        hilo.start();


        sensorManager.registerListener(sEventoListener, sAcelerometro, SensorManager.SENSOR_DELAY_GAME, mSensorHandler);

        crearTimer();
    }

    public void verificarProximoJugador(){
        SharedPreferences sharedPreferences = getSharedPreferences("Preferencias", MODE_PRIVATE);
        cambio = true;
        int nroJugador = sharedPreferences.getInt("SiguienteJugador", -1);
        int cantJugadores = sharedPreferences.getInt("cantJugadores", 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("PuntajeJugador"+nroJugador, puntajeJugador);
        editor.commit();
        if(nroJugador < cantJugadores){
            editor.putInt("SiguienteJugador", nroJugador+1);
            editor.commit();
            Intent intentNuevoJugador = new Intent(PersonajeActivity.this, PrepararseJugadorActivity.class);
            startActivity(intentNuevoJugador);
        } else {
            Intent intentGanador = new Intent(PersonajeActivity.this, FinJuegoActivity.class);
            intentGanador.putExtra("Ganador", obtenerGanador(sharedPreferences, cantJugadores));
            startActivity(intentGanador);
        }
    }

    public String obtenerGanador(SharedPreferences sharedPreferences, int cantJugadores) {

        Integer[] puntajes = new Integer[cantJugadores];
        int puntajeMaximo = 0;
        int posicionMaximo = 0;

        for(int i = 0 ; i < cantJugadores ; i++){

            puntajes[i] = sharedPreferences.getInt("PuntajeJugador"+(1+i), -1);
        }

        puntajeMaximo = Collections.max(Arrays.asList(puntajes));
        posicionMaximo = Arrays.asList(puntajes).indexOf(puntajeMaximo);

        return sharedPreferences.getString("Jugador "+(posicionMaximo+1), "");

    }

    public void cambiarPersonaje(){

        Personaje personaje =  personajes.get(new Random().nextInt(personajes.size()));
        String nombre = personaje.nombre;
        tv_personaje.setText(nombre);
        personajes.remove(personaje);

        if(personajes.size() == 0) {
            verificarProximoJugador();
        }
    }

    private void crearTimer(){
        timer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tv_espera.setText(Long.toString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                verificarProximoJugador();
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sEventoListener);
        if(!cambio) {
            Intent intent = new Intent(PersonajeActivity.this, BienvenidoActivity.class);
            startActivity(intent);
        }
        timer.cancel();
        finish();
        super.onPause();
    }

    @Override
    protected void onResume() {
        MainActivity.hilo.setContext(this);
        super.onResume();
    }

}
