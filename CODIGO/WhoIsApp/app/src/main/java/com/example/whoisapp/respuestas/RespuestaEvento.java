package com.example.whoisapp.respuestas;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespuestaEvento {

    @SerializedName("success")
    @Expose
    public boolean success;
    @SerializedName("env")
    @Expose
    public String env;
    @SerializedName("event")
    @Expose
    public Evento event;

    private class Evento {

        @SerializedName("type_events")
        @Expose
        public String type_events;
        @SerializedName("dni")
        @Expose
        public Integer dni;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("id")
        @Expose
        public Integer id;

    }

}
