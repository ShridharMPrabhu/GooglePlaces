package com.shridhar.googleplaces.AppAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shridhar.googleplaces.R;

import org.json.JSONArray;
import org.json.JSONException;

import static com.shridhar.googleplaces.AppConstants.Constants.API_KEY;
import static com.shridhar.googleplaces.AppConstants.Constants.INDEX;
import static com.shridhar.googleplaces.AppConstants.Constants.KEYS;


public class ImageSliderAdapter extends PagerAdapter {

    private JSONArray array;
    private LayoutInflater inflater;
    private Context context;

    public ImageSliderAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array=array;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return array.length();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.image_slider, view, false);
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.image);


        try {
            String image = array.getJSONObject(position).getString("photo_reference");

            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+image+"&sensor=false&key="+API_KEY;

            API_KEY = KEYS.get(INDEX++);
            if(INDEX == KEYS.size()-1)
            {
                INDEX = 0;
            }

            Glide.with(context)
                    .load(url)
                    .into(myImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}