package com.example.dada.androidstudio1;



import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/* A placeholder fragment containing a simple view.
        */
public class ForecastFragment extends Fragment
{

    String JSONString;

    public ForecastFragment() {
    }

    public final String TAG = "Application1";
    protected ArrayAdapter<String> aa;


    private String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        SharedPreferences unit_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unit_type = unit_prefs.getString(getString(R.string.prefs_units_key),
                getString(R.string.prefs_units_default_value));
        if (unit_type.equals(getString(R.string.prefs_units_imperial))) {
            high = (high * 1.8) + 32;
            low = (low * 1.8) + 32;
        }
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_CITY = "city";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DATETIME = "dt";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        JSONObject cityObject = forecastJson.getJSONObject(OWM_CITY);
        String city_Name = cityObject.getString("name");
        String[] resultStrs = new String[numDays + 1];
        resultStrs[0] = "City- " + city_Name;
        for (int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);
            //  JSONObject descObject= descArray.getJSONObject(i);
            //JSONObject weatherObject=weatherArray.getJSONObject(OWM_TEMPERATURE);
            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime = dayForecast.getLong(OWM_DATETIME);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            //  JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);


            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            JSONArray weatherDesc = dayForecast.getJSONArray(OWM_WEATHER);
            //JSONObject tempDesc= weatherDesc.getJSONObject(1);
            JSONObject weatherObj = weatherDesc.getJSONObject(0);
            description = weatherObj.getString("main");
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);


            highAndLow = formatHighLows(high, low);
            resultStrs[i + 1] = day + " - " + description + " - " + highAndLow;
        }

        return resultStrs;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        String[] StringArray = {};


        List<String> list = new ArrayList<String>(Arrays.asList(StringArray));


        aa = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, list);

        ListView lv = (ListView) rootView.findViewById(R.id.listview_forecast);
        lv.setAdapter(aa);
        setHasOptionsMenu(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String ToastString = aa.getItem(i);
                Toast.makeText(getActivity(), ToastString, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), Detail_Activity.class)
                        .putExtra(Intent.EXTRA_TEXT, ToastString);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                updateWeather();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    public void updateWeather() {
        ForecastTask WeatherTask = new ForecastTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.prefs_location_key),
                getString(R.string.prefs_location_default_value));
        WeatherTask.execute(location);
    }

    public class ForecastTask extends AsyncTask<String, Void, String[]> {

        Uri builtUri;

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }


            String format = "json";
            int days_count = 7;
            String Units = "metric";
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // http://openweathermap.org/API#forecast
                String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                String QUERY_PARAM = "q";
                String FORMAT_PARAM = "mode";
                String UNITS_PARAM = "units";
                String DAYS_PARAM = "cnt";

                builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, Units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(days_count))
                        .build();
                URL url = new URL(builtUri.toString());

                Log.v(TAG, builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                JSONString = buffer.toString();
                Log.v(TAG, "The received JSON string is: " + JSONString);
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return (getWeatherDataFromJson(forecastJsonStr, days_count));
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            if (result != null) {
                aa.clear();
            }
            for (String weatherString : result) {
                aa.add(weatherString);
            }
            Log.v(TAG, builtUri.toString());
        }
    }
}
