package com.Nekron.mafia;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {
    private ProgressBar progressSplash;
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressSplash = findViewById(R.id.progressBar_splash);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        progressSplash.setVisibility(View.VISIBLE);
        if (checkNetwork()){
            progressSplash.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, SignupActivity.class);
                    startActivity(intent);
                    finish();

                }
            }, 2000);
        }else{
            progressSplash.setVisibility(View.INVISIBLE);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
            alertDialog.setTitle("Connection error");
            alertDialog.setMessage("Check your internet connection and try again");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(SplashActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialog.show();
        }


    }
    public boolean checkNetwork(){
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            return true;

        }
        return false;
    }
}
