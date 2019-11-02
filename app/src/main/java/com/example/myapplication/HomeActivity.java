package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final TextView welcomeText = findViewById(R.id.welcomeText);

        sessionManager = new SessionManager(this);

        if (sessionManager.getAccessToken() != null && !sessionManager.getAccessToken().isEmpty()) {

            RetrofitClient.getmInstance().getApi()
                    .getUser("Bearer " + sessionManager.getAccessToken())
                    .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    Log.d("Home Activity", String.valueOf(response.code()));

                    if (response.isSuccessful()) {
                        try {
                            Log.d("Home Activity", response.body().toString());

                            JSONObject j = new JSONObject(response.body().string());

                            welcomeText.setText("Welcome " + j.getJSONObject("user").getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    Log.e("Home Activity", t.getMessage());
                    t.printStackTrace();
                }
            });
        } else {
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            Toast.makeText(getApplicationContext(), "Please login first!", Toast.LENGTH_LONG).show();
            finish();
        }

        findViewById(R.id.logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.setAccessToken(null);
                Toast.makeText(getApplicationContext(), "Logged out!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
