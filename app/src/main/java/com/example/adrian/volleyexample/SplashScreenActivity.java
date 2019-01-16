package com.example.adrian.volleyexample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;
import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.adrian.volleyexample.utilerias.Utilerias;
import com.example.adrian.volleyexample.volley.MySingleton;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.TextView;
import android.widget.Toast;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "SplashScreenActivity";
    private RelativeLayout rlNoAutorizado;
    private TextView txtCodigoActivacion;
    private ProgressBar progressBarSplash;


    private static final int PERMISSION_ALL = 1;
    public static final String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE};

    private boolean isPermissionVisible = false;
    static final String STATE_PERMISSION_VISIBLE = "state_permission_visible";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        //oPreferencias = new Preferencias(this);

        rlNoAutorizado = findViewById(R.id.RelativeNoAutorizado);
        txtCodigoActivacion = findViewById(R.id.txtCodigoActivacion);
        progressBarSplash = findViewById(R.id.progressBarSplash);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        progressBarSplash.setVisibility(View.VISIBLE); //View.VISIBLE
        rlNoAutorizado.setVisibility(View.GONE); //View.

        //if (checkPlayServices()) {

            if (savedInstanceState != null) {
                // Restore value of members from saved state
                isPermissionVisible = savedInstanceState.getBoolean(STATE_PERMISSION_VISIBLE);
            } else {
                // Probably initialize members with default values for a new instance
            }
            if (!isPermissionVisible) {
                if (!Utilerias.hasPermissions(this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                    isPermissionVisible = true;
                }
            }

            verificarDispositivo();
        //}
    }
    // Check screen orientation or screen rotate event here

    public void verificarDispositivo() {
        String REQUEST_TAG = "SPLASHSCREENACTIVITY.VERIFICARDISPOSITIVO";

        if (Utilerias.isOnline(getApplicationContext())) {
            final JSONObject localJSONObject = new JSONObject();
            try {//creamos el objeto con toda la informacion del dispositivo
                localJSONObject.put(getApplicationContext().getResources().getString(R.string.strImei), Utilerias.getIMEI(getApplicationContext()));
                localJSONObject.put(getApplicationContext().getResources().getString(R.string.strNumeroCelular), Utilerias.getNumberCelular(getApplicationContext()));
                localJSONObject.put(getApplicationContext().getResources().getString(R.string.strNumeroSerie), Utilerias.getNumeroSerie());
                localJSONObject.put(getApplicationContext().getResources().getString(R.string.strMarca), Utilerias.getManufacturer());
                localJSONObject.put(getApplicationContext().getResources().getString(R.string.strModelo), Utilerias.getModel());
                localJSONObject.put(getApplicationContext().getResources().getString(R.string.strMacWlan), Utilerias.getMacWlan());
                localJSONObject.put(getApplicationContext().getResources().getString(R.string.strSistemaOperativo), "ANDROID");
                localJSONObject.put(getApplicationContext().getResources().getString(R.string.strVersionSistemaOperativo), Utilerias.getVersionSdkStr());

                StringRequest stringRequest = new StringRequest(Request.Method.POST, getApplicationContext().getResources().getString(R.string.strUrlRegistrarDispositivo),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject prResult = new JSONObject(response);
                                    if (!prResult.getJSONObject("meta").getBoolean("isValid")) {
                                        progressBarSplash.setVisibility(View.GONE);
                                        /*Intent oIntent = new Intent(getApplicationContext(), NotificacionPopUp.class);
                                        oIntent.putExtra("tipo_notificacion", NotificacionPopUp.ERROR_CODE);
                                        oIntent.putExtra("mensaje", prResult.getJSONObject("meta").getString("message"));
                                        startActivity(oIntent);*/
                                    } else {
                                        progressBarSplash.setVisibility(View.GONE);
                                        JSONObject resultado = prResult.getJSONObject("data").getJSONObject("datos");
                                        if (resultado.getString("autorizado").equals("S")) {
                                            /*Intent intent = new Intent(SplashScreenActivity.this, NotificacionesActivity.class);
                                            startActivity(intent);
                                            finish();*/
                                        } else {
                                            rlNoAutorizado.setVisibility(View.VISIBLE);
                                            txtCodigoActivacion.setText("Codigo Activacion: \n" + resultado.getString("codigo_activacion"));
                                        }
                                    }
                                } catch (JSONException ex) {
                                    progressBarSplash.setVisibility(View.GONE);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("error", "errorResponse al verificar dispositivo.");
                                progressBarSplash.setVisibility(View.GONE);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        try {
                            params.put(getApplicationContext().getResources().getString(R.string.strImei), localJSONObject.getString(getApplicationContext().getResources().getString(R.string.strImei)));
                            params.put(getApplicationContext().getResources().getString(R.string.strToken), "");
                            params.put(getApplicationContext().getResources().getString(R.string.strNumeroCelular), localJSONObject.getString(getApplicationContext().getResources().getString(R.string.strNumeroCelular)));
                            params.put(getApplicationContext().getResources().getString(R.string.strNumeroSerie), localJSONObject.getString(getApplicationContext().getResources().getString(R.string.strNumeroSerie)));
                            params.put(getApplicationContext().getResources().getString(R.string.strMarca), localJSONObject.getString(getApplicationContext().getResources().getString(R.string.strMarca)));
                            params.put(getApplicationContext().getResources().getString(R.string.strModelo), localJSONObject.getString(getApplicationContext().getResources().getString(R.string.strModelo)));
                            params.put(getApplicationContext().getResources().getString(R.string.strMacWlan), localJSONObject.getString(getApplicationContext().getResources().getString(R.string.strMacWlan)));
                            params.put(getApplicationContext().getResources().getString(R.string.strSistemaOperativo), localJSONObject.getString(getApplicationContext().getResources().getString(R.string.strSistemaOperativo)));
                            params.put(getApplicationContext().getResources().getString(R.string.strVersionSistemaOperativo), localJSONObject.getString(getApplicationContext().getResources().getString(R.string.strVersionSistemaOperativo)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return params;
                    }
                };
                // Adding JsonObject request to request queue
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, REQUEST_TAG);
            } catch (JSONException localJSONException) {
                Log.i("error", localJSONException.getMessage());
                progressBarSplash.setVisibility(View.GONE);
            }
        }else{
            Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.strNoCuentaConInternet),Toast.LENGTH_LONG).show();
            progressBarSplash.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL:
                isPermissionVisible = false;
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),"SE REQUIEREN LOS PERMISOS PARA ACCEDER A LA INFORMACION DEL DISPOSITIVO.",Toast.LENGTH_LONG).show();
                    if(!Utilerias.hasPermissions(this, PERMISSIONS)){
                        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                    }
                }else{
                    if (Utilerias.isOnline(getApplicationContext())) {
                        verificarDispositivo();
                    } else {
                        progressBarSplash.setVisibility(View.GONE);
                    }
                }
                break;
            default:
                break;
        }
    }

    /*private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }*/

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(STATE_PERMISSION_VISIBLE, isPermissionVisible);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        isPermissionVisible = savedInstanceState.getBoolean(STATE_PERMISSION_VISIBLE);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
