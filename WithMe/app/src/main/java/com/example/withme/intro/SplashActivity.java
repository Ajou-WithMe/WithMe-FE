package com.example.withme.intro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.withme.MainActivity;
import com.example.withme.R;
import com.example.withme.retorfit.RetrofitAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo_1, logo_2;
    private ArrayList<String> protectionPersonName = new ArrayList<>();
    private ArrayList<String> protectionPersonUid = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("http://withme-lb-1691720831.ap-northeast-2.elb.amazonaws.com")
                .addConverterFactory(GsonConverterFactory.create()) //gson converter 생성, gson은 JSON을 자바 클래스로 바꾸는데 사용된다.
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        SharedPreferences sf = getSharedPreferences("storeAccessToken", MODE_PRIVATE);
        String accessToken = sf.getString("AccessToken", "");
        Log.e("AccessToken", accessToken);

        SharedPreferences sf2 = getSharedPreferences("storeAccessToken", MODE_PRIVATE);


        SharedPreferences.Editor editor;
        editor = sf2.edit();

        logo_1 = (ImageView)findViewById(R.id.with_me_logo_white_1);
        logo_2 = (ImageView)findViewById(R.id.with_me_logo);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);

        logo_1.startAnimation(animation);
        logo_2.startAnimation(animation);

        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
        Intent intent2 = new Intent(getApplicationContext(), DescriptionActivity.class);

        retrofitAPI.getAllprotection(accessToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean success = jsonObject.getBoolean("success");

                        if (success == true) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            Log.e("getAllProtection", data.toString());
                            Log.e("protection num", String.valueOf(data.length()));

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject protectionPerson = data.getJSONObject(i);
                                String name = protectionPerson.getString("name");
                                String uid = protectionPerson.getString("uid");
                                protectionPersonName.add(name);
                                protectionPersonUid.add(uid);
                            }
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            Log.e("protectionName", protectionPersonName.toString());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        retrofitAPI.getProfile(accessToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String newAccessToken = response.headers().get("AccessToken");
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean success = jsonObject.getBoolean("success");
                        int status = jsonObject.getInt("status");
                        if (success == true) {
                            if(newAccessToken == null) {
                                Log.e("바뀐 access token", "null"); // 무시!
                            } else {
                                Log.e("바뀐 access token", newAccessToken);
                                editor.putString("AccessToken", newAccessToken);
                                editor.commit();
                            }
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    intent1.putExtra("protectionPersonName", protectionPersonName);
                                    intent1.putExtra("protectionPersonUid", protectionPersonUid);
                                    startActivity(intent1);
                                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    finish();
                                }
                            }, 2000); // 인트로 화면 로딩 시간

                        } else {
                            if (status == 401) { // 인증 오류!
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                        finish();
                                        startActivity(intent2);
                                    }
                                }, 2000); // 인트로 화면 로딩 시간
                                Log.e("인증 오류", String.valueOf(status));
                                Log.e("인증 오류", String.valueOf(success));
                            } else {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                        finish();
                                        startActivity(intent2);
                                    }
                                }, 2000); // 인트로 화면 로딩 시간
                                Log.e("인증 오류는 아니지만 다른 오류", String.valueOf(status));
                                Log.e("인증 오류는 아니지만 다른 오류", String.valueOf(success));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("failure", t.getMessage());
                Log.e("failure", "전송 실패");
                startActivity(intent2);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}