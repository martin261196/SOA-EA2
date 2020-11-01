package com.example.whoisapp.bd;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = Personaje.TABLE_NAME)
public class Personaje {
    public static final String TABLE_NAME = "personaje";

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "tematica")
    public String tematica;

    public Personaje(String nombre, String tematica) {
        this.nombre = nombre;
        this.tematica = tematica;
    }
}