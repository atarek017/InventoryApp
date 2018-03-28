package com.example.ahmed.inventoryapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ahmed.inventoryapp.data.Contract.InventoryEntry;

public class EditorActivity extends AppCompatActivity {

    private Uri productUri = null;
    private EditText productName;
    private EditText productQuantity;
    private EditText productPrice;
    private EditText supplierName;
    private EditText supplierMail;
    private EditText supplierPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        productName = findViewById(R.id.edt_product_name);
        productQuantity = findViewById(R.id.edt_product_quantity);
        productPrice = findViewById(R.id.edt_product_price);
        supplierName = findViewById(R.id.edt_supplier_name);
        supplierMail = findViewById(R.id.edt_supplier_mail);
        supplierPhone = findViewById(R.id.edt_supplier_phone);

        try {
            if (getIntent().getData() != null) {
                productUri = getIntent().getData();
                setTitle(R.string.update);
                getProductDetails();
            }
        } catch (NullPointerException ex) {
            Log.e("EditorActivity", "productUri is Empty");
            setTitle(R.string.add);
        } catch (CursorIndexOutOfBoundsException e) {
            Log.e("EditorActivity", e.getMessage());
            setTitle(R.string.add);
        }
    }

    //  content value
    private ContentValues getInputData() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryEntry.COLUMN_Product_NAME, productName.getText().toString().trim());
        contentValues.put(InventoryEntry.COLUMN_Product_Quantity, productQuantity.getText().toString().trim());
        contentValues.put(InventoryEntry.COLUMN_Product_Price, productPrice.getText().toString().trim());
        contentValues.put(InventoryEntry.COLUMN_SUPPLIER_Name, supplierName.getText().toString().trim());
        contentValues.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, supplierMail.getText().toString().trim());
        contentValues.put(InventoryEntry.COLUMN_SUPPLIER_Phone, supplierPhone.getText().toString().trim());
        return contentValues;
    }

    // clears the EditText views content

    private void clearInputFields() {
        productName.setText("");
        productQuantity.setText("");
        productPrice.setText("");
        supplierName.setText("");
        supplierMail.setText("");
        supplierPhone.setText("");
    }

    private boolean resetData() {
        if (!productName.getText().toString().trim().equals(""))
            return true;
        else if (!productQuantity.getText().toString().trim().equals(""))
            return true;
        else if (!productPrice.getText().toString().trim().equals(""))
            return true;
        else if (!supplierName.getText().toString().trim().equals(""))
            return true;
        else if (!supplierMail.getText().toString().trim().equals(""))
            return true;
        else if (!supplierPhone.getText().toString().trim().equals(""))
            return true;
        else
            return false;
    }

    private void getProductDetails() {
        String[] projection = {InventoryEntry._ID,
                InventoryEntry.COLUMN_Product_NAME,
                InventoryEntry.COLUMN_Product_Quantity,
                InventoryEntry.COLUMN_Product_Price,
                InventoryEntry.COLUMN_SUPPLIER_Name,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryEntry.COLUMN_SUPPLIER_Phone
        };

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(InventoryEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        } catch (Exception ex) {
            Log.e("EditorActivity", ex.getMessage());
        }

        cursor.moveToFirst();
        productName.setText(cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_Product_NAME)));
        productQuantity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_Product_Quantity))));
        productPrice.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_Product_Price))));
        supplierName.setText(cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_Name)));
        supplierMail.setText(cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_EMAIL)));
        supplierPhone.setText(cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_Phone)));

    }

    private void updateProduct() {
        try {
            int result = getContentResolver().update(productUri, getInputData(), null, null);
            clearInputFields();
            Toast.makeText(this, "" + result + " Item Updated", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add:
                //check if the activity is opened to update an existing product
                if (productUri != null) {
                    updateProduct();
                } else {
                    // if the activity is opened to add new product
                    try {
                        getContentResolver().insert(InventoryEntry.CONTENT_URI, getInputData());
                        clearInputFields();
                        Toast.makeText(this, getString(R.string.successful), Toast.LENGTH_SHORT).show();
                    } catch (IllegalArgumentException ex) {
                        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                // when the user presses the up arrow
                if (resetData()) {
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            };
                    showUnsavedChangesDialog(discardButtonClickListener);
                } else
                    finish();
        }
        return true;
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_leave));
        builder.setPositiveButton(getString(R.string.yes), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
