package com.example.whoisapp;

import androidx.room.Room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.whoisapp.bd.Personaje;
import com.example.whoisapp.bd.PersonajeBD;

import java.util.ArrayList;

public class TematicasActivity extends Activity {

    Button btn_deportistas;
    Button btn_famosos;
    Button btn_musica;
    Button btn_peliculas;

    boolean cambioAPrepararseJugador = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tematicas);

        btn_deportistas = findViewById(R.id.buttonDeportistas);
        btn_famosos = findViewById(R.id.buttonFamosos);
        btn_musica = findViewById(R.id.buttonMusica);
        btn_peliculas = findViewById(R.id.buttonPeliculas);

        new Thread() {
            public void run() {
                PersonajeBD db = Room.databaseBuilder(getApplicationContext(),
                        PersonajeBD.class, "personaje-bd").allowMainThreadQueries().fallbackToDestructiveMigration().build();
                ArrayList<Personaje> personajes = new ArrayList();

                //Deportistas
                personajes.add(new Personaje("Lionel Messi", "Deportista"));
                personajes.add(new Personaje("Juan Martin del Potro", "Deportista"));
                personajes.add(new Personaje("Sergio Aguero", "Deportista"));
                personajes.add(new Personaje("Cristiano Ronaldo", "Deportista"));
                personajes.add(new Personaje("Juan Manuel Fangio", "Deportista"));
                personajes.add(new Personaje("Guillermo Vilas", "Deportista"));
                personajes.add(new Personaje("Ricardo Bochini", "Deportista"));
                personajes.add(new Personaje("Tigresa Acuña", "Deportista"));
                personajes.add(new Personaje("Paula Pareto", "Deportista"));
                personajes.add(new Personaje("Gabriela Sabatini", "Deportista"));

                //Famosos
                personajes.add(new Personaje("Susana Gimenez", "Famosos"));
                personajes.add(new Personaje("Mirtha Legrand", "Famosos"));
                personajes.add(new Personaje("Marcelo Tinelli", "Famosos"));
                personajes.add(new Personaje("Guido Kaczka", "Famosos"));
                personajes.add(new Personaje("Ivan de Pineda", "Famosos"));
                personajes.add(new Personaje("Jose Maria Listorti", "Famosos"));
                personajes.add(new Personaje("Marley", "Famosos"));
                personajes.add(new Personaje("Rocio Marengo", "Famosos"));
                personajes.add(new Personaje("Pampita", "Famosos"));
                personajes.add(new Personaje("Moria Casan", "Famosos"));

                //Musica
                personajes.add(new Personaje("Valeria Lynch", "Musica"));
                personajes.add(new Personaje("Damas Gratis", "Musica"));
                personajes.add(new Personaje("Ciro y los persas", "Musica"));
                personajes.add(new Personaje("Indio Solari", "Musica"));
                personajes.add(new Personaje("La Sole", "Musica"));
                personajes.add(new Personaje("Lali Esposito", "Musica"));
                personajes.add(new Personaje("The Beatles", "Musica"));
                personajes.add(new Personaje("Queen", "Musica"));
                personajes.add(new Personaje("Kiss", "Musica"));
                personajes.add(new Personaje("Babasonicos", "Musica"));

                //Peliculas
                personajes.add(new Personaje("Harry Potter", "Peliculas"));
                personajes.add(new Personaje("Sherlock Holmes", "Peliculas"));
                personajes.add(new Personaje("Walter White", "Peliculas"));
                personajes.add(new Personaje("Frodo", "Peliculas"));
                personajes.add(new Personaje("Peter Parker", "Peliculas"));
                personajes.add(new Personaje("Superman", "Peliculas"));
                personajes.add(new Personaje("Jackie Chan", "Peliculas"));
                personajes.add(new Personaje("Toretto", "Peliculas"));
                personajes.add(new Personaje("Rocky Balboa", "Peliculas"));
                personajes.add(new Personaje("Woody", "Peliculas"));

                db.personajeDAO().insertAll(personajes);
            }}.start();

        btn_deportistas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarPrepararseJugadorActivity();
                guardarTematica("Deportista");
            }
        });

        btn_famosos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarPrepararseJugadorActivity();
                guardarTematica("Famosos");
            }
        });

        btn_musica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarPrepararseJugadorActivity();
                guardarTematica("Musica");
            }
        });

        btn_peliculas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarPrepararseJugadorActivity();
                guardarTematica("Peliculas");
            }
        });
    }

    public void iniciarPrepararseJugadorActivity(){
        cambioAPrepararseJugador = true;
        Intent intentPrepararse = new Intent(TematicasActivity.this, PrepararseJugadorActivity.class);
        SharedPreferences sharedPreferencesTematica =  getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesTematica.edit();
        editor.putInt("SiguienteJugador", 1);
        editor.commit();
        startActivity(intentPrepararse);
    }

    private void guardarTematica(String tematica) {
        SharedPreferences sharedPreferencesTematica =  getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesTematica.edit();
        editor.putString("Tematica", tematica);
        editor.commit();
    }

    @Override
    protected void onPause() {
        if(!cambioAPrepararseJugador) {
            Intent intent = new Intent(TematicasActivity.this, BienvenidoActivity.class);
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
