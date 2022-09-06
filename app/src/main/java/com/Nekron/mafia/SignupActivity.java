package com.Nekron.mafia;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import com.Nekron.mafia.apputil.AppConfig;



import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;



import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;



import java.util.HashMap;
import java.util.Map;



public class SignupActivity extends AppCompatActivity {

    private EditText userEmail, userName, myPass;
    private Button btnRegister;
    private TextView loginMyAcc, tv_response;
    private ProgressBar progressSignup;
    private AppConfig appConfig;

    private boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        appConfig = new AppConfig(this);
        userEmail = (EditText) findViewById(R.id.userEmail);
        userName = (EditText) findViewById(R.id.usernameReg);
        myPass = (EditText) findViewById(R.id.myPassReg);
        btnRegister = (Button) findViewById(R.id.btnSignup);
        loginMyAcc = (TextView) findViewById(R.id.loginInAcc);
        tv_response = (TextView) findViewById(R.id.tvResponse);
        progressSignup = (ProgressBar) findViewById(R.id.progress_signup);

        progressSignup.setVisibility(View.INVISIBLE);
        if (appConfig.isUserLogin()){
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            intent.putExtra("username", appConfig.getUsernameOfUser());
            startActivity(intent);
            this.finish();
        }



        loginMyAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                SignupActivity.this.finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAllFieldsChecked = checkAllFields();
                if (isAllFieldsChecked) {
                    progressSignup.setVisibility(View.VISIBLE);

                    StringRequest request = new StringRequest(Request.Method.POST, "https://danashafiei.ir/signup.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            progressSignup.setVisibility(View.INVISIBLE);

                            Log.d("from onResponse()", response);
                            response = response.trim();
                            if (response.equals("Successful_Registration")) {
                                appConfig.updateUserLoginStatus(true);
                                appConfig.saveUsernameOfUser(userName.getText().toString());
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.putExtra("username", appConfig.getUsernameOfUser());
                                startActivity(intent);
                                finish();

                            }else if (response.equals("User_already_exists")){
                                tv_response.setText("User already exists!");
                                tv_response.setTextColor(Color.YELLOW);
                            }else if (response.equals("Failed_to_Register")){
                                tv_response.setText("username or email already exists!");
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
                            progressSignup.setVisibility(View.INVISIBLE);
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
                            params.put("email", userEmail.getText().toString());
                            params.put("password", myPass.getText().toString());


                            return params;
                        }
                    };

                    Volley.newRequestQueue(SignupActivity.this).add(request);





                }
            }
        });
    }
    private boolean checkAllFields(){
        if (userEmail.length() == 0){
            userEmail.setError("This field is required!");
            return false;
        }
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





}