package com.example.android.sunshine.app;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Bojan on 3.10.2015.
 */
public class ForecastFragment extends Fragment {

    public ArrayAdapter<String> adapter;
    //private FeedReaderDbHelper dbHelper;
    //Database helper instance
   private static ForecastFragment _instance;

    public ForecastFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //dbHelper = FeedReaderDbHelper.getInstance(getContext());
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute("");
    }

    private void InsertIntoDb() {
        InsertDbTask DbTask = new InsertDbTask();
        DbTask.execute();
    }




    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
        //Poziva metodu koja upisuje sadrzaj u bazu nakon pritiska na refresh dugme
            InsertIntoDb();
            Log.v(ForecastFragment.class + "", "Refresh button pressed");
          //  FetchWeatherTask weatherTask = new FetchWeatherTask();
          //  weatherTask.execute("");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview);
        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);
        return rootView;
    }

    public class InsertDbTask extends AsyncTask<Void,Void,Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                String[] data = FetchWeatherTask.bazaArray;

                FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContext());
                // Gets the data repository in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();

                values.put("entryid", 2);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, data[5]);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, "BanjaLuka");

                // Insert the new row, returning the primary key value of the new row
                long newRowId;
                newRowId = db.insert(
                        FeedReaderContract.FeedEntry.TABLE_NAME,
                        null,
                        values);
                db.close();

                return true;
            } catch (Exception e) {

                return false;
            }
        }
    }



}
