package com.example.whoisapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistrarActivity extends AppCompatActivity {

    EditText et_nombre;
    EditText et_apellido;
    EditText et_dni;
    EditText et_email;
    EditText et_contraseña;
    EditText et_comision;
    Button btn_registrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        et_nombre = findViewById(R.id.editTextNombre);
        et_apellido = findViewById(R.id.editTextApellido);
        et_dni = findViewById(R.id.editTextDNI);
        et_email = findViewById(R.id.editTextEmail);
        et_contraseña = findViewById(R.id.editTextContraseña);
        et_comision = findViewById(R.id.editTextComision);
        btn_registrar = findViewById(R.id.botonConfirmarRegistro);

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mensaje = verificarCampos();

                if(mensaje != ""){
                    MainActivity.mostrarMensaje(RegistrarActivity.this,"ERROR DE REGISTRO",mensaje, true);
                }else{
                    if(MainActivity.verificarConexionInternet((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))){
                        MainActivity.hilo.apiEnum = CodigoApiEnum.REGISTER;
                    }else{
                        MainActivity.mostrarMensaje(RegistrarActivity.this,"ERROR DE CONEXION", "No se ha detectado conexión a internet. Intente nuevamente.", true);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        MainActivity.hilo.setContext(this);
        super.onResume();
    }

    public String getNombre(){
        return et_nombre.getText().toString().trim();
    }

    public String getApellido(){
        return et_apellido.getText().toString().trim();
    }

    public int getDNI(){
        return Integer.parseInt(et_dni.getText().toString().trim());
    }

    public String getEmail(){
        return et_email.getText().toString().trim();
    }

    public String getContraseña(){
        return et_contraseña.getText().toString().trim();
    }

    public int getComision(){
        return Integer.parseInt(et_comision.getText().toString().trim());
    }

    public void iniciarBienvenidosActivity(){
        Intent intentRegistro = new Intent(RegistrarActivity.this,BienvenidoActivity.class);
        startActivity(intentRegistro);
    }

    public String verificarCampos(){
        if(et_nombre.getText().toString().trim().equals("")){
            return "Debe ingresar un nombre válido para continuar";
        }else if(et_apellido.getText().toString().trim().equals("")){
            return "Debe ingresar un apellido válido para continuar";
        }else if(et_dni.length() < 6 || et_dni.getText().toString().trim().equals("")){
            return "Debe ingresar un documento válido para continuar";
        }else if(et_email.getText().toString().trim().equals("")){
            return "Debe ingresar un email válido para continuar";
        }else if(et_contraseña.getText().toString().trim().equals("")) {
            return "Debe ingresar una clave válido para continuar";
        }else if(et_contraseña.length()<8){
            return "La contraseña debe tener al menos 8 digitos";
        }else if(et_comision.getText().toString().trim().equals("")){
            return "Debe ingresar una comisión válido para continuar";
        }
        return "";
    }
}
