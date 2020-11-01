package com.example.whoisapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class EsperaComienzoActivity extends Activity {

    TextView tv_espera;
    boolean cambioAPersonaje = false;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espera_comienzo);
        tv_espera = findViewById(R.id.textViewTimeoutEspera);
        timer = new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                tv_espera.setText(Long.toString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                cambioAPersonaje = true;
                Intent intentPersonaje = new Intent(EsperaComienzoActivity.this, PersonajeActivity.class);
                startActivity(intentPersonaje);

            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        timer.cancel();
        if(!cambioAPersonaje) {
            Intent intent = new Intent(EsperaComienzoActivity.this, BienvenidoActivity.class);
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
