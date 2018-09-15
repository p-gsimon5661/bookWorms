package com.example.android.bookworms;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.example.android.bookworms.data.WormContract.WormsEntry;

public class EditBooksActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_BOOK_WORM_LOADER = 0;

    private EditText e_isbn;
    private EditText e_productName;
    private EditText e_price;
    private TextView t_quantity;
    private EditText e_supplierName;
    private EditText e_supplierPhoneNumber;
    private int e_bookFormat = WormsEntry.FROMAT_UNKNOWN;
    private EditText e_bookGenre;
    private RadioGroup e_isFictionRadio;
    private RadioButton noRadioButton;
    private RadioButton yesRadioButton;
    private Spinner formatSpinner;
    private Button orderButton;
    Button minusButton;
    Button plusButton;
    private boolean hasBookWormChanged = false;
    private Uri currentBookUri;

    //listener for user touching the view
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            hasBookWormChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_books);

        Intent intent = getIntent();
        currentBookUri = intent.getData();
        orderButton = findViewById(R.id.order_button);
        minusButton = findViewById(R.id.minus_button);
        plusButton = findViewById(R.id.plus_button);

        e_isbn = findViewById(R.id.edit_isbn);
        e_productName = findViewById(R.id.edit_book_name);
        e_price = findViewById(R.id.edit_book_price);
        t_quantity = findViewById(R.id.edit_book_quantity);
        e_supplierName = findViewById(R.id.edit_book_supplier);
        e_supplierPhoneNumber = findViewById(R.id.edit_book_supplier_num);
        e_bookGenre = findViewById(R.id.edit_book_genre);
        formatSpinner = findViewById(R.id.spinner_book_format);
        e_isFictionRadio = findViewById(R.id.isfiction_radio_button);
        noRadioButton = findViewById(R.id.no_radio_button);
        yesRadioButton = findViewById(R.id.yes_radio_button);

        if (currentBookUri == null) {
            // adding a book
            setTitle(getString(R.string.add_book_activity_title));
            invalidateOptionsMenu();
            orderButton.setVisibility(View.GONE);
            t_quantity.setText("0");
        } else {
            // editing a book
            setTitle(getString(R.string.edit_book_activity_title));
            getLoaderManager().initLoader(EXISTING_BOOK_WORM_LOADER, null, this);
        }

        e_isbn.setOnTouchListener(mTouchListener);
        e_productName.setOnTouchListener(mTouchListener);
        e_price.setOnTouchListener(mTouchListener);
        t_quantity.setOnTouchListener(mTouchListener);
        e_supplierName.setOnTouchListener(mTouchListener);
        e_supplierPhoneNumber.setOnTouchListener(mTouchListener);
        e_bookGenre.setOnTouchListener(mTouchListener);
        formatSpinner.setOnTouchListener(mTouchListener);
        e_isFictionRadio.setOnTouchListener(mTouchListener);
        minusButton.setOnTouchListener(mTouchListener);
        plusButton.setOnTouchListener(mTouchListener);

        bookFormatSpinner();

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseQuantity(Integer.parseInt(t_quantity.getText().toString()));
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseQuantity(Integer.parseInt(t_quantity.getText().toString()));
            }
        });
    }

    private void bookFormatSpinner() {
        ArrayAdapter bookFormatSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_book_format_options, android.R.layout.simple_spinner_item);

        bookFormatSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        formatSpinner.setAdapter(bookFormatSpinnerAdapter);

        formatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);

                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.book_format_audio)))
                        e_bookFormat = WormsEntry.FORMAT_AUDIO;
                    else if (selection.equals(getString(R.string.book_format_print)))
                        e_bookFormat = WormsEntry.FORMAT_PRINT;
                    else if (selection.equals(getString(R.string.book_format_ebook)))
                        e_bookFormat = WormsEntry.FORMAT_EBOOK;
                    else
                        e_bookFormat = WormsEntry.FROMAT_UNKNOWN;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                e_bookFormat = WormsEntry.FROMAT_UNKNOWN;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_books, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                boolean wasASuccess = insertBook();
                if (wasASuccess)
                    finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!hasBookWormChanged) {
                    NavUtils.navigateUpFromSameTask(EditBooksActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditBooksActivity.this);
                            }
                        };

                unsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean insertBook() {
        String isbnValue = e_isbn.getText().toString().trim();
        String productNameValue = e_productName.getText().toString().trim();
        String priceValue = e_price.getText().toString().trim();
        String quantityValue = t_quantity.getText().toString().trim();
        String supplierNameValue = e_supplierName.getText().toString().trim();
        String supplierPhoneNumberValue = e_supplierPhoneNumber.getText().toString().trim();
        String bookGenreValue = e_bookGenre.getText().toString().trim();
        int isFictionValue = e_isFictionRadio.indexOfChild(findViewById(e_isFictionRadio.getCheckedRadioButtonId()));

        if (currentBookUri == null && TextUtils.isEmpty(isbnValue) &&
                TextUtils.isEmpty(productNameValue) && TextUtils.isEmpty(priceValue) &&
                TextUtils.isEmpty(quantityValue) && TextUtils.isEmpty(supplierNameValue) &&
                TextUtils.isEmpty(supplierPhoneNumberValue) &&
                e_bookFormat == WormsEntry.FROMAT_UNKNOWN && isFictionValue < 0) {
            Toast.makeText(this, getString(R.string.book_info), Toast.LENGTH_SHORT).show();
            return false;
        }

        ContentValues values = new ContentValues();
        if (TextUtils.isEmpty(isbnValue)) {
            Toast.makeText(this, getString(R.string.book_isbn_info), Toast.LENGTH_SHORT).show();
            return false;
        } else
            values.put(WormsEntry.ISBN, isbnValue);

        if (TextUtils.isEmpty(productNameValue)) {
            Toast.makeText(this, getString(R.string.book_name_info), Toast.LENGTH_SHORT).show();
            return false;
        } else
            values.put(WormsEntry.COLUMN_PRODUCT_NAME, productNameValue);

        if (TextUtils.isEmpty(priceValue)) {
            Toast.makeText(this, getString(R.string.book_price_info), Toast.LENGTH_SHORT).show();
            return false;
        } else
            values.put(WormsEntry.COLUMN_PRICE, Double.parseDouble(priceValue));

        if (TextUtils.isEmpty(quantityValue)) {
            Toast.makeText(this, getString(R.string.book_quantity_info), Toast.LENGTH_SHORT).show();
            return false;
        } else
            values.put(WormsEntry.COLUMN_QUANTITY, Integer.parseInt(quantityValue));

        if (TextUtils.isEmpty(supplierNameValue)) {
            Toast.makeText(this, getString(R.string.book_supplierName_info), Toast.LENGTH_SHORT).show();
            return false;
        } else
            values.put(WormsEntry.COLUMN_SUPPLIER_NAME, supplierNameValue);

        if (TextUtils.isEmpty(supplierPhoneNumberValue) || supplierPhoneNumberValue.length() < 10) {
            Toast.makeText(this, getString(R.string.book_supplierPhone_info), Toast.LENGTH_SHORT).show();
            return false;
        } else
            values.put(WormsEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberValue);

        if (e_bookFormat == WormsEntry.FROMAT_UNKNOWN) {
            Toast.makeText(this, getString(R.string.book_format_info), Toast.LENGTH_SHORT).show();
            return false;
        } else
            values.put(WormsEntry.COLUMN_FORMAT, e_bookFormat);

        if (TextUtils.isEmpty(bookGenreValue)) {
            Toast.makeText(this, getString(R.string.book_genre_info), Toast.LENGTH_SHORT).show();
            return false;
        } else
            values.put(WormsEntry.COLUMN_GENRE, bookGenreValue);

        if (noRadioButton.isChecked())
            values.put(WormsEntry.COLUMN_ISFICTION, WormsEntry.ISFICTION_FALSE);
        else if (yesRadioButton.isChecked())
            values.put(WormsEntry.COLUMN_ISFICTION, WormsEntry.ISFICTION_TRUE);
        else {
            Toast.makeText(this, getString(R.string.book_isFiction_info), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (currentBookUri == null) {
            Uri newUri = getContentResolver().insert(WormsEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.error_inserting_book), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.success_inserting_book), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.error_updating_book),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.success_updating_book),
                        Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!hasBookWormChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        unsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                WormsEntry._ID,
                WormsEntry.COLUMN_PRODUCT_NAME,
                WormsEntry.COLUMN_PRICE,
                WormsEntry.COLUMN_QUANTITY,
                WormsEntry.COLUMN_SUPPLIER_NAME,
                WormsEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
                WormsEntry.COLUMN_FORMAT,
                WormsEntry.COLUMN_GENRE,
                WormsEntry.COLUMN_ISFICTION};

        return new CursorLoader(this,
                currentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // check for null cursor or no row returned
        if (cursor == null || cursor.getCount() < 1)
            return;

        // reading from the cursor
        if (cursor.moveToFirst()) {
            int isbnColumnIndex = cursor.getColumnIndex(WormsEntry.ISBN);
            int bookNameColumnIndex = cursor.getColumnIndex(WormsEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(WormsEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(WormsEntry.COLUMN_QUANTITY);
            int formatColumnIndex = cursor.getColumnIndex(WormsEntry.COLUMN_FORMAT);
            int genreColumnIndex = cursor.getColumnIndex(WormsEntry.COLUMN_GENRE);
            int supplierNameColumnIndex = cursor.getColumnIndex(WormsEntry.COLUMN_SUPPLIER_NAME);
            int supplierPnoneColumnIndex = cursor.getColumnIndex(WormsEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            int isFictiontColumnIndex = cursor.getColumnIndex(WormsEntry.COLUMN_ISFICTION);

            // Extract out the value from the Cursor for the given column index
            int isbn = cursor.getInt(isbnColumnIndex);
            String bookName = cursor.getString(bookNameColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int format = cursor.getInt(formatColumnIndex);
            String genre = cursor.getString(genreColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            final String supplierphone = cursor.getString(supplierPnoneColumnIndex);
            int isFiction = cursor.getInt(isFictiontColumnIndex);

            // Update the views on the screen with the values from the database
            e_isbn.setText(String.format(Integer.toString(isbn), 0));
            e_productName.setText(bookName);
            e_price.setText(String.format(Double.toString(price), 2));
            t_quantity.setText(String.format(Integer.toString(quantity), 0));
            e_supplierName.setText(supplierName);
            e_supplierPhoneNumber.setText(supplierphone);
            e_bookGenre.setText(genre);

            // format is a dropdown spinner
            switch (format) {
                case WormsEntry.FORMAT_EBOOK:
                    formatSpinner.setSelection(1);
                    break;
                case WormsEntry.FORMAT_PRINT:
                    formatSpinner.setSelection(2);
                    break;
                case WormsEntry.FORMAT_AUDIO:
                    formatSpinner.setSelection(3);
                    break;
                default:
                    formatSpinner.setSelection(0);
                    break;
            }

            if (isFiction == 0)
                noRadioButton.setChecked(true);
            else if (isFiction == 1)
                yesRadioButton.setChecked(true);

            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + supplierphone));
                    startActivity(callIntent);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // clear out all the data
        e_isbn.setText("");
        e_productName.setText("");
        e_price.setText("");
        t_quantity.setText("");
        e_supplierName.setText("");
        e_supplierPhoneNumber.setText("");
        e_bookGenre.setText("");
        formatSpinner.setSelection(0);
        e_isFictionRadio.clearCheck();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void unsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete_book, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        if (currentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    public void decreaseQuantity(int quantity) {
        if (quantity > 0)
            quantity--;
        else {
            Toast toastLowerBound = Toast.makeText(getApplicationContext(), getString(R.string.zero_quantity), Toast.LENGTH_SHORT);
            toastLowerBound.show();
        }
        t_quantity.setText(String.format(Integer.toString(quantity), 0));
    }

    public void increaseQuantity(int quantity) {
        quantity++;
        t_quantity.setText(String.format(Integer.toString(quantity), 0));
    }
}