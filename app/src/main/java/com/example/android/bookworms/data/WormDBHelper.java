package com.example.android.bookworms.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookworms.data.WormContract.WormsEntry;

public class WormDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = WormDBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "bookworm.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link WormDBHelper}.
     *
     * @param context of the app
     */
    public WormDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creates the books table
        String SQL_CREATE_BOOKWORM_TABLE = "CREATE TABLE " + WormsEntry.TABLE_NAME + " ("
                + WormsEntry.ISBN + " INTEGER PRIMARY KEY, "
                + WormsEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + WormsEntry.COLUMN_PRICE + " DOUBLE NOT NULL, "
                + WormsEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + WormsEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + WormsEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL, "
                + WormsEntry.COLUMN_FORMAT + " INTEGER NOT NULL, "
                + WormsEntry.COLUMN_GENRE + " TEXT NOT NULL, "
                + WormsEntry.COLUMN_ISFICTION + " BOOLEAN NOT NULL );";

        // Execute the create statement
        db.execSQL(SQL_CREATE_BOOKWORM_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
