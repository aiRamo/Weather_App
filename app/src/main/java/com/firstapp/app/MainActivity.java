/*TODO:
 *Create new FindCity button that will prompt user with a pop up text field.
 *Create a separate page to display search results
 *Create a separate page for saved cities to quickly select

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements LocationListener {

    Button cityButton, idButton, nameButton;
    EditText et_dataInput;

    TextView tv_temperature, tv_feelsLike, tv_tempMin, tv_tempMax, tv_humidity, tv_cityName, tv_weatherCond;
    TextView tv_forecastWeekday, tv_forecastTemperature, tv_forecastCondition;


    List<Forecast> forecastDataList;
    RecyclerView recyclerview;

    String apiKey = "f5c49ede6b0561d6a9e4ba8da29d09de";
    String url;

    public static String city;

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        recyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerview.setHasFixedSize(true);
        LinearLayoutManager HorizontalLayout =
                new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerview.setLayoutManager(HorizontalLayout);

        forecastDataList = new ArrayList<>();

        //assign values to each control in the layout page

        cityButton = findViewById(R.id.cityButton);
        idButton = findViewById(R.id.idButton);
        nameButton = findViewById(R.id.nameButton);

        et_dataInput = findViewById(R.id.et_dataInput);

        //Text displays for current time weather data
        tv_temperature = findViewById(R.id.tv_temperature);
        tv_feelsLike = findViewById(R.id.tv_feelsLike);
        tv_tempMin = findViewById(R.id.tv_tempMin);
        tv_tempMax = findViewById(R.id.tv_tempMax);
        tv_humidity = findViewById(R.id.tv_humidity);
        tv_cityName = findViewById(R.id.tv_cityName);
        tv_weatherCond = findViewById(R.id.tv_weatherCond);

        //text displays and card view for forecast

        tv_forecastTemperature = findViewById(R.id.tv_forecastTemperature);
        tv_forecastCondition = findViewById(R.id.tv_forecastCondition);
        tv_forecastWeekday = findViewById(R.id.tv_forecastWeekday);

        //cv_weatherForecast = findViewById(R.id.cv_weatherForecast);


        //Runtime permissions for locationManager
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
                displayForecast(city);

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


            displayHomePageInfo(address);

            displayForecast(address);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Uses Volley to initiate a JSON object request for the openWeather api. Then, I parse this api for useful variables

    private void displayHomePageInfo(String cityName){
        url = "https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&appid="+apiKey;

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

                    tv_cityName.setText(cityName);
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

    public void displayForecast(String cityName){
        forecastDataList.clear();
        url = "https://api.openweathermap.org/data/2.5/forecast?q="+cityName+"&appid="+apiKey;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //parse the API
                    for (int i = 0; i < 5; i++) {
                        JSONArray forecastList = response.getJSONArray("list");

                        //for the temperature value:
                        JSONObject forecastSlot = forecastList.getJSONObject(i * 8);
                        JSONObject forecastNumbers = forecastSlot.getJSONObject("main");

                        //for the condition phrase:
                        JSONArray forecastConditionList = forecastSlot.getJSONArray("weather");
                        JSONObject forecastCondition = forecastConditionList.getJSONObject(0);

                        String forecastTemp = forecastNumbers.getString("temp");
                        String forecastCond = forecastCondition.getString("main");

                        //Getting weekday info...
                        ArrayList<String> Weekdays = new ArrayList<>(Arrays.asList(
                                "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"));

                        Calendar c = Calendar.getInstance();
                        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                        //convert kelvin value to Fahrenheit
                        forecastTemp = convertTemp(forecastTemp);

                        returnData(forecastTemp, forecastCond, Weekdays.get((dayOfWeek - 1 + i) % 7));
                    }

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

    private void returnData(String temp, String cond, String day){


        forecastDataList.add(new Forecast(
                temp + "\u00B0",
                cond,
                day
        ));


        ForecastAdapter forecastAdapter = new ForecastAdapter(this, forecastDataList);
        recyclerview.setAdapter(forecastAdapter);

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