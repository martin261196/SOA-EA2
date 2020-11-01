package com.example.whoisapp.peticiones;

import com.example.whoisapp.respuestas.RespuestaEvento;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface EventoInterface {

    @Headers({
            "Content-Type: application/json"
    })

    @POST("event")
    Call<RespuestaEvento> getPosts(@Header("Authorization") String token, @Body RequestBody body);

}
