package com.example.whoisapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class BienvenidoActivity extends Activity {

    List<EditText> listaJugadores;
    EditText et_jugador1;
    EditText et_jugador2;
    EditText et_jugador3;
    EditText et_jugador4;
    EditText et_jugador5;
    Button btn_jugar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenido);
        listaJugadores = new ArrayList<>();

        btn_jugar = findViewById(R.id.btn_jugar);

        et_jugador1 = findViewById(R.id.editTextJugador1);
        et_jugador2 = findViewById(R.id.editTextJugador2);
        et_jugador3 = findViewById(R.id.editTextJugador3);
        et_jugador4 = findViewById(R.id.editTextJugador4);
        et_jugador5 = findViewById(R.id.editTextJugador5);

        listaJugadores.add(et_jugador1);
        listaJugadores.add(et_jugador2);
        listaJugadores.add(et_jugador3);
        listaJugadores.add(et_jugador4);
        listaJugadores.add(et_jugador5);

        SharedPreferences sharedPreferences =  getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        float x = sharedPreferences.getFloat("X", -1);
        float y = sharedPreferences.getFloat("Y", -1);
        float z = sharedPreferences.getFloat("Z", -1);

        if(x != -1 && y != -1 && z != -1){
            Toast.makeText(this, "Los ultimos valores del acelerometro fueron X : "+String.format("%.2f", x)+" Y : "+String.format("%.2f", y)+" Z : "+String.format("%.2f", z),Toast.LENGTH_LONG).show();
        }

        btn_jugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTematica = new Intent(BienvenidoActivity.this, TematicasActivity.class);

                List<String> nombresJugadores= new ArrayList<>();

                for(int i = 0; i < 5; i++){
                    String text = listaJugadores.get(i).getText().toString().trim();
                    if(!text.equals("")){
                        nombresJugadores.add(text);
                    }
                }
                guardarJugadores(nombresJugadores);
                startActivity(intentTematica);
            }
        });

    }

    private void guardarJugadores(List jugadores) {

        SharedPreferences sharedPreferencesJugadores =  getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesJugadores.edit();
        editor.putInt("cantJugadores", jugadores.size());
        for(int i = 0; i < jugadores.size() ; i++){
            editor.putString("Jugador "+(i+1), String.valueOf(jugadores.get(i)));
        }
        editor.commit();
    }

    @Override
    protected void onResume() {
        MainActivity.hilo.setContext(this);
        for(int i = 0; i < 5; i++) {
            listaJugadores.get(i).setText("");
        }
        super.onResume();
    }
}
