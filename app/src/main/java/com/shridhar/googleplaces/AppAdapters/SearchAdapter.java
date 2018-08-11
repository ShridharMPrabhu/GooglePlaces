package com.shridhar.googleplaces.AppAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shridhar.googleplaces.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchAdapter extends BaseAdapter {

    Context context;
    JSONArray jsonArray;

    public SearchAdapter(Context context, JSONArray jsonArray)
    {
        this.context = context;
        this.jsonArray = jsonArray;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.search_element, null, true);

        }

        TextView name = (TextView) convertView.findViewById(R.id.name);


        try {
            JSONObject obj = (JSONObject) jsonArray.get(position);
            String nameStr = obj.getString("description");
            convertView.setTag(obj.getString("place_id"));
            name.setText(nameStr);

        } catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
}
