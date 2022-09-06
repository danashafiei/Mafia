package com.Nekron.mafia;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.Nekron.mafia.apputil.AppConfig;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private EditText userName, myPass;
    private Button btnLogin;
    private TextView createNewAcc, tv_response;
    private ProgressBar progressLogin;
    private AppConfig appConfig;
    private boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appConfig = new AppConfig(this);

        userName = (EditText) findViewById(R.id.usernameLog);
        myPass = (EditText) findViewById(R.id.myPassLog);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        createNewAcc = (TextView) findViewById(R.id.createnewacc);
        tv_response = (TextView) findViewById(R.id.tvResponse1);
        progressLogin = (ProgressBar) findViewById(R.id.progress_login);

        progressLogin.setVisibility(View.INVISIBLE);

        createNewAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAllFieldsChecked = checkAllFields();
                if (isAllFieldsChecked) {

                    progressLogin.setVisibility(View.VISIBLE);

                    StringRequest request = new StringRequest(Request.Method.POST, "https://danashafiei.ir/login.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressLogin.setVisibility(View.INVISIBLE);

                            Log.d("from onResponse()", response);
                            response = response.trim();
                            if (response.equals("Successful_Login")){
                                appConfig.updateUserLoginStatus(true);
                                appConfig.saveUsernameOfUser(userName.getText().toString());
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("username", appConfig.getUsernameOfUser());
                                startActivity(intent);
                                finish();

                            }else if (response.equals("Username_or_password_is_incorrect")){
                                tv_response.setText("Wrong username or password!");
                                tv_response.setTextColor(Color.YELLOW);

                            }else if (response.equals("Error_connect_to_server")){
                                tv_response.setText("Error connect to server!");
                                tv_response.setTextColor(Color.RED);

                            }else{
                                tv_response.setText("Error!");
                                tv_response.setTextColor(Color.RED);

                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressLogin.setVisibility(View.INVISIBLE);
                            tv_response.setText("Error in server or Your is offline!");
                            tv_response.setTextColor(Color.RED);
                            tv_response.setTextSize(12);

                        }
                    }
                    ){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError{
                            Map<String, String> params = new HashMap<>();

                            params.put("username", userName.getText().toString());
                            params.put("password", myPass.getText().toString());

                            return params;

                        }
                    };
                    Volley.newRequestQueue(LoginActivity.this).add(request);

                }
            }
        });

    }
    private boolean checkAllFields(){
        if (userName.length() == 0){
            userName.setError("This field is required!");
            return false;
        }

        if (myPass.length() == 0){
            myPass.setError("This field is required!");
            return false;
        }else if (myPass.length() < 8){
            myPass.setError("Password must be minimum 8 characters!");
            return false;
        }else if (myPass.length() > 50){
            myPass.setError("Password must be maximum 50 characters!");
            return false;
        }
        return true;
    }
    /*private boolean checkUser(String user){
        char[] ch = user.toCharArray();
        for (int i = 0; i < user.length(); i++){
            for (int j = ; )
        }
    }*/



}