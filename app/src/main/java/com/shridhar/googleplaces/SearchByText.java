package com.shridhar.googleplaces;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;
import me.relex.circleindicator.CircleIndicator;

import static com.shridhar.googleplaces.AppConstants.Constants.API_KEY;
import static com.shridhar.googleplaces.AppConstants.Constants.INDEX;
import static com.shridhar.googleplaces.AppConstants.Constants.KEYS;

public class SearchByText extends AppCompatActivity  implements View.OnClickListener
        ,ServerResponse {

    @BindView(R.id.backArrow)
    ImageView backArrow;

    @BindView(R.id.searchView)
    TextView searchView;

    @BindView(R.id.exploreList)
    ListView exploreList;

    @BindView(R.id.search)
    EditText search;

    @BindView(R.id.progress)
    ProgressBar progress;

    String latLng;
    JSONArray jsonArray;
    private FusedLocationProviderClient mFusedLocationClient;
    Handler handler;
    String searchStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_text);
        ButterKnife.bind(this);

        searchView.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLocation();


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
                        new MakeServerCall(url, SearchByText.this).getResposeByServer();

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

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                searchStr = charSequence.toString();
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
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            String url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="+searchStr+"&inputtype=textquery&fields=photos,formatted_address,name,place_id&locationbias=circle:2000@"+latLng+"&key="+API_KEY;
            new MakeServerCall(url,SearchByText.this).getResposeByServer();

            API_KEY = KEYS.get(INDEX++);
            if(INDEX == KEYS.size()-1)
            {
                INDEX = 0;
            }
        }

    };
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

            final Dialog dialog = new Dialog(SearchByText.this);
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


            ImageSliderAdapter adapter = new ImageSliderAdapter(SearchByText.this,result.getJSONArray("photos"));
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
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                latLng = location.getLatitude()+","+location.getLongitude();
                search.setVisibility(View.VISIBLE);
                search.requestFocus();

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

        Intent intent = new Intent(SearchByText.this,SearchPlace.class);
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

                search.setText("");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serverResponseProcess(String response, String atType) {

        switch (atType)
        {
            case "FIND_PLACE":
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
            jsonArray = jsonObject.getJSONArray("candidates");
            PlaceListAdapter adapter = new PlaceListAdapter(SearchByText.this,jsonArray);
            adapter.notifyDataSetChanged();
            exploreList.setAdapter(adapter);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
