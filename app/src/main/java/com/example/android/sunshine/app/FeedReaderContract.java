package com.example.android.sunshine.app;

import android.provider.BaseColumns;

/**
 * Created by Bojan on 10.10.2015.
 */
public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "meni";
        public static final String COLUMN_NAME_KATEGORIJA = "kategorija";
        public static final String COLUMN_NAME_ID_SLIKE = "idslike";
        public static final String COLUMN_NAME_URL_SLIKE = "urlslike";
        public static final String COLUMN_NAME_TEXT_SLIKE = "textslike";
        public static final String COLUMN_NAME_PONUDA = "ponuda";
        public static final String COLUMN_NAME_CIJENA = "cijena";
        public static final String COLUMN_NAME_OPIS = "opis";
        public static final String COLUMN_NAME_URL_DESNA_SLIKA = "urldesnaslika";
    }
}