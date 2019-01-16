package com.example.adrian.volleyexample.utilerias;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;


import com.example.adrian.volleyexample.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by adrian on 11/04/2018.
 */

public class Utilerias {

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isOnline(Context prContext) {
        try
        {
            ConnectivityManager oConnectivityManager =
                    (ConnectivityManager) prContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if(oConnectivityManager != null){
                NetworkInfo netInfo = oConnectivityManager.getActiveNetworkInfo();
                return netInfo != null && netInfo.isConnectedOrConnecting();
            }
            return false;
        }
        catch (Exception e) {
            // TODO: handle exception
            Log.d("Error checar internet:",e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("resource")
    public static String convertirImageToBase64(String mCurrentPhotoPath) throws Exception{
        File file = new File(mCurrentPhotoPath);
        FileInputStream imageInFile = new FileInputStream(file);
        byte imageData[] = new byte[(int) file.length()];
        imageInFile.read(imageData);
        // Converting Image byte array into Base64 String
        return Base64.encodeToString(imageData, Base64.DEFAULT);

    }

    public static String reducirTamanoEncodedUriImage(Context context,Uri prPathImagen) throws Exception {
        Bitmap photo = BitmapFactory.decodeStream(
                context.getContentResolver().openInputStream(prPathImagen));

        //Convert bitmap to byte array
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
        byte[] byteArray = bytes .toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateComplete(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return formatearString(sdf.format(new Date()));
    }

    //eliminar caracteres especiales a un string
    public static String formatearString(String prNombre){
        String nueva = prNombre.replaceAll("[^\\dA-Za-z]", "");

        nueva = nueva.replaceAll("[\\W]|_", "");

        return nueva.replaceAll("[^a-zA-Z0-9]", "");
    }

    /** Check if this device has a camera */
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    //obtener el identificador del divice
    public static String getIdentificadorMd5(Context prContext,String pr1){
        String myAndroidDeviceId;
        TelephonyManager oTelephonyManager = (TelephonyManager) prContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (oTelephonyManager.getDeviceId() != null){
            myAndroidDeviceId = oTelephonyManager.getDeviceId();
        }else{//el device no es un telefono
            myAndroidDeviceId = getMacWlan();

        }
        if(myAndroidDeviceId.length() > 0) {
            //codificar el identificador
            return md5(myAndroidDeviceId+prContext.getResources().getString(R.string.strSeparadorIdentificadorCompuesto)+pr1);
        }
        return "";
    }
    public static String getIdentificador(Context prContext){
        String myAndroidDeviceId;
        TelephonyManager oTelephonyManager = (TelephonyManager) prContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (oTelephonyManager.getDeviceId() != null){
            myAndroidDeviceId = oTelephonyManager.getDeviceId();
        }else{//el device no es un telefono
            myAndroidDeviceId = getMacWlan();

        }
        return myAndroidDeviceId;

    }
    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    //obtener informacion del dispositivo
    public static String getManufacturer()
    {
        return Build.MANUFACTURER;
    }
    public static String getModel()
    {
        return Build.MODEL;
    }
    public static String getNumberCelular(Context prContext){
        TelephonyManager telephonyManager = ((TelephonyManager) prContext.getSystemService(Context.TELEPHONY_SERVICE)); //"phone"
        String number1 = telephonyManager.getLine1Number();
        return number1 != null ? number1 : "";
    }
    public static JSONObject getAccounts(Context prContext){
        AccountManager accountManager = AccountManager.get(prContext);
        JSONObject localJSONObject = new JSONObject();
        try
        {
            for (Account localAccount : accountManager.getAccounts()) {
                if(localAccount.type.equals("com.google")){
                    localJSONObject.put(localAccount.type, localAccount.name);
                }
            }
        }
        catch (Exception localException)
        {
            String str = "Exception: " + localException.getMessage();
            System.err.println(str);
        }
        return localJSONObject;
    }
    public static String getNetworkOperatorName(Context prContext){
        TelephonyManager telephonyManager = ((TelephonyManager) prContext.getSystemService(Context.TELEPHONY_SERVICE)); //"phone"
        if (telephonyManager != null) {
            return telephonyManager.getNetworkOperatorName();
        }else{
            return "DESCONOCIDO";
        }
    }
    public static String getNetworkOperatorCountry(Context prContext){
        TelephonyManager telephonyManager = ((TelephonyManager) prContext.getSystemService(Context.TELEPHONY_SERVICE)); //"phone"
        if (telephonyManager != null) {
            return telephonyManager.getNetworkCountryIso();
        }else{
            return "DESCONOCIDO";
        }
    }
    public static String getNetworkType(Context prContext){
        TelephonyManager telephonyManager = ((TelephonyManager) prContext.getSystemService(Context.TELEPHONY_SERVICE)); //"phone"
        if (telephonyManager != null) {
            int valor = telephonyManager.getNetworkType();
            switch (valor)
            {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_GSM:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:
                case TelephonyManager.NETWORK_TYPE_IWLAN:
                    //case TelephonyManager.NETWORK_TYPE_LTE_CA:
                    return "4G";
                default:
                    return "DECONOCIDO";
            }
        }else{
            return "DECONOCIDO";
        }
    }
    public static boolean getWifiConnected(Context prContext) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) prContext.getSystemService(Context.CONNECTIVITY_SERVICE)); //"connectivity"
        return connectivityManager != null && connectivityManager.getNetworkInfo(1).isConnected();
    }
    public static float getBatteryPercentage(Context prContext){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = prContext.registerReceiver(null, ifilter);

        // Are we charging / charged?
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if ((level == -1) || (scale == -1))
            return 50.0f;
        return 100.0f * (level / (float) scale);
    }
    public static boolean getBatteryCharging(Context prContext){
        return prContext.getApplicationContext().registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("status", -1) == 2;
    }
    public static double[] getLatLng(Context prContext){
        LocationManager localLocationManager = (LocationManager) prContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE); //location
        List localList = localLocationManager.getProviders(true);
        Location localLocation = null;
        for (int i = -1 + localList.size(); ; i--)
        {
            if (i >= 0)
            {
                localLocation = localLocationManager.getLastKnownLocation((String)localList.get(i));
                if (localLocation == null)
                    continue;
            }
            double[] arrayOfDouble = { 0.0D, 0.0D };
            if (localLocation != null)
            {
                arrayOfDouble[0] = localLocation.getLatitude();
                arrayOfDouble[1] = localLocation.getLongitude();
            }
            return arrayOfDouble;
        }
    }
    public static String getMacWlan() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    String sd = Integer.toHexString(b & 0xFF);
                    res1.append( sd.length() == 1 ? "0" + sd + ":" : sd + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            return "";
        }
        return "";
    }
    public static String getIMEI(Context prContext){
        String myAndroidDeviceId;
        TelephonyManager oTelephonyManager = (TelephonyManager) prContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (oTelephonyManager.getDeviceId() != null){
            myAndroidDeviceId = oTelephonyManager.getDeviceId();
        }else{//el device no es un telefono
            myAndroidDeviceId = "";
        }
        return myAndroidDeviceId;
    }
    public static String getVersionSdkStr() {
        return String.format("%s", Build.VERSION.RELEASE);
    }
    public static String getNumeroSerie(){
        return Build.SERIAL;
    }

    //rotar imagen
    public static void rotarImagenFile(Context prContext,String prFilePath, Uri prUriImagen) throws Exception {

        ExifInterface oExifInterface = new ExifInterface(prFilePath);
        int orientation = oExifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Float valorRotar;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                valorRotar = 90.0f;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                valorRotar = 180.0f;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                valorRotar = 270.0f;
                break;
            default:
                valorRotar = null;
                break;
        }

        if(valorRotar != null) {
            Matrix matrix = new Matrix();
            matrix.postRotate(valorRotar);
            Bitmap original;

            if( Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                original = BitmapFactory.decodeStream(prContext.getContentResolver().openInputStream(prUriImagen),null, null);
            }else{
                original = BitmapFactory.decodeFile(prFilePath);
            }

            //Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.ic_error170dp);
            Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);

            FileOutputStream out = new FileOutputStream(prFilePath);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            out.close();
        }
    }

    // metodo para checar si una aplicacion esta en segundo plano
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    //metodo para checar si una actividad en concreto en primer plano
    public static boolean isForegroundActivity(Context ctx, String TagActivity) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (TagActivity.equalsIgnoreCase(task.topActivity.getClassName()))
                return true;
        }

        return false;
    }

    // Reproducir un sonido de notificacion
    public static void playNotificationSound(Context prContext) {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + prContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(prContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

