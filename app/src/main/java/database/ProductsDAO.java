package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import datamodels.Product;

public class ProductsDAO {
    protected static final int TYPE_WISH_LIST = 1;
    protected static final int TYPE_CART = 2;

    private int type;
    private SQLiteDatabase database;
    private DatabaseSQLiteHelper dbHelper;

    protected ProductsDAO(Context context, int type) {
        this.type = type;
        dbHelper = new DatabaseSQLiteHelper(context);
    }

    protected void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    protected void close() {
        dbHelper.close();
    }

    /**
     * method, used to add product to database
     */
    protected void add(Product product) {
        // add basic data to products table
        ContentValues productsValues = new ContentValues();
        productsValues.put(DatabaseSQLiteHelper.PRODUCTS_ID, product.getId());
        productsValues.put(DatabaseSQLiteHelper.PRODUCTS_TITLE, product.getTitle());
        productsValues.put(DatabaseSQLiteHelper.PRODUCTS_PRICE, product.getPrice());
        productsValues.put(DatabaseSQLiteHelper.PRODUCTS_SELECTED_COLOR, product.getSelectedColor());
        productsValues.put(DatabaseSQLiteHelper.PRODUCTS_SELECTED_SIZE, product.getSelectedSize());
        productsValues.put(DatabaseSQLiteHelper.PRODUCTS_TYPE, type);
        database.insert(DatabaseSQLiteHelper.TABLE_PRODUCTS, null, productsValues);

        // loop to add sizes to sizes table
        for (String size : product.getSizes()) {
            ContentValues sizesValues = new ContentValues();
            sizesValues.put(DatabaseSQLiteHelper.SIZES_TITLE, size);
            sizesValues.put(DatabaseSQLiteHelper.SIZES_PROD_ID, product.getId());
            sizesValues.put(DatabaseSQLiteHelper.SIZES_PROD_TYPE, type);
            database.insert(DatabaseSQLiteHelper.TABLE_SIZES, null, sizesValues);
        }

        // loop to add colors to colors table
        for (Product.Color color : product.getColors()) {
            ContentValues colorsValues = new ContentValues();
            colorsValues.put(DatabaseSQLiteHelper.COLORS_TITLE, color.getTitle());
            colorsValues.put(DatabaseSQLiteHelper.COLORS_COLOR_CODE, color.getColor());
            colorsValues.put(DatabaseSQLiteHelper.COLORS_PROD_ID, product.getId());
            colorsValues.put(DatabaseSQLiteHelper.COLORS_PROD_TYPE, type);
            long colorId = database.insert(DatabaseSQLiteHelper.TABLE_COLORS, null, colorsValues);

            // loop to add images to images table
            for (String image : color.getImages()) {
                ContentValues imagesValues = new ContentValues();
                imagesValues.put(DatabaseSQLiteHelper.IMAGES_URL, image);
                imagesValues.put(DatabaseSQLiteHelper.IMAGES_COLOR_ID, colorId);
                database.insert(DatabaseSQLiteHelper.TABLE_IMAGES, null, imagesValues);
            }
        }
    }

    /**
     * method, used to delete item from database filtered by id
     */
    protected void delete(int productId) {
        database.delete(DatabaseSQLiteHelper.TABLE_PRODUCTS, DatabaseSQLiteHelper.PRODUCTS_ID + " = " + productId
                + " AND " + DatabaseSQLiteHelper.PRODUCTS_TYPE + " = " + type, null);
        database.delete(DatabaseSQLiteHelper.TABLE_SIZES, DatabaseSQLiteHelper.SIZES_PROD_ID + " = " + productId
                + " AND " + DatabaseSQLiteHelper.SIZES_PROD_TYPE + " = " + type, null);
        database.delete(DatabaseSQLiteHelper.TABLE_IMAGES, DatabaseSQLiteHelper.IMAGES_COLOR_ID + " = " + getColorsId(productId), null);
        database.delete(DatabaseSQLiteHelper.TABLE_COLORS, DatabaseSQLiteHelper.COLORS_PROD_ID + " = " + productId
                + " AND " + DatabaseSQLiteHelper.COLORS_PROD_TYPE + " = " + type, null);
    }

    /**
     * method, used to delete all items from database
     */
    protected void clear() {
        database.delete(DatabaseSQLiteHelper.TABLE_PRODUCTS, DatabaseSQLiteHelper.PRODUCTS_TYPE + " = " + type, null);
        database.delete(DatabaseSQLiteHelper.TABLE_SIZES, DatabaseSQLiteHelper.SIZES_PROD_TYPE + " = " + type, null);
        database.delete(DatabaseSQLiteHelper.TABLE_IMAGES, DatabaseSQLiteHelper.IMAGES_COLOR_ID + " = " + getColorsId(), null);
        database.delete(DatabaseSQLiteHelper.TABLE_COLORS, DatabaseSQLiteHelper.COLORS_PROD_TYPE + " = " + type, null);
    }

    /**
     * method, used to get colors id for a product from db
     */
    private int getColorsId(int productId) {
        Cursor cursor = database.query(DatabaseSQLiteHelper.TABLE_COLORS, null,
                DatabaseSQLiteHelper.COLORS_PROD_ID + " = " + productId
                        + " AND " + DatabaseSQLiteHelper.COLORS_PROD_TYPE + " = " + type, null, null, null, null);

        int colorsId = -1;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            colorsId = cursor.getInt(cursor.getColumnIndex(DatabaseSQLiteHelper.COLORS_ID));
            cursor.close();
        }
        return colorsId;
    }

    /**
     * method, used to get colors id for a type from db
     */
    private int getColorsId() {
        Cursor cursor = database.query(DatabaseSQLiteHelper.TABLE_COLORS, null,
                DatabaseSQLiteHelper.COLORS_PROD_TYPE + " = " + type, null, null, null, null);

        int colorsId = -1;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            colorsId = cursor.getInt(cursor.getColumnIndex(DatabaseSQLiteHelper.COLORS_ID));
            cursor.close();
        }
        return colorsId;
    }

    /**
     * method, used to get all items from db
     */
    protected List<Product> getAll() {
        List<Product> products = new ArrayList();
        Cursor productsCursor = database.query(DatabaseSQLiteHelper.TABLE_PRODUCTS, null,
                DatabaseSQLiteHelper.PRODUCTS_TYPE + " = " + type, null, null, null, null);

        productsCursor.moveToFirst();
        while (!productsCursor.isAfterLast()) {
            // get values from cursor
            int id = productsCursor.getInt(productsCursor.getColumnIndex(DatabaseSQLiteHelper.PRODUCTS_ID));
            String title = productsCursor.getString(productsCursor.getColumnIndex(DatabaseSQLiteHelper.PRODUCTS_TITLE));
            int price = productsCursor.getInt(productsCursor.getColumnIndex(DatabaseSQLiteHelper.PRODUCTS_PRICE));
            String selectedColor = productsCursor.getString(productsCursor.getColumnIndex(DatabaseSQLiteHelper.PRODUCTS_SELECTED_COLOR));
            String selectedSize = productsCursor.getString(productsCursor.getColumnIndex(DatabaseSQLiteHelper.PRODUCTS_SELECTED_SIZE));

            // create product object
            Product product = new Product(id);
            product.setTitle(title);
            product.setPrice(price);
            product.setSelectedColor(selectedColor);
            product.setSelectedSize(selectedSize);

            // get sizes from sizes table
            Cursor sizesCursor = database.query(DatabaseSQLiteHelper.TABLE_SIZES, null,
                    DatabaseSQLiteHelper.SIZES_PROD_ID + " = " + product.getId()
                            + " AND " + DatabaseSQLiteHelper.SIZES_PROD_TYPE + " = " + type, null, null, null, null);
            sizesCursor.moveToFirst();
            String[] sizes = new String[sizesCursor.getCount()];
            while (!sizesCursor.isAfterLast()) {
                String size = sizesCursor.getString(sizesCursor.getColumnIndex(DatabaseSQLiteHelper.SIZES_TITLE));
                sizes[sizesCursor.getPosition()] = size;
                sizesCursor.moveToNext();
            }
            sizesCursor.close();

            // get colors from colors table
            Cursor colorsCursor = database.query(DatabaseSQLiteHelper.TABLE_COLORS, null,
                    DatabaseSQLiteHelper.COLORS_PROD_ID + " = " + product.getId()
                            + " AND " + DatabaseSQLiteHelper.COLORS_PROD_TYPE + " = " + type, null, null, null, null);
            colorsCursor.moveToFirst();
            Product.Color[] colors = new Product.Color[colorsCursor.getCount()];
            while (!colorsCursor.isAfterLast()) {
                int colorId = colorsCursor.getInt(colorsCursor.getColumnIndex(DatabaseSQLiteHelper.COLORS_ID));
                String colorTitle = colorsCursor.getString(colorsCursor.getColumnIndex(DatabaseSQLiteHelper.COLORS_TITLE));
                String colorCode = colorsCursor.getString(colorsCursor.getColumnIndex(DatabaseSQLiteHelper.COLORS_COLOR_CODE));

                // get images from image table
                Cursor imagesCursor = database.query(DatabaseSQLiteHelper.TABLE_IMAGES, null,
                        DatabaseSQLiteHelper.IMAGES_COLOR_ID + " = " + colorId, null, null, null, null);
                imagesCursor.moveToFirst();
                String[] images = new String[imagesCursor.getCount()];
                while (!imagesCursor.isAfterLast()) {
                    String image = imagesCursor.getString(imagesCursor.getColumnIndex(DatabaseSQLiteHelper.IMAGES_URL));
                    images[imagesCursor.getPosition()] = image;
                    imagesCursor.moveToNext();
                }
                imagesCursor.close();

                // create color object
                Product.Color color = new Product.Color(colorTitle, colorCode, images);
                colors[colorsCursor.getPosition()] = color;
                colorsCursor.moveToNext();
            }
            colorsCursor.close();

            // update products object
            product.setSizes(sizes);
            product.setColors(colors);

            // add to array list
            products.add(product);
            productsCursor.moveToNext();
        }
        productsCursor.close();
        return products;
    }

    /**
     * method, used to check if item exists in database or not
     */
    protected boolean existsInDB(int id) {
        Cursor mCount = database.rawQuery("SELECT COUNT(" + DatabaseSQLiteHelper.PRODUCTS_ID
                + ") FROM " + DatabaseSQLiteHelper.TABLE_PRODUCTS
                + " WHERE " + DatabaseSQLiteHelper.PRODUCTS_ID + " = " + id
                + " AND " + DatabaseSQLiteHelper.PRODUCTS_TYPE + " = " + type, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();

        if (count == 0)
            return false;
        else
            return true;
    }
}