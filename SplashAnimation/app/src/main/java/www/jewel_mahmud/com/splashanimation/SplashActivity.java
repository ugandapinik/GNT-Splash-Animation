package www.jewel_mahmud.com.splashanimation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SplashActivity extends Activity{
    //variables
    private boolean connectionStatus;
    private ImageView splashLoadImage;
    Thread splashLoadThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashLoadImage = (ImageView) findViewById(R.id.splashImage);

        Glide.with(getApplicationContext()).load(R.drawable.loadingimage).asBitmap().centerCrop().into(new BitmapImageViewTarget(splashLoadImage) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                splashLoadImage.setImageDrawable(circularBitmapDrawable);
            }
        });

        /**
         * when application load, display the splash loading image
         */

        if(checkConnectionStatus()){
            splash();
        }
        else{
            showAlertDialog(SplashActivity.this, "Internet Connection",
                    "You don't have internet connection", false);
        }



        //check the internet connection
        //if connection established move to mainActivity
        //or show alertDialog

    }


    public void splash() {
        rotateAndZoomAnimation();
        Thread timerTread = new Thread() {
            public void run() {
                try {
                    sleep(2300);

                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timerTread.start();
    }

    /**
     * @desc display the splash Animation
     */
    public void splashLoad(){

        rotateAndZoomAnimation();
        splashLoadThread = new Thread(){
            @Override
            public void run() {
                try{
                    //sleep after 2.3 second
                    sleep(2300);


                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            }
        };
    }


    /**
     * @desc Check Connection Status.
     * @return
     */
    public boolean checkConnectionStatus(){

        connectionStatus = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI")){
                if (netInfo.isConnected()) {
                    //Toast.makeText(getApplicationContext(), "Connected to WIFI", Toast.LENGTH_SHORT).show();
                    connectionStatus = true;
                }
            }

            if(netInfo.getTypeName().equalsIgnoreCase("MOBILE")){
                if(netInfo.isConnected()){
                    //Toast.makeText(getApplicationContext(), "Connected to Mobile", Toast.LENGTH_SHORT).show();
                    connectionStatus = true;
                }
            }
        }

        return connectionStatus;
    }


    public void showAlertDialog(final Context context, String title, String message, Boolean status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("Enable WIFI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //enable WIFI
                WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
                showMessage("Enable WIFI");
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        builder.setNegativeButton("Enable DATA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //enable Mobile Data

                setMobileDataEnabled(getApplicationContext(), true);


            }
        });

        builder.create();
        // Showing Alert Message
        builder.show();
    }

    private void setMobileDataEnabled(Context context, boolean enabled) {
        final ConnectivityManager conman =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(
                    iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }



    /**
     * @desc Animate the splash Image
     * @desc Zoom and Rotation
     */
    public void rotateAndZoomAnimation(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_and_zoom);
        splashLoadImage.startAnimation(animation);
    }

    /**
     * @desc Show Toast Message
     * @param message
     */
    public void showMessage(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
