package com.example.withme.bulletin;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.withme.MainActivity;
import com.example.withme.R;
import com.example.withme.group.BottomSheetDialogMain;
import com.example.withme.retorfit.RetrofitAPI;
import com.example.withme.settings.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyBulletin extends Fragment {

    MainActivity activity;

    Retrofit retrofit = new retrofit2.Retrofit.Builder()
            .baseUrl("http://withme-lb-1691720831.ap-northeast-2.elb.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create()) //gson converter 생성, gson은 JSON을 자바 클래스로 바꾸는데 사용된다.
            .build();
    RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

    private String accessToken, phoneNumber;
    private int postLength;
    private List<Address> finalLocations;
    private LinearLayout posts;
    private ScrollView scrollView;
    private TextView post;
    private ImageButton write, settings, group;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        activity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mybulletin, container, false);

        SharedPreferences sf = getActivity().getSharedPreferences("storeAccessToken", MODE_PRIVATE);
        accessToken = sf.getString("AccessToken", "");

        posts = (LinearLayout) rootView.findViewById(R.id.posts);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);

        settings = rootView.findViewById(R.id.settings);
        group = rootView.findViewById(R.id.group);

        post = (TextView) rootView.findViewById(R.id.post);

        Bundle bundle = new Bundle(1); // 파라미터의 숫자는 전달하려는 값의 갯수
        bundle.putString("AccessToken", accessToken);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onFragmentChange(0);
            }
        });

        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogMain bottomSheetDialogMain = new BottomSheetDialogMain();
                bottomSheetDialogMain.show(getActivity().getSupportFragmentManager(), "bottomSheet");
                bottomSheetDialogMain.setArguments(bundle);
            }
        });

        write = (ImageButton) rootView.findViewById(R.id.write);

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();

                WriteBulletin writeBulletin = new WriteBulletin();

                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_container, writeBulletin).commit();
                transaction.addToBackStack(null);
            }
        });


        retrofitAPI.getProfile(accessToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean success = jsonObject.getBoolean("success");

                        if (success == true) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            phoneNumber = data.getString("phone");
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
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Settings();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
        });

        retrofitAPI.getMyPost(accessToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean success = jsonObject.getBoolean("success");

                        if (success == true) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            postLength = data.length();

                            Log.e("getMyPost", String.valueOf(postLength));
                            Log.e("getMyPost", data.toString());

                            for (int i = 0; i < postLength; i++) {

                                JSONObject post = data.getJSONObject(i);

                                long id = post.getInt("id");
                                String img = post.getString("img");
                                String title = post.getString("title");
                                String clothes = post.getString("description");
                                int state = post.getInt("state");
                                double latitude = post.getDouble("latitude");
                                double longitude = post.getDouble("longitude");
                                String createdAt = post.getString("createdAt");

                                LinearLayout linearLayout = new LinearLayout(getContext());
                                linearLayout.setId(i);
                                ViewGroup.LayoutParams layout= new LinearLayout.LayoutParams(939, 198);

                                linearLayout.setLayoutParams(layout);
                                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                                posts.addView(linearLayout);

                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        939,
                                        198);
                                lp.setMargins(0,72,0,0);
                                linearLayout.setLayoutParams(lp);

                                ImageView postImg = new ImageView(getContext());
                                ViewGroup.LayoutParams imgParam= new LinearLayout.LayoutParams(300, 198);
                                postImg.setLayoutParams(imgParam);

                                Glide.with(getActivity().getApplicationContext()).load(img).into(postImg);
                                if (img.equals("null") && state == 0) {
                                    postImg.setBackgroundResource(R.drawable.no_profile);
                                } else if (state == 1) {
                                    Glide.with(getContext()).load(R.drawable.state_changed).into(postImg);
                                } else if (state == 0){
                                    Glide.with(getContext()).load(img).into(postImg);
                                }
                                linearLayout.addView(postImg);

                                LinearLayout detailPost = new LinearLayout(getContext());
                                detailPost.setId(i);
                                ViewGroup.LayoutParams layout_detail = new LinearLayout.LayoutParams(606, 198);

                                detailPost.setLayoutParams(layout_detail);
                                detailPost.setOrientation(LinearLayout.VERTICAL);

                                linearLayout.addView(detailPost);
                                detailPost.setGravity(Gravity.RIGHT);

                                TextView tv_title = new TextView(getActivity().getApplicationContext());
                                tv_title.setText(title);
                                tv_title.setTextSize(14);
                                tv_title.setTextColor(Color.parseColor("#222222"));

                                LinearLayout.LayoutParams lp_text1 = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                lp_text1.setMargins(36,30,0,0);

                                LinearLayout.LayoutParams lp_text2 = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                lp_text2.setMargins(36,12,0,0);

                                LinearLayout.LayoutParams lp_text3 = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                lp_text3.setMargins(36,6,0,0);

                                detailPost.addView(tv_title);
                                tv_title.setLayoutParams(lp_text3);

                                TextView tv_final = new TextView(getActivity().getApplicationContext());

                                Geocoder g = new Geocoder(getContext());

                                try {
                                    finalLocations = g.getFromLocation(latitude,longitude,10);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.d("test","입출력오류");
                                }
                                if(finalLocations!=null){
                                    if(finalLocations.size()==0){
                                        tv_final.setText("마지막 목격 장소 : 주소 찾기 오류");
                                    }else{
                                        tv_final.setText("마지막 목격 장소 : " + finalLocations.get(0).getAddressLine(0));
                                    }
                                    tv_final.setTextSize(12);
                                    tv_final.setTextColor(Color.parseColor("#222222"));
                                    tv_final.setSelected(true);
                                    tv_final.setSingleLine();
                                    tv_final.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                }

                                detailPost.addView(tv_final);
                                tv_final.setLayoutParams(lp_text1);

                                TextView tv_clothes = new TextView(getActivity().getApplicationContext());
                                tv_clothes.setText("인상 착의 : " + clothes);
                                tv_clothes.setTextSize(12);
                                tv_clothes.setTextColor(Color.parseColor("#222222"));

                                detailPost.addView(tv_clothes);
                                tv_clothes.setLayoutParams(lp_text2);

                                linearLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle result = new Bundle();
                                        result.putLong("id", id);

                                        retrofitAPI.getPostDetail(accessToken, id).enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (response.isSuccessful()) {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                                        boolean success = jsonObject.getBoolean("success");

                                                        if (success == true) {
                                                            JSONObject data = jsonObject.getJSONObject("data");
                                                            String title = data.getString("title");
                                                            JSONArray files = data.getJSONArray("files");
                                                            String createdAt = data.getString("createdAt");
                                                            String content = data.getString("content");
                                                            String activityRadius = data.getString("activityRadius");
                                                            String name = data.getString("name");
                                                            String contact = data.getString("contact");
                                                            String description = data.getString("description");
                                                            ArrayList<String> fileList = new ArrayList<>();
                                                            double latitude = data.getDouble("latitude");
                                                            double longitude = data.getDouble("longitude");


                                                            for (int file = 0; file < files.length(); file++) {
                                                                fileList.add(files.getString(file));
                                                            }

                                                            result.putString("title", title);
                                                            result.putString("createdAt", createdAt);
                                                            result.putStringArrayList("files", fileList);
                                                            result.putString("content", content);
                                                            result.putString("activityRadius", activityRadius);
                                                            result.putString("name", name);
                                                            result.putString("description", description);
                                                            result.putString("phone", contact);
                                                            result.putDouble("latitude", latitude);
                                                            result.putDouble("longitude", longitude);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Fragment fragment = new BulletinDetail();
                                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                                    FragmentTransaction fmt = fm.beginTransaction();
                                                    fragment.setArguments(result);

                                                    fmt.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                                                }

                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                            }
                                        });
                                    }
                                });
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

            }
        });
        return rootView;
    }
}