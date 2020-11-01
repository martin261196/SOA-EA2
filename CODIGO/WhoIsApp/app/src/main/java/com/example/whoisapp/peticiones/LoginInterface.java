package com.example.whoisapp.peticiones;

import com.example.whoisapp.respuestas.RespuestaLogin;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginInterface {

    @Headers({
            "Content-Type: application/json"
    })

    @POST("login")
    Call<RespuestaLogin> getPosts(@Body RequestBody body);

}
