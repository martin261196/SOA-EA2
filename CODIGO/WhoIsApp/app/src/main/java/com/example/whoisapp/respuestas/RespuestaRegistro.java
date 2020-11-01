package com.example.whoisapp.respuestas;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespuestaRegistro {

    @SerializedName("success")
    @Expose
    public boolean success;
    @SerializedName("env")
    @Expose
    public String env;
    @SerializedName("token")
    @Expose
    public String token;
    @SerializedName("token_refresh")
    @Expose
    public String token_refresh;
}
