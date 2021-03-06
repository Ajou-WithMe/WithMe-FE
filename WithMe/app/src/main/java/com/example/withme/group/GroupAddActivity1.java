package com.example.withme.group;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.withme.MainActivity;
import com.example.withme.R;

public class GroupAddActivity1 extends AppCompatActivity {

    ConstraintLayout groupJoin, groupCreate;
    ImageButton xButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add1);

        groupCreate = (ConstraintLayout) findViewById(R.id.groupCreate);
        groupJoin = (ConstraintLayout) findViewById(R.id.groupJoin);

        xButton = findViewById(R.id.xButton);

        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAddActivity1.this, MainActivity.class);
                startActivity(intent);
            }
        });

        groupCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAddActivity1.this, GroupActivity1.class);
                startActivity(intent);
            }
        });

        groupJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAddActivity1.this, GroupAddActivity2.class);
                startActivity(intent);
            }
        });
    }
}