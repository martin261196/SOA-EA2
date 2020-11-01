package com.example.whoisapp.peticiones;

import com.example.whoisapp.respuestas.RespuestaToken;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;

public interface TokenInterface {

    @Headers({
            "Content-Type: application/json"
    })

    @PUT("refresh")
    Call<RespuestaToken> getPosts(@Header("Authorization") String token_refresh);

}
