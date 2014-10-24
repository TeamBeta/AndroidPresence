package com.example.androidpresence.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.androidpresence.MainActivity;
import com.example.androidpresence.R;
 
public class ExpandableListAdapter extends BaseExpandableListAdapter {
 
    private Context _context;
    private ArrayList<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<String>> _listDataChild;
    private HashMap<String, ArrayList<String>> _listDataChildTypes;
    private String currentType = null;
 
    public ExpandableListAdapter(Context context, ArrayList<String> listDataHeader,
            HashMap<String, ArrayList<String>> listContactInformation, HashMap<String, ArrayList<String>> listDataChildTypes) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listContactInformation;
        this._listDataChildTypes = listDataChildTypes;
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }
    
    public Object getType(int groupPosition, int childPosititon) {
    	return this._listDataChildTypes.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }
    
    public void update(ArrayList<String> _GroupStrings, HashMap<String, ArrayList<String>> _ChildStrings){
    	_listDataHeader =  (ArrayList<String>) _GroupStrings;
    	_listDataChild =  (HashMap<String, ArrayList<String>>) _ChildStrings;
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
    	Log.d("getChildView", "step 1");
        final String childText = (String) getChild(groupPosition, childPosition);
        final String childType = (String) getType(groupPosition, childPosition);
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.listItem); 
 
        txtListChild.setText(childText);
        txtListChild.setTag(childType);
        
        convertView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				TextView textView = (TextView) v.findViewById(R.id.listItem); 
				String serviceType = textView.getTag().toString();
				if (serviceType.toLowerCase().equals("facebook")) {
					Intent intent = new Intent("android.intent.category.LAUNCHER");
					intent.setClassName("com.facebook.katana", "com.facebook.katana.LoginActivity");
					MainActivity.context.startActivity(Intent.createChooser(intent, "Facebook is not installed"));
				}
				else if (serviceType.toLowerCase().equals("gmail")) {
					//Intent intent = new Intent("android.intent.category.LAUNCHER");
					Intent sky = new Intent("android.intent.action.VIEW", Uri.parse("https://talkgadget.google.com/hangouts/extras/talk.google.com/myhangout"));
					//intent.setClassName("com.google.android.talk", "com.google.android.apps.babel.phone.BabelProfileActionActivity");
					//intent.setPackage("com.google.android.talk");
					MainActivity.context.startActivity(sky);
				}
				else if (serviceType.toLowerCase().equals("hipchat")) {
					//Launch hipchat App if installed
				}
				return false;
			}
		});
        return convertView;
    }  
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.listHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}