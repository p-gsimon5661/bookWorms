package com.example.android.bookworms.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.bookworms.data.WormContract.WormsEntry;

public class WormProvider extends ContentProvider {
    public static final String LOG_TAG = WormProvider.class.getSimpleName();
    private static final int BOOK_WORMS = 200;
    private static final int BOOK_WORMS_ID = 201;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(WormContract.CONTENT_AUTHORITY, WormContract.PATH_BOOK_WORMS, BOOK_WORMS);
        sUriMatcher.addURI(WormContract.CONTENT_AUTHORITY, WormContract.PATH_BOOK_WORMS + "/#", BOOK_WORMS_ID);
    }

    /**
     * Database helper object
     */
    private WormDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new WormDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOK_WORMS:
                cursor = database.query(WormsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_WORMS_ID:
                selection = WormsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(WormsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOK_WORMS:
                return insertBookWorm(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBookWorm(Uri uri, ContentValues values) {
        Integer isbn = values.getAsInteger(WormsEntry.ISBN);
        if (isbn == null || isbn < 0) {
            throw new IllegalArgumentException("An ISBN is required");
        }

        String productName = values.getAsString(WormsEntry.COLUMN_PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("The name of the book is required");
        }

        Double price = values.getAsDouble(WormsEntry.COLUMN_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("The price of the book is required");
        }

        Integer quantity = values.getAsInteger(WormsEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("The quantity of the book is required");
        }

        String supplierName = values.getAsString(WormsEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Supplier name is required");
        }

        String supplierPhoneNum = values.getAsString(WormsEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierPhoneNum == null) {
            throw new IllegalArgumentException("Supplier phone number is required");
        }

        Integer format = values.getAsInteger(WormsEntry.COLUMN_FORMAT);
        if (format == null || !WormsEntry.isValidFormat(format)) {
            throw new IllegalArgumentException("A valid book format is required");
        }

        String genre = values.getAsString(WormsEntry.COLUMN_GENRE);
        if (genre == null) {
            throw new IllegalArgumentException("The book Genre is required");
        }

        Integer isFiction = values.getAsInteger(WormsEntry.COLUMN_ISFICTION);
        if (isFiction == null || !WormsEntry.isValidIsFiction(isFiction)) {
            throw new IllegalArgumentException("Whether the book is Fiction is required");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(WormsEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOK_WORMS:
                return updateBookWorm(uri, contentValues, selection, selectionArgs);
            case BOOK_WORMS_ID:
                selection = WormsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBookWorm(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBookWorm(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(WormsEntry.ISBN)) {
            Integer isbn = values.getAsInteger(WormsEntry.ISBN);
            if (isbn == null || isbn < 0) {
                throw new IllegalArgumentException("An ISBN is required");
            }
        }

        if (values.containsKey(WormsEntry.COLUMN_PRODUCT_NAME)) {
            String productName = values.getAsString(WormsEntry.COLUMN_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException("The name of the book is required");
            }
        }

        if (values.containsKey(WormsEntry.COLUMN_PRICE)) {
            Double price = values.getAsDouble(WormsEntry.COLUMN_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("The price of the book is required");
            }
        }

        if (values.containsKey(WormsEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(WormsEntry.COLUMN_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("The quantity of the book is required");
            }
        }

        if (values.containsKey(WormsEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(WormsEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Supplier name is required");
            }
        }

        if (values.containsKey(WormsEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNum = values.getAsString(WormsEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierPhoneNum == null) {
                throw new IllegalArgumentException("Supplier phone number is required");
            }
        }

        if (values.containsKey(WormsEntry.COLUMN_FORMAT)) {
            Integer format = values.getAsInteger(WormsEntry.COLUMN_FORMAT);
            if (format == null || !WormsEntry.isValidFormat(format)) {
                throw new IllegalArgumentException("A valid book format is required");
            }
        }

        if (values.containsKey(WormsEntry.COLUMN_GENRE)) {
            String genre = values.getAsString(WormsEntry.COLUMN_GENRE);
            if (genre == null) {
                throw new IllegalArgumentException("The book Genre is required");
            }
        }

        if (values.containsKey(WormsEntry.COLUMN_ISFICTION)) {
            Integer isFiction = values.getAsInteger(WormsEntry.COLUMN_ISFICTION);
            if (isFiction == null || !WormsEntry.isValidIsFiction(isFiction)) {
                throw new IllegalArgumentException("Whether the book is Fiction is required");
            }
        }

        // Check for update values
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Perform the update
        int rowsUpdated = database.update(WormsEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOK_WORMS:
                rowsDeleted = database.delete(WormsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_WORMS_ID:
                selection = WormsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(WormsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOK_WORMS:
                return WormsEntry.CONTENT_LIST_TYPE;
            case BOOK_WORMS_ID:
                return WormsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
