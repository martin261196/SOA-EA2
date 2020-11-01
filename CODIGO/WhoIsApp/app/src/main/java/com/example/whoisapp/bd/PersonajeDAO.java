package com.example.whoisapp.bd;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PersonajeDAO {


    @Query("SELECT * FROM personaje WHERE tematica LIKE :tematica")
    List<Personaje> getPorTematica(String tematica);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Personaje> personajes);


}
