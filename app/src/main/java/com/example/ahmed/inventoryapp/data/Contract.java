package com.example.ahmed.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ahmed on 11/03/18.
 */

public final class Contract {
    private Contract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI =  Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT = "products";

    public static final class InventoryEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);

        public final static String TABLE_NAME = "products";
        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_Product_NAME = "product_name";
        public final static String COLUMN_Product_Price = "price";
        public final static String COLUMN_Product_Quantity = "quantity";
        public static final String COLUMN_IMAGE = "image";
        public final static String COLUMN_SUPPLIER_Name = "supplier_name";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public final static String COLUMN_SUPPLIER_Phone = "supplier_phone";
    }
}
