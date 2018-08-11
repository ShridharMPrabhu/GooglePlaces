package com.shridhar.googleplaces.AppAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shridhar.googleplaces.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.shridhar.googleplaces.AppConstants.Constants.API_KEY;
import static com.shridhar.googleplaces.AppConstants.Constants.INDEX;
import static com.shridhar.googleplaces.AppConstants.Constants.KEYS;

public class PlaceListAdapter extends BaseAdapter {

    Context context;
    JSONArray jsonArray;

    public PlaceListAdapter(Context context, JSONArray jsonArray)
    {
        this.context = context;
        this.jsonArray = jsonArray;
    }
    @Override
    public int getCount() {

        if(jsonArray.length() > 6)
        {
            return 6;
        }
        else
        {
            return jsonArray.length();
        }
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.place_element, null, true);

        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);


        try {
            JSONObject obj = (JSONObject) jsonArray.get(position);
            name.setText(obj.getString("name"));

            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=300&photoreference="+obj.getJSONArray("photos").getJSONObject(0).getString("photo_reference")+"&sensor=false&key="+API_KEY;

            API_KEY = KEYS.get(INDEX++);
            if(INDEX >= KEYS.size()-1)
            {
                INDEX = 0;
            }

            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions().centerCrop())
                    .into(imageView);

            convertView.setTag(obj.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
}
