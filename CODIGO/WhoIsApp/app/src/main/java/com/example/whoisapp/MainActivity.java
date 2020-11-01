package com.example.whoisapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    Button btn_registrar;
    Button btn_ingresar;
    EditText et_email;
    EditText et_contraseña;
    TextView tv_bateria;
    static ApiThread hilo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        hilo = new ApiThread(this);
        hilo.start();

        et_email = findViewById(R.id.textViewUsuario);
        et_contraseña = findViewById(R.id.textViewContraseña);
        btn_ingresar = findViewById(R.id.botonIngresar);
        btn_registrar = findViewById(R.id.botonRegistrar);
        tv_bateria = findViewById(R.id.textViewBateria);

        if(verificarToken()){
            iniciarBienvenidosActivity();
        } else{

            nivelDeBateria();

            btn_ingresar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    botones(v);
                }
            });

            btn_registrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    botones(v);
                }
            });
        }
    }

    private void botones(View v) {

            switch (v.getId()){
                case R.id.botonIngresar:
                    if(verificarConexionInternet((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))){
                        hilo.apiEnum = CodigoApiEnum.LOGIN;
                    }else{
                        mostrarMensaje(MainActivity.this,"ERROR DE CONEXION", "No se ha detectado conexión a internet. Intente nuevamente.", true);
                    }
                    break;
                case R.id.botonRegistrar:
                    Intent intentReg = new Intent(MainActivity.this, RegistrarActivity.class);
                    startActivity(intentReg);
                    break;
            }
    }
    private void nivelDeBateria() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int nivelBase = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int escala = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int nivel = -1;
                if (nivelBase >= 0 && escala > 0) {
                    nivel = (nivelBase * 100) / escala;
                }
                tv_bateria.setText(getString(R.string.porc_bateria_string) + " " + nivel + " %");
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    public static boolean verificarConexionInternet(ConnectivityManager conexion) {
        return conexion.getNetworkInfo(conexion.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                conexion.getNetworkInfo(conexion.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    public static void mostrarMensaje(Context context, String titulo, String mensaje, boolean alerta){

        new AlertDialog.Builder(context).setTitle(titulo).setMessage(mensaje)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon((alerta)?android.R.drawable.ic_dialog_alert:android.R.drawable.ic_dialog_info)
                .show();
    }

    public String getEmail(){
        return et_email.getText().toString().trim();
    }

    public String getContraseña(){
        return et_contraseña.getText().toString().trim();
    }

    public void iniciarBienvenidosActivity(){
        Intent intentLogin = new Intent(MainActivity.this,BienvenidoActivity.class);
        startActivity(intentLogin);
        finish();
    }

    public static void iniciarActivity(Context context){
        Intent intentLogin = new Intent(context, MainActivity.class);
        context.startActivity(intentLogin);
    }

    public static void guardarTokens(String token, String tokenRefresh, Context context) {
        SharedPreferences sharedPreferences =  context.getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", String.valueOf(token));
        editor.putString("token_refresh", String.valueOf(tokenRefresh));
        editor.commit();
    }

    public boolean verificarToken(){
        SharedPreferences sharedPreferences =  getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        return !token.equals("");
    }

}
