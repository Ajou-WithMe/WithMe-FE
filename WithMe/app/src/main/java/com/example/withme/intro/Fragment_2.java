package com.example.withme.intro;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.Lottie;
import com.airbnb.lottie.LottieAnimationView;
import com.example.withme.R;

public class Fragment_2 extends Fragment {

    private LottieAnimationView lottieAnimationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.description_frame2, container, false);

        lottieAnimationView = (LottieAnimationView) v.findViewById(R.id.animationView2);
        lottieAnimationView.playAnimation();
        lottieAnimationView.setRepeatCount(-1);

        return v;
    }
}