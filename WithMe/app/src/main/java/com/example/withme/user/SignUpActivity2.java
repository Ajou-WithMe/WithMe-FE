package com.example.withme.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.withme.R;
import com.example.withme.retorfit.RetrofitAPI;
import com.example.withme.retorfit.PostEmail;
import com.example.withme.retorfit.SignUpDuplicate;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity2 extends AppCompatActivity implements TextWatcher {

    private EditText editTextEmail, emailCodeText;
    private MainHandler mainHandler;
    private TextView warningMessage, authenticateComplete, messageComplete;
    private Button emailAuthentication, authenticate;
    private LinearLayout layoutAuthenticate;
    private String emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private String email;

    static int value;
    String GmailCode;
    int mailSend = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        Intent intent = new Intent(SignUpActivity2.this, SignUpActivity3.class);

        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextEmail.addTextChangedListener(this);

        warningMessage = (TextView)findViewById(R.id.warningMessage);
        messageComplete = (TextView) findViewById(R.id.messageComplete);
        authenticateComplete = (TextView) findViewById(R.id.authenticateComplete);
        emailCodeText = (EditText)findViewById(R.id.emailCodeText);
        emailAuthentication = (Button)findViewById(R.id.emailAuthentication);
        authenticate = (Button)findViewById(R.id.authenticate);
        layoutAuthenticate = (LinearLayout) findViewById(R.id.layoutAuthenticate);

        // ????????? ???????????? ??????
        // ???????????? ?????? ?????? ????????????, ?????? ????????? ????????? ????????? ?????? ????????? ????????? ?????????.
        authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailCodeText.getText().toString().equals(GmailCode)) {
                    authenticate.setBackgroundResource(R.drawable.radius_1_nonclickable);
                    emailCodeText.setBackgroundResource(R.drawable.edittext_bg_selector_authenticate_complete);
                    emailCodeText.setClickable(false);
                    emailCodeText.setFocusable(false);
                    authenticateComplete.setVisibility(View.INVISIBLE);
                    messageComplete.setText("????????? ?????????????????????!");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            intent.putExtra("email", email);
                            startActivity(intent);
                        }
                    }, 1000);
                } else {
                    emailCodeText.setBackgroundResource(R.drawable.edittext_bg_selector_not_validate);
                    Toast.makeText(getApplicationContext(), "??????????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        email = editTextEmail.getText().toString().trim();
        if (email.matches(emailValidation) && s.length() > 0) {
            editTextEmail.setBackgroundResource(R.drawable.edittext_bg_selector);
            warningMessage.setText("");

            emailAuthentication.setBackgroundResource(R.drawable.radius_3);
            emailAuthentication.setClickable(true);
            authenticate.setBackgroundResource(R.drawable.radius_3);
            authenticate.setClickable(true);

            emailAuthentication.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, Object> input1 = new HashMap<>();
                    input1.put("email", email);
                    HashMap<String, Object> input2 = new HashMap<>();
                    input2.put("email", email);

                    Retrofit retrofit = new retrofit2.Retrofit.Builder()
                            .baseUrl("http://withme-lb-1691720831.ap-northeast-2.elb.amazonaws.com")
                            .addConverterFactory(GsonConverterFactory.create()) //gson converter ??????, gson??? JSON??? ?????? ???????????? ???????????? ????????????.
                            .build();
                    RetrofitAPI retrofitAPI1 = retrofit.create(RetrofitAPI.class);
                    RetrofitAPI retrofitAPI2 = retrofit.create(RetrofitAPI.class);

                    retrofitAPI1.postSignupDuplicate(input1).enqueue(new Callback<SignUpDuplicate>() {
                        @Override
                        public void onResponse(Call<SignUpDuplicate> call, Response<SignUpDuplicate> response) {
                            SignUpDuplicate data = response.body();
                            if(response.isSuccessful()) {
                                Log.e("Duplicate", "Post ??????");
                                Log.e("Duplicate", String.valueOf(data.getStatus()));
                                Log.e("Duplicate", String.valueOf(data.getData())); // true??? ????????? ?????????!

                                if (data.getData() == true) {
                                    emailAuthentication.setText("???????????? ???????????? ?????? ??????");
                                    authenticateComplete.setText("??????????????? ???????????? ??????????????? ??????????????????! ???????????? ?????? ??? ??????????????????.");
                                    mainHandler = new MainHandler();
                                    // ????????? ?????? ??????

                                    if(mailSend == 0) {
                                        value = 300;
                                        // ????????? ?????? ??????
                                        BackgroundThread backgroundThread = new BackgroundThread();
                                        // ????????? ?????????
                                        backgroundThread.start();
                                        mailSend += 1;
                                    } else {
                                        value = 300;
                                    }
                                    retrofitAPI2.postEmail(input2).enqueue(new Callback<PostEmail>() {
                                        @Override
                                        public void onResponse(Call<PostEmail> call, Response<PostEmail> response) {
                                            PostEmail data = response.body();
                                            if(response.isSuccessful()) {
                                                Log.e("Test", "Post ??????");
                                                Log.e("Test", String.valueOf(data.getStatus()));
                                                Log.e("Test", data.getData());
                                                GmailCode = data.getData();
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<PostEmail> call, Throwable t) {
                                            Log.e("Failure", "Post ??????");
                                        }
                                    });
                                } else {
                                    Toast.makeText(SignUpActivity2.this, "????????? ??????????????????. ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                    warningMessage.setText("?????? ????????? ??????????????????. ?????? ?????????????????????!");
                                    editTextEmail.setBackgroundResource(R.drawable.edittext_bg_selector_not_validate);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SignUpDuplicate> call, Throwable t) {
                            Log.e("Failure_Duplicate", "Post ??????");
                        }
                    });
                }
            });
        } else {
            editTextEmail.setBackgroundResource(R.drawable.edittext_bg_selector_not_validate);
            warningMessage.setText("???????????? ????????? ????????? ??????????????????!");
            emailAuthentication.setBackgroundColor(Color.parseColor("#E9E9E9"));
            emailAuthentication.setClickable(false);
            authenticate.setBackgroundColor(Color.parseColor("#E9E9E9"));
            authenticate.setClickable(false);
        }
    }

    // ?????????????????? ???????????? ?????? ???????????? ?????????
    // ?????????????????? ????????? ??????????????? UI??? ????????? ??? ??? ??????.
    class MainHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            int min, sec;

            Bundle bundle = message.getData();
            int value = bundle.getInt("value");

            min = value / 60;
            sec = value % 60;

            // ?????? 10?????? ????????? ?????? 0??? ??? ????????? ???????????? ??????.
            if (sec < 10) {
                // ??????????????? ???????????? ?????????
                emailCodeText.setHint("0" + min + " : 0" + sec);
            } else {
                emailCodeText.setHint("0" + min + " : " + sec);
            }
            emailCodeText.setHintTextColor(Color.parseColor("#FF302B"));
        }
    }

    // ?????? ?????? ????????? ?????? ?????????
    class BackgroundThread extends Thread {
        // 300?????? 5???
        // ?????? ???????????? value ??? ???????????? ???????????? ????????? ?????? ??????.

        public void run() {
            // 300??? ?????? ???????????? ????????? ????????? ?????? ???????????????.
            while(true) {
                value -= 1;
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }

                Message message = mainHandler.obtainMessage();
                // ???????????? ????????? ????????? ????????? ?????? ???????????? ????????????.
                Bundle bundle = new Bundle();
                bundle.putInt("value", value);
                message.setData(bundle);

                // ???????????? ????????? ?????? ?????????
                mainHandler.sendMessage(message);

                if (value <= 0) {
                    GmailCode = "";
                    break;
                }
            }
        }
    }
}