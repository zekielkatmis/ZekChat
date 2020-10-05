package com.example.zekchat.Utils;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.zekchat.R;

public class ChangeFragment {

    private Context context;

    public ChangeFragment(Context context) { this.context = context; }

    public void change (Fragment fragment){
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()

                .replace(R.id.fragment_container, fragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void changeWithParameter(Fragment fragment, String userId){

        //transport information between fragments
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        fragment.setArguments(bundle);

        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()

                .replace(R.id.fragment_container, fragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}
