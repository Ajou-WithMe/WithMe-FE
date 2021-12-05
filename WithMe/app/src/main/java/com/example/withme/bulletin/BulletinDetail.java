package com.example.withme.bulletin;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.withme.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class BulletinDetail extends Fragment {

    private ImageButton option;
    private long id;
    private LinearLayout postImgLayout;
    private List<Address> finalLocations;
    private String title, description, contents, radius, name, phone, createdAt;
    private double longitude, latitude;
    private TextView addComment, postTitle, clothes, activityRadius, content, nameAge, phoneNumber, finalLocation, date;

    ArrayList<String> fileList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bulletindetail, container, false);

        postTitle = (TextView) rootView.findViewById(R.id.postTitle);
        clothes = (TextView) rootView.findViewById(R.id.clothes);
        content = (TextView) rootView.findViewById(R.id.content);
        nameAge = (TextView) rootView.findViewById(R.id.nameAge);
        activityRadius = (TextView) rootView.findViewById(R.id.activityRadius);
        phoneNumber = (TextView) rootView.findViewById(R.id.phoneNumber);
        finalLocation = (TextView) rootView.findViewById(R.id.finalLocation);
        date = (TextView) rootView.findViewById(R.id.date);
        addComment = (TextView) rootView.findViewById(R.id.addComment);

        postImgLayout = (LinearLayout) rootView.findViewById(R.id.postImgLayout);

        // 빈 데이터 리스트 생성.
        final ArrayList<String> items = new ArrayList<String>() ;
        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, items) ;

        // listview 생성 및 adapter 지정.
        final ListView listview = (ListView) rootView.findViewById(R.id.comment_list) ;
        listview.setAdapter(adapter) ;

        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getLong("id"); //id 받기.
            title = bundle.getString("title");
            name = bundle.getString("name");
            description = bundle.getString("description");
            contents = bundle.getString("content");
            createdAt = bundle.getString("createdAt");
            radius = bundle.getString("activityRadius");
            phone = bundle.getString("phone");
            fileList = bundle.getStringArrayList("files");
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
        }

        Geocoder g = new Geocoder(getContext());

        try {
            finalLocations = g.getFromLocation(latitude,longitude,10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("test","입출력오류");
        }
        if(finalLocations!=null){
            if(finalLocations.size()==0){
                finalLocation.setText("주소찾기 오류");
            }else{
                Log.d("찾은 주소",finalLocations.get(0).toString());
                finalLocation.setText(finalLocations.get(0).getAddressLine(0));
            }
        }

        finalLocation.setSelected(true);
        clothes.setSelected(true);
        activityRadius.setSelected(true);

        StringTokenizer st = new StringTokenizer(createdAt,"T");
        String tmpDate = st.nextToken();
        tmpDate = tmpDate.replaceAll("-",".");
        String tokenized = tmpDate.substring(2, 10);
        Log.e("fileList", String.valueOf(fileList));

        date.setText(tokenized);
        postTitle.setText(title);
        clothes.setText(description);
        activityRadius.setText(radius);
        content.setText(contents);
        nameAge.setText(name);
        phoneNumber.setText(phone);

        for (int i = 0; i < fileList.size(); i++) {
            ImageView imageView = new ImageView(getActivity().getApplicationContext());
            ViewGroup.LayoutParams image= new LinearLayout.LayoutParams(360, 360);
            imageView.setLayoutParams(image);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    360,
                    360);
            lp.setMargins(0,0,36,0);
            imageView.setLayoutParams(lp);

            Glide.with(getActivity().getApplicationContext()).load(fileList.get(i)).into(imageView);
            if (fileList.get(i).equals("null")) {
                imageView.setBackgroundColor(Color.parseColor("#BDBDBD"));
            }

            postImgLayout.addView(imageView);
        }

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count;
                count = adapter.getCount();

                // 아이템 추가.
                items.add("LIST" + Integer.toString(count + 1));

                // listview 갱신
                adapter.notifyDataSetChanged();
            }
        });

        option = (ImageButton) rootView.findViewById(R.id.option);

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle result = new Bundle();
                result.putLong("id", id);

                BottomSheetDialogBoard bottomSheetDialogBoard = new BottomSheetDialogBoard();
                bottomSheetDialogBoard.show(getActivity().getSupportFragmentManager(), "fuck");
                bottomSheetDialogBoard.setArguments(result);
            }
        });

        return rootView;
    }
}