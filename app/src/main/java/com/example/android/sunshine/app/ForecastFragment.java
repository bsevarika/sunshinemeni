package com.example.android.sunshine.app;

import android.content.ContentValues;
import android.database.Cursor;
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

import java.util.ArrayList;

/**
 * Created by Bojan on 3.10.2015.
 */
public class ForecastFragment extends Fragment {

    public static String[] kategorijaArray;
    public static String[] idSlikeArray;
    public static String[] urlSLikeArray;
    public static String[] textArray;
    public static String[] ponudaArray;
    public static String[] cijenaArray;
    public static String[] opisArray;
    public static String[] urlDesnaSLikaArray;

    public ArrayAdapter<String> adapter;

    public ForecastFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //dbHelper = FeedReaderDbHelper.getInstance(getContext());
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    private void ReadFromJSON() {
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute("");
    }

    private void InsertIntoDb() {
        InsertDbTask DbTask = new InsertDbTask();
        DbTask.execute();
    }


    private void ReadFromDb() {
        ReadFromDbTask DbTask = new ReadFromDbTask();
        DbTask.execute();
    }

    private void DeleteFromDb() {
        DeleteFromDbTask DbTask = new DeleteFromDbTask();
        DbTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        ReadFromJSON();
        InsertIntoDb();
        ReadFromDb();
       // updateWeather();
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
            DeleteFromDb();

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

    //Asynktask za upisivanje JSON-a u bazu
    public class InsertDbTask extends AsyncTask<Void,Void,Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {
            FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContext());
            String[] data = FetchWeatherTask.JSONforDbArray;
            try {

                // Gets the data repository in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();

                for (int i = 0; i < data.length; i = i + 8) {

                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_KATEGORIJA, data[i]);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ID_SLIKE, data[i+1]);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_URL_SLIKE, data[i+2]);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TEXT_SLIKE, data[i+3]);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PONUDA, data[i+4]);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CIJENA, data[i+5]);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_OPIS, data[i+6]);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_URL_DESNA_SLIKA, data[i+7]);

                    // Insert the new row
                    db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
                }
                db.close();

                return true;
            } catch (Exception e) {

                return false;
            }
        }
    }

    //AsynkTask za citanje iz baze
    public class ReadFromDbTask extends AsyncTask<Void,Void,Boolean> {



        @Override
        protected Boolean doInBackground(Void... params) {
            FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContext());
            try {


                SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
                String[] projection = {
                        FeedReaderContract.FeedEntry.COLUMN_NAME_KATEGORIJA,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_ID_SLIKE,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_URL_SLIKE,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_TEXT_SLIKE,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_PONUDA,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_CIJENA,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_OPIS,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_URL_DESNA_SLIKA
                        };

// How you want the results sorted in the resulting Cursor
                String sortOrder =
                        FeedReaderContract.FeedEntry.COLUMN_NAME_PONUDA + " ASC";

                Cursor cursor = db.query(
                        FeedReaderContract.FeedEntry.TABLE_NAME,  // The table to query
                        projection,                               // The columns to return
                        null,                                // The columns for the WHERE clause
                        null,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        sortOrder                                 // The sort order
                );
                ArrayList<String> kategorijaList = new ArrayList<String>();
                ArrayList<String> idSlikeList = new ArrayList<String>();
                ArrayList<String> urlSLikeList = new ArrayList<String>();
                ArrayList<String> textList = new ArrayList<String>();
                ArrayList<String> ponudaList = new ArrayList<String>();
                ArrayList<String> cijenaList = new ArrayList<String>();
                ArrayList<String> opisList = new ArrayList<String>();
                ArrayList<String> urlDesnaSLikaList = new ArrayList<String>();
                if(cursor.moveToFirst()) {

                    int i = 0;
                    do {
                        kategorijaList.add(cursor.getString(i));
                        idSlikeList.add(cursor.getString(i + 1));
                        urlSLikeList.add(cursor.getString(i + 2));
                        textList.add(cursor.getString(i + 3));
                        ponudaList.add(cursor.getString(i + 4));
                        cijenaList.add(cursor.getString(i + 5));
                        opisList.add(cursor.getString(i + 6));
                        urlDesnaSLikaList.add(cursor.getString(i + 7));

                    }
                    while (cursor.moveToNext());

                }

                ponudaArray  = new String[ponudaList.size()];
                ponudaArray = ponudaList.toArray(ponudaArray);

                for (String s : ponudaArray) {
                    Log.i(ForecastFragment.class + "", "Ponuda: " + s);
                }

                for (String s : ponudaArray) {
                    adapter.add(s);

                }
                cursor.close();
                db.close();

                return true;
            } catch (Exception e) {

                return false;
            }
        }
    }

    //Asynktask za upisivanje JSON-a u bazu
    public class DeleteFromDbTask extends AsyncTask<Void,Void,Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {

            FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContext());
            try {

                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                db.delete(FeedReaderContract.FeedEntry.TABLE_NAME,null,null);
                db.close();

                return true;
            } catch (Exception e) {

                return false;
            }
        }
    }

}
