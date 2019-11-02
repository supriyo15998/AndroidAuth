package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText Name,Email,Pass,Conf_pass;
    Button reg_button;
    AlertDialog.Builder builder;
    TextView login_text;
    ProgressDialog progressDialog;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Name = findViewById(R.id.reg_name);
        Email = findViewById(R.id.reg_email);
        Pass = findViewById(R.id.reg_password);
        Conf_pass = findViewById(R.id.cnf_password);
        reg_button = (Button)findViewById(R.id.reg_btn);
        login_text = (TextView)findViewById(R.id.sign_up);
        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Requesting...");
        progressDialog.setCancelable(false);

        sessionManager = new SessionManager(this);

        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Name.getText().toString().equals("")||Email.getText().toString().equals("")||Pass.getText().toString().equals("")||Conf_pass.getText().toString().equals(""))
                {
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong");
                    builder.setMessage("Please fill all the fields");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else if(!(Pass.getText().toString().equals(Conf_pass.getText().toString())))
                {
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong");
                    builder.setMessage("Two Passwords Should Match");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Pass.setText("");
                            Conf_pass.setText("");
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else
                {

                    progressDialog.show();

                    Log.d("RegisterActivity", "Registering Now");
                    Call<ResponseBody> call = RetrofitClient
                            .getmInstance()
                            .getApi()
                            .createUser(Name.getText().toString(), Email.getText().toString(), Pass.getText().toString(), Conf_pass.getText().toString());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            progressDialog.dismiss();

                            Log.d("RegisterActivity", String.valueOf(response.code()));
                            String s = null;
                            try{
                                if(response.code() == 201)
                                {
                                    s = response.body().string();
                                    //Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_LONG).show();
                                }
                                else if(response.code() == 422)
                                {
                                    s = response.errorBody().string();
                                    //Toast.makeText(RegisterActivity.this,s,Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Log.e("RegisterActivity", "Something went wrong!");
                                }
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            if(s != null)
                            {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    sessionManager.setAccessToken(jsonObject.getString("access_token"));
                                    //Log.d("RegisterActivity", String.valueOf(jsonObject.getString("message")));
                                    Toast.makeText(RegisterActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                            progressDialog.dismiss();

                            Log.e("RegisterActivity", t.getMessage());
                            t.printStackTrace();
                            Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
