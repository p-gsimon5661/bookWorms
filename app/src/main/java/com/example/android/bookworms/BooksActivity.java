package com.example.android.bookworms;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Random;

import com.example.android.bookworms.data.WormContract.WormsEntry;

public class BooksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BOOK_WORM_LOADER = 0;

    //Adapter for the ListView
    BooksCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        // Setup FAB
        FloatingActionButton fab = findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BooksActivity.this, EditBooksActivity.class);
                startActivity(intent);
            }
        });

        ListView bookWormsListView = findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        bookWormsListView.setEmptyView(emptyView);

        // adapter and cursor linkedup
        mCursorAdapter = new BooksCursorAdapter(this, null);
        bookWormsListView.setAdapter(mCursorAdapter);

        //item click listener
        bookWormsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(BooksActivity.this, EditBooksActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(WormsEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });

        //The loader
        getLoaderManager().initLoader(BOOK_WORM_LOADER, null, this);
    }

    private void insertDummyData() {
        ContentValues values = new ContentValues();
        values.put(WormsEntry.ISBN, fakeISBNGenerator());
        values.put(WormsEntry.COLUMN_PRODUCT_NAME, getString(R.string.dummy_data_book_name));
        values.put(WormsEntry.COLUMN_PRICE, Double.parseDouble(getString(R.string.dummy_data_price)));
        values.put(WormsEntry.COLUMN_QUANTITY, Integer.parseInt(getString(R.string.dummy_data_quantity)));
        values.put(WormsEntry.COLUMN_SUPPLIER_NAME, getString(R.string.dummy_data_supplier));
        values.put(WormsEntry.COLUMN_SUPPLIER_PHONE_NUMBER, getString(R.string.dummy_data_supplier_num));
        values.put(WormsEntry.COLUMN_FORMAT, Integer.parseInt(getString(R.string.dummy_data_format)));
        values.put(WormsEntry.COLUMN_GENRE, getString(R.string.dummy_data_genre));
        values.put(WormsEntry.COLUMN_ISFICTION, Integer.parseInt(getString(R.string.dummy_data_isFiction)));

        //insert a new row for the Holy Bible in DB
        Uri newUri = getContentResolver().insert(WormsEntry.CONTENT_URI, values);
        Log.v("BooksActivity", newUri + " -- logging the uri.");
    }

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(WormsEntry.CONTENT_URI, null, null);
        Log.v("BooksActivity", rowsDeleted + " rows deleted from books database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_manage_books.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_manage_books, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            case R.id.action_empty_books_table:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                WormsEntry._ID,
                WormsEntry.COLUMN_PRODUCT_NAME,
                WormsEntry.COLUMN_PRICE,
                WormsEntry.COLUMN_QUANTITY,
                WormsEntry.COLUMN_FORMAT,
                WormsEntry.COLUMN_GENRE};

        return new CursorLoader(this,
                WormsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    public int fakeISBNGenerator() {
        Random rand = new Random();
        return (rand.nextInt(1000000) + 1);
    }


    public void RegisterSale(int cId, int quantity) {
        if (quantity > 0) {
            quantity--;

            ContentValues values = new ContentValues();
            values.put(WormsEntry.COLUMN_QUANTITY, quantity);

            Uri updateUri = ContentUris.withAppendedId(WormsEntry.CONTENT_URI, cId);

            getContentResolver().update(updateUri, values, null, null);

        } else
            Toast.makeText(this, getString(R.string.zero_quantity), Toast.LENGTH_SHORT).show();
    }
}
