package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView signup_text;
    Button loginButton;
    EditText email,password;
    AlertDialog.Builder builder;
    SessionManager sessionManager;
    //@SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signup_text = (TextView)findViewById(R.id.sign_up);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Requesting...");
        progressDialog.setCancelable(false);
        sessionManager = new SessionManager(this);

        if (sessionManager.getAccessToken() != null && !sessionManager.getAccessToken().isEmpty()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        signup_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
        loginButton = (Button)findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().equals("") || password.getText().toString().equals("")) {
                    builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Something went wrong");
                    builder.setMessage("Please fill all the details");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else
                {
                    progressDialog.show();
                    Call<ResponseBody> call = RetrofitClient.getmInstance().getApi().loginUser(email.getText().toString(), password.getText().toString());
                    call.enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            progressDialog.dismiss();
                            Log.d("MainActivity", String.valueOf(response.code()));
                            //Log.d("MainActivity", String.valueOf(response.body()));

                            if (response.code() == 401)
                            {
                                Log.d("MainActivity", "Unauthorized");
                                Toast.makeText(MainActivity.this,"Unauthorized", Toast.LENGTH_LONG).show();
                            }

                            if (response.isSuccessful()) {
                                try {
                                    JSONObject j = new JSONObject(response.body().string());

                                    //Log.d("MainActivity", )

                                    Log.d("MainActivity", "Response is successful!");
                                    Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_LONG).show();
                                    sessionManager.setAccessToken(j.getString("access_token"));
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e("MainActivity", "Something went wrong!");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("MainActivity", t.getMessage());
                            t.printStackTrace();
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

}
