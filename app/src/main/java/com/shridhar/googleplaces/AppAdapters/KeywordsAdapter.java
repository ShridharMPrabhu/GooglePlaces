package com.shridhar.googleplaces.AppAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shridhar.googleplaces.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KeywordsAdapter extends BaseAdapter {

    Context context;
    JSONArray jsonArray;
    int selectedPosition;

    public KeywordsAdapter(Context context, JSONArray jsonArray, int selectedPosition)
    {
        this.context = context;
        this.jsonArray = jsonArray;
        this.selectedPosition = selectedPosition;
    }
    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView name;
        public RelativeLayout layout;
        public ImageView check;
        public ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.keyword_element, null, true);

        }

        ViewHolder holder = new ViewHolder();

        holder.name = (TextView) convertView.findViewById(R.id.name);
        holder.layout = (RelativeLayout) convertView.findViewById(R.id.layout);


        if(position == selectedPosition)
        {
            Log.d("position ",position+" , "+selectedPosition);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.layout.setBackground(ContextCompat.getDrawable(context,R.drawable.blue_element_bg));
                holder.name.setTextColor(ContextCompat.getColor(context,R.color.white));
            }

        }
        else
        {
            Log.d("position ",position+" , "+selectedPosition);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.layout.setBackground(ContextCompat.getDrawable(context,R.drawable.white_element_bg));
                holder.name.setTextColor(ContextCompat.getColor(context,R.color.black));
            }
        }

        try {
            JSONObject obj = (JSONObject) jsonArray.get(position);
            String nameStr = obj.getString("name");
            String tag = obj.getString("keyword");
            holder.name.setText(nameStr);
            convertView.setTag(tag);

        } catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
}
