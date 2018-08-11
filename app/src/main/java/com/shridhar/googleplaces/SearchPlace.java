package com.shridhar.googleplaces;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.shridhar.googleplaces.AppAdapters.SearchAdapter;
import com.shridhar.googleplaces.ServerCall.MakeServerCall;
import com.shridhar.googleplaces.ServerCall.ServerResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shridhar.googleplaces.AppConstants.Constants.API_KEY;
import static com.shridhar.googleplaces.AppConstants.Constants.INDEX;
import static com.shridhar.googleplaces.AppConstants.Constants.KEYS;

public class SearchPlace extends AppCompatActivity implements View.OnClickListener
                    ,ServerResponse
                    ,TextWatcher
                    ,AdapterView.OnItemClickListener{

    @BindView(R.id.backArrow)
    ImageView backArrow;

    @BindView(R.id.searchView)
    EditText searchView;

    @BindView(R.id.searchList)
    ListView searchList;

    @BindView(R.id.currentLoc)
    RelativeLayout currentLoc;

    Handler handler;
    String searchStr;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);
        ButterKnife.bind(this);

        backArrow.setOnClickListener(this);
        currentLoc.setOnClickListener(this);

        searchView.addTextChangedListener(this);

        searchList.setOnItemClickListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+searchStr+"&key="+API_KEY;
            new MakeServerCall(url,SearchPlace.this).getResposeByServer();

            API_KEY = KEYS.get(INDEX++);
            if(INDEX == KEYS.size()-1)
            {
                INDEX = 0;
            }
        }

    };

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.backArrow:
                finish();
                break;

            case R.id.currentLoc:
                getLocation();
                break;
        }
    }

    @Override
    public void serverResponseProcess(String response, String atType) {

        switch (atType)
        {
            case "AUTOCOMPLETE":
                updateUI(response);
                break;

            case "PLACE_DETAILS":
                sendResponse(response);
                break;
        }

    }

    private void sendResponse(String response) {

        try {

            JSONObject object = new JSONObject(response);
            String status = object.getString("status");
            if(status.equalsIgnoreCase("OK"))
            {
                JSONObject obj = object.getJSONObject("result");
                String address = obj.getString("formatted_address");
                JSONObject geometry = obj.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                String latLng = location.getString("lat")+","+location.getString("lng");
                Intent intent=new Intent();
                intent.putExtra("address",address);
                intent.putExtra("latLng",latLng);
                setResult(0,intent);
                finish();
            }

        }catch (Exception e){
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

                String latLng = location.getLatitude() + "," + location.getLongitude();

                Intent intent=new Intent();
                intent.putExtra("address","Current Location");
                intent.putExtra("latLng",latLng);
                setResult(0,intent);
                finish();

            }
        });
    }
    private void updateUI(String response) {

        try{

            JSONObject object = new JSONObject(response);
            JSONArray array = object.getJSONArray("predictions");

            currentLoc.setVisibility(View.GONE);
            searchList.setVisibility(View.VISIBLE);
            SearchAdapter adapter = new SearchAdapter(SearchPlace.this,array);
            searchList.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Search
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        searchStr = charSequence.toString();
        if(searchStr.equalsIgnoreCase(""))
        {
            currentLoc.setVisibility(View.VISIBLE);
            searchList.setVisibility(View.GONE);
        }
        if(handler == null) {
            handler = new Handler();
        }
        else {
            handler.removeCallbacks(runnable);
        }
        handler.postDelayed(runnable,2000);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    //Adapter
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String placeID = view.getTag().toString();
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeID+"&key="+API_KEY;
        new MakeServerCall(url,SearchPlace.this).getResposeByServer();

        API_KEY = KEYS.get(INDEX++);
        if(INDEX == KEYS.size()-1)
        {
            INDEX = 0;
        }
    }
}
