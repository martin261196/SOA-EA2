package com.example.whoisapp.bd;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Personaje.class}, version = 2)
public abstract class PersonajeBD extends RoomDatabase {

    public abstract PersonajeDAO personajeDAO();

}
