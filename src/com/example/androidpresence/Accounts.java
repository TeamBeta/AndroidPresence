package com.example.androidpresence;

import com.example.androidpresence.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
 
public class Accounts extends Fragment{
 
	TextView nameLabel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.accounts, container, false);
    	nameLabel = (TextView) rootView.findViewById(R.id.nameLabel);
    	nameLabel.setText(MainActivity.userName);
        return rootView;
    }
    
}