package com.example.whoisapp.peticiones;

import com.example.whoisapp.respuestas.RespuestaRegistro;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RegistroInterface {

    @Headers({
            "Content-Type: application/json"
    })

    @POST("register")
    Call<RespuestaRegistro> getPosts(
            @Body RequestBody body
           );


}
