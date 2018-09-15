package com.example.android.bookworms.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

public class WormContract {
    private WormContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.bookworms";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOK_WORMS = "books";

    public static final class WormsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOK_WORMS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOK_WORMS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOK_WORMS;

        public final static String TABLE_NAME = "books";
        public final static String ISBN = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "productName";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "supplierName";
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplierPhoneNumber";
        public final static String COLUMN_FORMAT = "format";
        public final static String COLUMN_GENRE = "genre";
        public final static String COLUMN_ISFICTION = "isFiction";

        public static final int FROMAT_UNKNOWN = 0;
        public static final int FORMAT_EBOOK = 1;
        public static final int FORMAT_PRINT = 2;
        public static final int FORMAT_AUDIO = 3;

        public static final int ISFICTION_FALSE = 0;
        public static final int ISFICTION_TRUE = 1;

        public static boolean isValidFormat(int format) {
            return (format == FROMAT_UNKNOWN || format == FORMAT_EBOOK || format == FORMAT_PRINT ||
                    format == FORMAT_AUDIO);
        }

        public static boolean isValidIsFiction(int isFiction) {
            return (isFiction == ISFICTION_FALSE || isFiction == ISFICTION_TRUE);
        }

        public static String getFormatStringEqual(int format) {
            String FormatStringEqual;

            switch (format) {
                case FORMAT_EBOOK:
                    FormatStringEqual = "eBook";
                    break;
                case FORMAT_PRINT:
                    FormatStringEqual = "Print";
                    break;
                case FORMAT_AUDIO:
                    FormatStringEqual = "Audio";
                    break;
                case FROMAT_UNKNOWN:
                default:
                    FormatStringEqual = "UnKnown";
                    break;
            }
            return FormatStringEqual;
        }
    }
}
