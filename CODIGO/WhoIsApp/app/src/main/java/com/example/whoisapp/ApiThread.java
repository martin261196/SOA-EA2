package com.example.whoisapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.example.whoisapp.peticiones.EventoInterface;
import com.example.whoisapp.peticiones.LoginInterface;
import com.example.whoisapp.peticiones.RegistroInterface;
import com.example.whoisapp.peticiones.TokenInterface;
import com.example.whoisapp.respuestas.RespuestaEvento;
import com.example.whoisapp.respuestas.RespuestaLogin;
import com.example.whoisapp.respuestas.RespuestaRegistro;
import com.example.whoisapp.respuestas.RespuestaToken;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiThread extends Thread {

    public CodigoApiEnum apiEnum = CodigoApiEnum.IDLE;
    private Context context;
    private Retrofit retrofit;
    private Timer timer;

    public ApiThread(Context context) {
        this.context = context;
        retrofit = new Retrofit.Builder().baseUrl("http://so-unlam.net.ar/api/api/")
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public synchronized void setContext(Context context){
        this.context = context;
    }

    private class ErrorResponse {

        public String msg;
        public Boolean status;
    }

    @Override
    public void run() {

        while(true) {
            switch(apiEnum) {
                case IDLE:
                    break;
                case LOGIN:
                    autenticar(((MainActivity)context).getEmail(), ((MainActivity)context).getContraseña());
                    apiEnum = CodigoApiEnum.IDLE;
                    break;
                case TOKEN:
                    actualizarToken();
                    break;
                case REGISTER:
                    String nombre = ((RegistrarActivity)context).getNombre();
                    String apellido = ((RegistrarActivity)context).getApellido();
                    Integer dni = ((RegistrarActivity)context).getDNI();
                    String email = ((RegistrarActivity)context).getEmail();
                    String contraseña = ((RegistrarActivity)context).getContraseña();
                    Integer comision = ((RegistrarActivity)context).getComision();
                    registrar(nombre, apellido, dni, email, contraseña, comision);
                    apiEnum = CodigoApiEnum.IDLE;
                    break;
                case CERRAR_SESION:
                    cancelContador();
                    apiEnum = CodigoApiEnum.IDLE;
                    break;
            }
        }
    }

    private synchronized void autenticar(final String email, String contraseña) {

        LoginInterface loginInterface = retrofit.create(LoginInterface.class);

        Map<String, Object> jsonParams = new HashMap<>();
        jsonParams.put("email",email);
        jsonParams.put("password",contraseña);

        Call<RespuestaLogin> call = loginInterface.getPosts(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString()));

        call.enqueue(new Callback<RespuestaLogin>() {

            @Override
            public void onResponse(Call<RespuestaLogin> call, Response<RespuestaLogin> response) {
                if(response.isSuccessful()){
                    iniciarContador();
                    MainActivity.guardarTokens(response.body().token, response.body().token_refresh, context);
                    guardarEvento("LOGIN",
                            "El usuario con email " + email + " se ha logueado correctamente a las " + new SimpleDateFormat("dd/MM/yyyy - HH.mm.ss").format(new Date()),
                            response.body().token);
                    ((MainActivity)context).iniciarBienvenidosActivity();

                }else{
                    try {
                        Gson g = new Gson();
                        ErrorResponse error = g.fromJson(response.errorBody().string(), ErrorResponse.class);
                        MainActivity.mostrarMensaje(context,"HA OCURRIDO UN ERROR", error.msg, true);
                    } catch (IOException e) {
                        System.out.println("Error");
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaLogin> call, Throwable t) {
                if(t!=null)
                {
                    t.printStackTrace();
                }
            }
        });
    }

    private synchronized void guardarEvento(final String typeEvent, String description, String token){

        EventoInterface eventoInterface = retrofit.create(EventoInterface.class);
        Map<String, Object> jsonParams = new HashMap<>();
        jsonParams.put("env","PROD");
        jsonParams.put("type_events",typeEvent);
        jsonParams.put("description",description);


        Call<RespuestaEvento> call = eventoInterface.getPosts("Bearer " + token, RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString()));

        call.enqueue(new Callback<RespuestaEvento>() {
            @Override
            public void onResponse(Call<RespuestaEvento> call, Response<RespuestaEvento> response) {
                if(response.isSuccessful()){
                    System.out.println("Se ha registrado un evento del tipo "+ typeEvent);
                }else{
                    System.out.println("No se pudo registrar un evento del tipo "+typeEvent);
                }
            }

            @Override
            public void onFailure(Call<RespuestaEvento> call, Throwable t) {
                if(t!=null)
                {
                    System.out.println("Ocurrio un error en el envio del evento.");
                }
            }
        });
    }

    private synchronized void registrar(String nombre, String apellido, Integer dni, final String email, String contraseña, Integer comision){
        RegistroInterface registroInterface = retrofit.create(RegistroInterface.class);

        Map<String, Object> jsonParams = new HashMap<>();
        jsonParams.put("env","PROD");
        jsonParams.put("name",nombre);
        jsonParams.put("lastname",apellido);
        jsonParams.put("dni",dni);
        jsonParams.put("email",email);
        jsonParams.put("password",contraseña);
        jsonParams.put("commission",comision);

        String request = (new JSONObject(jsonParams)).toString();

        Call<RespuestaRegistro> call = registroInterface.getPosts(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), request));

        call.enqueue(new Callback<RespuestaRegistro>() {
            @Override
            public void onResponse(Call<RespuestaRegistro> call, Response<RespuestaRegistro> response) {
                if(response.isSuccessful()){
                    iniciarContador();
                    MainActivity.guardarTokens(response.body().token, response.body().token_refresh, context);
                    guardarEvento("REGISTRO",
                            "El usuario con email " + email + " se ha registrado correctamente a las " + new SimpleDateFormat("dd/MM/yyyy - HH.mm.ss").format(new Date()),
                            response.body().token);
                    MainActivity.mostrarMensaje(context, "REGISTRO EXITOSO", "Se completo el registro correctamente", false);
                    ((RegistrarActivity)context).iniciarBienvenidosActivity();
                }else{
                    Gson g = new Gson();
                    try {
                        ErrorResponse error = g.fromJson(response.errorBody().string(), ErrorResponse.class);
                        MainActivity.mostrarMensaje(context,"ERROR DE REGISTRO", error.msg, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaRegistro> call, Throwable t) {
                if(t!=null)
                {
                    t.printStackTrace();
                }
            }
        });
    }

    private synchronized void iniciarContador(){
        timer = new Timer(true);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                apiEnum = CodigoApiEnum.TOKEN;
                cancel();
            }
        };

        timer.schedule(task, 3600000); // 30 minutos
    }

    private synchronized void cancelContador() {
        if(timer != null) {
            timer.cancel();
        }
    }

    private synchronized void actualizarToken(){
        apiEnum = CodigoApiEnum.IDLE;

        if(MainActivity.verificarConexionInternet((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE))){

            TokenInterface tokenInterface = retrofit.create(TokenInterface.class);

            SharedPreferences sharedPreferences =  context.getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token_refresh", "");

            Call<RespuestaToken> call = tokenInterface.getPosts("Bearer " + token);

            call.enqueue(new Callback<RespuestaToken>() {
                @Override
                public void onResponse(Call<RespuestaToken> call, Response<RespuestaToken> response) {
                    if (response.isSuccessful()) {
                        MainActivity.guardarTokens(response.body().token, response.body().token_refresh, context);
                        guardarEvento("ACTUALIZAR TOKEN",
                                "El token se ha actualizado correctamente a las " + new SimpleDateFormat("dd/MM/yyyy - HH.mm.ss").format(new Date()),
                                response.body().token);
                        iniciarContador();
                    } else {
                        Toast.makeText(context, "ERROR DE ACTUALIZACION: Debe volver a iniciar sesión.", Toast.LENGTH_SHORT).show();
                        apiEnum = CodigoApiEnum.CERRAR_SESION;
                        MainActivity.iniciarActivity(context);
                    }
                }

                @Override
                public void onFailure(Call<RespuestaToken> call, Throwable t) {
                    Toast.makeText(context, "ERROR DE ENVIO DE TOKEN REFRESH", Toast.LENGTH_SHORT).show();
                    apiEnum = CodigoApiEnum.CERRAR_SESION;
                    MainActivity.iniciarActivity(context);
                }
            });
        } else {
            Toast.makeText(context, "NO SE HA DETECTADO CONEXION A INTERNET", Toast.LENGTH_SHORT).show();
            apiEnum = CodigoApiEnum.CERRAR_SESION;
            MainActivity.iniciarActivity(context);

        }

    }

}
