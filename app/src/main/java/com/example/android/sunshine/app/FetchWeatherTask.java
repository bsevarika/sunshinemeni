package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Bojan on 11.10.2015.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

    public static String[] JSONforDbArray;
    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    /**
     * Take the String representing the complete ispis in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    public String[] getWeatherDataFromJson(String forecastJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String LISTA_ISPISVANJE = "ispisivanje";
        final String KATEGORIJA = "kategorija";
        final String ID_SLIKE = "idSlike";
        final String URL_SLIKE = "urlSlike";
        final String TEXT_SLIKE = "textSlike";
        final String LISTA = "lista";
        final String PONUDA = "ponuda";
        final String CIJENA = "cijena";
        final String OPIS = "opis";
        final String URL_DESNA_SLIKA = "urlDesnaSlika";


        JSONObject ispisivanjeJson = new JSONObject(forecastJsonStr);
        JSONArray ispisivanjeArray = ispisivanjeJson.getJSONArray(LISTA_ISPISVANJE);

        ArrayList<String> bazaList = new ArrayList<String>();
        for(int i = 0; i < ispisivanjeArray.length(); i++) {
            JSONObject ispisObject = ispisivanjeArray.getJSONObject(i);
            String kategorija = ispisObject.getString("kategorija");
            String idSlike = ispisObject.getString("idSlike");
            String urlSlike= ispisObject.getString("urlSlike");
            String textSlike = ispisObject.getString("textSlike");
            JSONArray listaArray = ispisObject.getJSONArray(LISTA);
            String[] ponudaArray = new String[listaArray.length()];
            String[] cijenaArray = new String[listaArray.length()];
            String[] opisArray = new String[listaArray.length()];
            String[] urlDesnaSlikaArray = new String[listaArray.length()];
            for(int j = 0; j < listaArray.length(); j++) {
                JSONObject listaObject = listaArray.getJSONObject(j);
                String ponuda = listaObject.getString("ponuda");
                ponudaArray[j] = ponuda;
                String cijena = listaObject.getString("cijena");
                cijenaArray[j] = cijena;
                String opis = listaObject.getString("opis");
                opisArray[j] = opis;
                String urlDesnaSlika = listaObject.getString("urlDesnaSlika");
                urlDesnaSlikaArray[j] = urlDesnaSlika;
                bazaList.add(kategorija);
                bazaList.add(idSlike);
                bazaList.add(urlSlike);
                bazaList.add(textSlike);
                bazaList.add(ponudaArray[j]);
                bazaList.add(cijenaArray[j]);
                bazaList.add(opisArray[j]);
                bazaList.add(urlDesnaSlikaArray[j]);
            }

        }
        //Niz elemenata polja ponuda u JSON-u
        JSONforDbArray = new String[bazaList.size()];
        JSONforDbArray = bazaList.toArray(JSONforDbArray);

        for (String s : JSONforDbArray) {
            Log.i(LOG_TAG, "JSONforDbArray: " + s);
        }
        return JSONforDbArray;
    }

    @Override
    protected String[] doInBackground(String... params) {
        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }


        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;
        String format = "json";
        String units = "metric";
        //  int numDays = ItemNumber;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("http://192.168.1.27/xampp/json.html");
            //URL url = new URL("http://iad.duckdns.org:5000/forecast");

                 /*
                    final String FORECAST_BASE_URL =
                            "http://api.openweathermap.org/data/2.5/forecast/daily?";
                    final String QUERY_PARAM = "q";
                    final String FORMAT_PARAM = "mode";
                    final String UNITS_PARAM = "units";
                    final String DAYS_PARAM = "cnt";
                            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                            .appendQueryParameter(QUERY_PARAM, params[0])
                            .appendQueryParameter(FORMAT_PARAM, format)
                            .appendQueryParameter(UNITS_PARAM, units)
                            .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                            .build();
                            URL url = new URL(builtUri.toString());
            */
            Log.i(LOG_TAG, "Built URI " + url.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
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
                return null;
            }
            forecastJsonStr = buffer.toString();
            Log.i(LOG_TAG, "forecastJsonStr " + forecastJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getWeatherDataFromJson(forecastJsonStr);
        }   catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }
  /*
    @Override
    protected void onPostExecute(String[] result) {
        if (result != null) {
            adapter.clear();
            for(String dayForecastStr : result) {
                adapter.add(dayForecastStr);
            }
            // New data is back from the server.  Hooray!
        }
    }
    */
}