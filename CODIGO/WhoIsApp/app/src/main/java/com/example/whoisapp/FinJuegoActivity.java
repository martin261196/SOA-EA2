package com.example.whoisapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class FinJuegoActivity extends Activity {

    TextView tv_ganador;
    Button btn_jugar;
    Button btn_cerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_juego);

        tv_ganador = findViewById(R.id.textViewGanador);
        btn_cerrarSesion = findViewById(R.id.buttonCerrarSesion);
        btn_jugar = findViewById(R.id.buttonJugarDeNuevo);

        mostrarGanador();

        btn_jugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBienvenido = new Intent(FinJuegoActivity.this, BienvenidoActivity.class);
                startActivity(intentBienvenido);
            }
        });

        btn_cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentLogin = new Intent(FinJuegoActivity.this, MainActivity.class);
                MainActivity.hilo.apiEnum = CodigoApiEnum.CERRAR_SESION;
                SharedPreferences mySPrefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySPrefs.edit();
                editor.remove("token");
                editor.apply();
                startActivity(intentLogin);
            }
        });
    }

    public void mostrarGanador(){
        String ganador = getIntent().getStringExtra("Ganador");
        tv_ganador.setText(ganador);
    }

    @Override
    protected void onResume() {
        MainActivity.hilo.setContext(this);
        super.onResume();
    }
}
