package com.example.ahmed.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ahmed.inventoryapp.data.Contract.InventoryEntry;

/**
 * Created by ahmed on 20/03/18.
 */

public class InventorProvider extends ContentProvider {
    ContractHelper inventorDbHelper;
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_PRODUCT, PRODUCTS);
        uriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_PRODUCT + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        inventorDbHelper = new ContractHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = inventorDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        int match = uriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }


        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);

            default:
                throw new IllegalArgumentException("Cannot query unknown query " + uri);
        }
    }

    public Uri insertProduct(Uri uri, ContentValues contentValues) {
        String name = contentValues.getAsString(InventoryEntry.COLUMN_Product_NAME);
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("Product requires a name");

        Integer quantity = contentValues.getAsInteger(InventoryEntry.COLUMN_Product_Quantity);

        if (quantity == null || quantity < 1)
            throw new IllegalArgumentException("cant add product with quantity less than or equal to Zero");

        Integer price = contentValues.getAsInteger(InventoryEntry.COLUMN_Product_Price);
        if (price == null || price < 1)
            throw new IllegalArgumentException("Price cannot be less than or equal to Zero");

        String supplier = contentValues.getAsString(InventoryEntry.COLUMN_SUPPLIER_Name);
        if (supplier == null || supplier.length() == 0)
            throw new IllegalArgumentException("Supplier requires name");

        String supplierMail = contentValues.getAsString(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
        if (supplierMail == null || supplierMail.length() == 0)
            throw new IllegalArgumentException("Supplier requires Email");

        String supplierPhone = contentValues.getAsString(InventoryEntry.COLUMN_SUPPLIER_Phone);
        if (supplierPhone == null || supplierPhone.length() == 0)
            throw new IllegalArgumentException("Supplier requires Phone Number");

        SQLiteDatabase db = inventorDbHelper.getWritableDatabase();
        long result = db.insert(InventoryEntry.TABLE_NAME, null, contentValues);
        if (result == -1) {
            Log.e(getContext().toString(), "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, result);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = inventorDbHelper.getWritableDatabase();
        int rowDelet;
        int match = uriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                rowDelet = db.delete(InventoryEntry.TABLE_NAME, s, strings);
                break;
            case PRODUCT_ID:
                s = InventoryEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDelet = db.delete(InventoryEntry.TABLE_NAME, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowDelet != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDelet;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int match = uriMatcher.match(uri);
        int result;
        switch (match) {
            case PRODUCTS:
                result = updateproduct(uri, contentValues, s, strings);
                break;
            case PRODUCT_ID:
                s = InventoryEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                result = updateproduct(uri, contentValues, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown query " + uri);

        }
        return result;
    }

    public int updateproduct(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        if (contentValues.containsKey(InventoryEntry.COLUMN_Product_NAME)) {
            String name = contentValues.getAsString(InventoryEntry.COLUMN_Product_NAME);
            if (name == null || name.length() == 0)
                throw new IllegalArgumentException("Product requires a name");
        }

        if (contentValues.containsKey(InventoryEntry.COLUMN_Product_Quantity)) {
            Integer quantity = contentValues.getAsInteger(InventoryEntry.COLUMN_Product_Quantity);
            if (quantity == null || quantity < 0)
                throw new IllegalArgumentException("cant update product with quantity less than or equal to Zero");
        }

        if (contentValues.containsKey(InventoryEntry.COLUMN_Product_Price)) {
            Integer price = contentValues.getAsInteger(InventoryEntry.COLUMN_Product_Price);
            if (price == null || price < 1)
                throw new IllegalArgumentException("Price cannot be less than or equal to Zero");
        }

        if (contentValues.containsKey(InventoryEntry.COLUMN_SUPPLIER_Name)) {
            String supplier = contentValues.getAsString(InventoryEntry.COLUMN_SUPPLIER_Name);
            if (supplier == null || supplier.length() == 0)
                throw new IllegalArgumentException("Supplier requires name");
        }

        if (contentValues.containsKey(InventoryEntry.COLUMN_SUPPLIER_EMAIL)) {
            String supplierMail = contentValues.getAsString(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
            if (supplierMail == null || supplierMail.length() == 0)
                throw new IllegalArgumentException("Supplier requires Email");
        }

        if (contentValues.containsKey(InventoryEntry.COLUMN_SUPPLIER_Phone)) {
            String supplierPhone = contentValues.getAsString(InventoryEntry.COLUMN_SUPPLIER_Phone);
            if (supplierPhone == null || supplierPhone.length() == 0)
                throw new IllegalArgumentException("Supplier requires Phone Number");
        }

        if (contentValues.size() == 0)
            return 0;

        SQLiteDatabase sqLiteDatabase = inventorDbHelper.getWritableDatabase();
        int rowsUpdated = sqLiteDatabase.update(InventoryEntry.TABLE_NAME, contentValues, s, strings);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}

