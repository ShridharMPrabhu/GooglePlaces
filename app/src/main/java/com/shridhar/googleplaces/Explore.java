package com.shridhar.googleplaces;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.shridhar.googleplaces.AppAdapters.ImageSliderAdapter;
import com.shridhar.googleplaces.AppAdapters.KeywordsAdapter;
import com.shridhar.googleplaces.AppAdapters.PlaceListAdapter;
import com.shridhar.googleplaces.AppConstants.Constants;
import com.shridhar.googleplaces.ServerCall.MakeServerCall;
import com.shridhar.googleplaces.ServerCall.ServerResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;
import me.relex.circleindicator.CircleIndicator;

import static com.shridhar.googleplaces.AppConstants.Constants.API_KEY;
import static com.shridhar.googleplaces.AppConstants.Constants.INDEX;
import static com.shridhar.googleplaces.AppConstants.Constants.KEYS;

public class Explore extends AppCompatActivity implements View.OnClickListener
                    ,ServerResponse{

    @BindView(R.id.backArrow)
    ImageView backArrow;

    @BindView(R.id.searchView)
    TextView searchView;

    @BindView(R.id.exploreList)
    ListView exploreList;

    @BindView(R.id.keywords)
    HListView keywords;

    @BindView(R.id.progress)
    ProgressBar progress;

    String latLng;
    String type;
    JSONArray jsonArray;
    private FusedLocationProviderClient mFusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        ButterKnife.bind(this);

        searchView.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setKeywords(0);
        getLocation();

        keywords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(latLng != null) {
                    type = view.getTag().toString();

                    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latLng + "&radius=5000&type=" + type + "&key=" + API_KEY;

                    API_KEY = KEYS.get(INDEX++);
                    if(INDEX == KEYS.size()-1)
                    {
                        INDEX = 0;
                    }
                    new MakeServerCall(url, Explore.this).getResposeByServer();

                    setKeywords(position);
                }
                else
                {
                    getLocation();
                }
            }
        });

        exploreList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> adapterView, View view, int i, long l) {

                Object object2 = view.getTag();
                if(object2 != null) {
                    String obj = object2.toString();
                    try {
                        JSONObject object = new JSONObject(obj);
                        String placeID = object.getString("place_id");
                        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID + "&key=" + API_KEY;
                        new MakeServerCall(url, Explore.this).getResposeByServer();

                        API_KEY = KEYS.get(INDEX++);
                        if (INDEX == KEYS.size() - 1) {
                            INDEX = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void showPlaceInfo(String response) {

        try {

            JSONObject object = new JSONObject(response);
            JSONObject result = object.getJSONObject("result");
            JSONArray array = result.getJSONArray("address_components");
            String city="",state="",coutnry="";
            for(int i=0;i<array.length();i++)
            {
                JSONObject object1 = array.getJSONObject(i);
                JSONArray array1 = object1.getJSONArray("types");
                String type = array1.getString(0);
                if(type.equalsIgnoreCase("country"))
                {
                    coutnry = object1.getString("long_name");
                }
                else if(type.equalsIgnoreCase("administrative_area_level_1"))
                {
                    state = object1.getString("long_name");
                }
                else if(type.equalsIgnoreCase("locality"))
                {
                    city = object1.getString("long_name");
                }
            }
            String nameStr = result.getString("name");
            JSONArray types = result.getJSONArray("types");
            String typeStr = "";
            for(int i=0;i<types.length();i++)
            {
                typeStr += types.getString(i)+ ", ";
            }

            final Dialog dialog = new Dialog(Explore.this);
            dialog.setContentView(R.layout.place_info);

            TextView name = dialog.findViewById(R.id.name);
            TextView cityTV = dialog.findViewById(R.id.city);
            TextView stateTV = dialog.findViewById(R.id.state);
            TextView countryTV = dialog.findViewById(R.id.country);
            TextView categories = dialog.findViewById(R.id.categories);
            ViewPager pager = dialog.findViewById(R.id.Pager);
            CircleIndicator indicator = dialog.findViewById(R.id.PagerIndicator);
            ImageView close = dialog.findViewById(R.id.close);

            name.setText(nameStr);
            cityTV.setText(city);
            stateTV.setText(state);
            countryTV.setText(coutnry);
            categories.setText(typeStr);


            ImageSliderAdapter adapter = new ImageSliderAdapter(Explore.this,result.getJSONArray("photos"));
            pager.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            indicator.setViewPager(pager);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();



        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setKeywords(int position) {
        try {
            JSONArray array = new JSONArray(Constants.KEYWORD_ARRAY);
            type = array.getJSONObject(position).getString("keyword");
            KeywordsAdapter adapter = new KeywordsAdapter(Explore.this, array , position);
            keywords.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                latLng = location.getLatitude()+","+location.getLongitude();
                keywords.setVisibility(View.VISIBLE);

                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latLng + "&radius=5000&type=" + type + "&key=" + API_KEY;

                API_KEY = KEYS.get(INDEX++);
                if(INDEX == KEYS.size()-1)
                {
                    INDEX = 0;
                }
                new MakeServerCall(url, Explore.this).getResposeByServer();

            }
        });
    }
    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.searchView:
                openSearchActivity();
                break;

            case R.id.backArrow:
                finish();
                break;
        }
    }

    private void openSearchActivity() {

        Intent intent = new Intent(Explore.this,SearchPlace.class);
        startActivityForResult(intent,0);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(data != null) {
                String address = data.getStringExtra("address");
                latLng = data.getStringExtra("latLng");

                searchView.setText(address);

                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latLng + "&radius=5000&type=" + type + "&key=" + API_KEY;

                API_KEY = KEYS.get(INDEX++);
                if(INDEX == KEYS.size()-1)
                {
                    INDEX = 0;
                }
                new MakeServerCall(url, Explore.this).getResposeByServer();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serverResponseProcess(String response, String atType) {

        switch (atType)
        {
            case "NEARBY":
                updateUI(response);
                break;

            case "PLACE_DETAILS":
                showPlaceInfo(response);
                break;
        }

    }

    private void updateUI(String response) {

        try {

            progress.setVisibility(View.GONE);
            exploreList.setVisibility(View.VISIBLE);
            JSONObject jsonObject = new JSONObject(response);
            jsonArray = jsonObject.getJSONArray("results");
            PlaceListAdapter adapter = new PlaceListAdapter(Explore.this,jsonArray);
            adapter.notifyDataSetChanged();
            exploreList.setAdapter(adapter);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
