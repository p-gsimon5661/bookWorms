package com.example.android.bookworms;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookworms.data.WormContract.WormsEntry;
import com.example.android.bookworms.data.WormDBHelper;

public class BooksCursorAdapter extends CursorAdapter {
    Context l_context;

    public BooksCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        l_context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.books_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView bookPriceTextView = view.findViewById(R.id.price);
        TextView bookNameTextView = view.findViewById(R.id.bookName);
        TextView bookQuantityTextView = view.findViewById(R.id.quantity);
        TextView bookFormatTextView = view.findViewById(R.id.format);
        TextView bookGenreTextView = view.findViewById(R.id.genre);

        int bookNameColumnIndex = cursor.getColumnIndex(WormsEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(WormsEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(WormsEntry.COLUMN_QUANTITY);
        int formatColumnIndex = cursor.getColumnIndex(WormsEntry.COLUMN_FORMAT);
        int genreColumnIndex = cursor.getColumnIndex(WormsEntry.COLUMN_GENRE);

        String bookName = cursor.getString(bookNameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);
        String bookFormat = cursor.getString(formatColumnIndex);
        String bookGenre = cursor.getString(genreColumnIndex);

        bookPriceTextView.setText(context.getString(R.string.book_price_lbl, Float.parseFloat(bookPrice)));
        bookNameTextView.setText(context.getString(R.string.book_name_lbl, bookName));
        bookQuantityTextView.setText(context.getString(R.string.book_quantity_lbl, Integer.parseInt(bookQuantity)));
        bookFormatTextView.setText(context.getString(R.string.book_format_lbl, WormsEntry.getFormatStringEqual(Integer.parseInt(bookFormat))));
        bookGenreTextView.setText(context.getString(R.string.book_genre_lbl, bookGenre));

        Button saleButton = view.findViewById(R.id.sale_button);
        int columnIdIndex = cursor.getColumnIndex(WormsEntry._ID);
        saleButton.setOnClickListener(new OnItemClickListener(cursor.getInt(columnIdIndex)));
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int columnIndex;

        public OnItemClickListener(int position) {
            super();
            columnIndex = position;
        }

        @Override
        public void onClick(View view) {
            SQLiteOpenHelper wormDBHelper = new WormDBHelper(l_context);
            SQLiteDatabase readableDatabase = wormDBHelper.getReadableDatabase();

            Cursor cursor = readableDatabase.rawQuery("select " +
                    WormsEntry.COLUMN_QUANTITY + " from " +
                    WormsEntry.TABLE_NAME + " where " +
                    WormsEntry.ISBN + " = " + columnIndex + "", null);

            if (cursor != null && cursor.moveToFirst()) {
                String quantity = cursor.getString(cursor.getColumnIndex(WormsEntry.COLUMN_QUANTITY));
                cursor.close();

                if (l_context instanceof BooksActivity) {
                    ((BooksActivity) l_context).RegisterSale(columnIndex, Integer.valueOf(quantity));
                }

            }
            readableDatabase.close();
        }
    }
}
