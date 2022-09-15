/*TODO:
 *Create new FindCity button that will prompt user with a pop up text field.
 *Create a separate page to display search results
 *Create a separate page for saved cities to quickly select
 *Use user's location to create a default weather page upon opening

 */

package com.firstapp.app;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements LocationListener {

    Button cityButton, idButton, nameButton;
    EditText et_dataInput;

    TextView tv_temperature, tv_feelsLike, tv_tempMin, tv_tempMax, tv_humidity, tv_cityName, tv_weatherCond;

    String apiKey = "f5c49ede6b0561d6a9e4ba8da29d09de";
    String url;

    public static String city;

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //assign values to each control in the layout page

        cityButton = findViewById(R.id.cityButton);
        idButton = findViewById(R.id.idButton);
        nameButton = findViewById(R.id.nameButton);

        et_dataInput = findViewById(R.id.et_dataInput);

        //Text displays, temporary
        tv_temperature = findViewById(R.id.tv_temperature);
        tv_feelsLike = findViewById(R.id.tv_feelsLike);
        tv_tempMin = findViewById(R.id.tv_tempMin);
        tv_tempMax = findViewById(R.id.tv_tempMax);
        tv_humidity = findViewById(R.id.tv_humidity);
        tv_cityName = findViewById(R.id.tv_cityName);
        tv_weatherCond = findViewById(R.id.tv_weatherCond);

        //Runtime permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }

        //call getLocation(), which takes the user's current location to figure out the closest city.

        getLocation();

        //****click listeners for each button, onClick handles button presses.

        //CITY FINDER BUTTON: TAKES IN CITY NAME AND SPITS OUT INFO ONTO TEXTVIEWS

        cityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Clicked cityButton", Toast.LENGTH_SHORT).show();

                city = et_dataInput.getText().toString();

                displayHomePageInfo(city);

            }
        });

        //CITY SAVER BUTTON: SAVES CITY INTO ARRAY TO BE PUT INTO LIST IN SAVED CITIES TAB

        idButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Clicked saveButton", Toast.LENGTH_SHORT).show();
            }
        });

        //SAVED CITIES TAB TOGGLER BUTTON: SWITCHES TO THE SAVED CITIES WINDOW WHEN PRESSED

        nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Switching...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,MainActivity.this);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override

    //called when the location is changed
    public void onLocationChanged(Location location) {
        Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getLocality();

            tv_cityName.setText(address);
            displayHomePageInfo(address);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Uses Volley to initiate a JSON object request for the openWeather api. Then, I parse this api for useful variables

    private void displayHomePageInfo(String cityName){
        url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //parse the API
                    JSONObject mainWeather = response.getJSONObject("main");
                    JSONArray descWeather = response.getJSONArray("weather");
                    JSONObject description = descWeather.getJSONObject(0);

                    String temperature = mainWeather.getString("temp");
                    String feelsLike = mainWeather.getString("feels_like");
                    String tempMin = mainWeather.getString("temp_min");
                    String tempMax = mainWeather.getString("temp_max");
                    String humidity = mainWeather.getString("humidity");
                    String condition = description.getString("description");

                    //convert all kelvin values to Fahrenheit

                    temperature = convertTemp(temperature);
                    feelsLike = convertTemp(feelsLike);
                    tempMin = convertTemp(tempMin);
                    tempMax = convertTemp(tempMax);

                    tv_weatherCond.setText(condition);
                    tv_temperature.setText(temperature + "\u2109");
                    tv_feelsLike.setText("Feels Like: "+ feelsLike + "\u2109");
                    tv_tempMin.setText("L:" + tempMin + "\u00B0");
                    tv_tempMax.setText("H:" + tempMax + "\u00B0");
                    tv_humidity.setText("Humidity: " + humidity + "%");


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);


    }

    @NonNull
    private String convertTemp(String value){

        double conversion = Double.parseDouble(value);

        conversion = (((conversion - 273.15) * 9) / 5) + 32;

        DecimalFormat oneDForm = new DecimalFormat("#.#");
        conversion = Double.valueOf(oneDForm.format(conversion));

        return Double.toString(conversion);
    }

}