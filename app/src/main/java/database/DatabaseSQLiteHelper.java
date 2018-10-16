package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseSQLiteHelper extends SQLiteOpenHelper {
    // database info
    private static final String DATABASE_NAME = "khotwh.db";
    private static final int DATABASE_VERSION = 4;

    // table users
    public static final String TABLE_USERS = "users";
    public static final String USERS_ID = "_id";
    public static final String USERS_FIRST_NAME = "first_name";
    public static final String USERS_LAST_NAME = "last_name";
    public static final String USERS_MOBILE = "mobile";
    public static final String USERS_EMAIL = "email";
    public static final String USERS_ADDRESS = "address";
    public static final String USERS_CITY = "city";

    // table wish list
    public static final String TABLE_PRODUCTS = "products";
    public static final String PRODUCTS_ID = "_id";
    public static final String PRODUCTS_TITLE = "title";
    public static final String PRODUCTS_PRICE = "price";
    public static final String PRODUCTS_SELECTED_COLOR = "selected_color";
    public static final String PRODUCTS_SELECTED_SIZE = "selected_size";
    public static final String PRODUCTS_TYPE = "type";

    // table sizes
    public static final String TABLE_SIZES = "sizes";
    public static final String SIZES_ID = "_id";
    public static final String SIZES_TITLE = "title";
    public static final String SIZES_PROD_ID = "product_id";
    public static final String SIZES_PROD_TYPE = "product_type";

    // table colors
    public static final String TABLE_COLORS = "colors";
    public static final String COLORS_ID = "_id";
    public static final String COLORS_TITLE = "title";
    public static final String COLORS_COLOR_CODE = "color_code";
    public static final String COLORS_PROD_ID = "product_id";
    public static final String COLORS_PROD_TYPE = "product_type";

    // table images
    public static final String TABLE_IMAGES = "images";
    public static final String IMAGES_ID = "_id";
    public static final String IMAGES_URL = "url";
    public static final String IMAGES_COLOR_ID = "color_id";

    // tables creation
    private static final String USERS_CREATE = "CREATE TABLE " + TABLE_USERS
            + "("
            + USERS_ID + " INTEGER PRIMARY KEY, "
            + USERS_FIRST_NAME + " TEXT, "
            + USERS_LAST_NAME + " TEXT, "
            + USERS_MOBILE + " TEXT, "
            + USERS_EMAIL + " TEXT, "
            + USERS_CITY + " TEXT, "
            + USERS_ADDRESS + " TEXT"
            + ");";

    private static final String PRODUCTS_CREATE = "CREATE TABLE " + TABLE_PRODUCTS
            + "("
            + PRODUCTS_ID + " INTEGER, "
            + PRODUCTS_TITLE + " TEXT, "
            + PRODUCTS_PRICE + " INTEGER, "
            + PRODUCTS_SELECTED_COLOR + " TEXT, "
            + PRODUCTS_SELECTED_SIZE + " TEXT, "
            + PRODUCTS_TYPE + " INTEGER, "
            + "PRIMARY KEY (" + PRODUCTS_ID + ", " + PRODUCTS_TYPE + ")"
            + ");";

    private static final String SIZES_CREATE = "CREATE TABLE " + TABLE_SIZES
            + "("
            + SIZES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SIZES_TITLE + " TEXT, "
            + SIZES_PROD_ID + " INTEGER, "
            + SIZES_PROD_TYPE + " INTEGER"
            + ");";

    private static final String COLORS_CREATE = "CREATE TABLE " + TABLE_COLORS
            + "("
            + COLORS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLORS_TITLE + " TEXT, "
            + COLORS_COLOR_CODE + " TEXT, "
            + COLORS_PROD_ID + " INTEGER, "
            + COLORS_PROD_TYPE + " INTEGER"
            + ");";

    private static final String IMAGES_CREATE = "CREATE TABLE " + TABLE_IMAGES
            + "("
            + IMAGES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + IMAGES_URL + " TEXT, "
            + IMAGES_COLOR_ID + " INTEGER"
            + ");";

    public DatabaseSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // create tables
        database.execSQL(USERS_CREATE);
        database.execSQL(PRODUCTS_CREATE);
        database.execSQL(SIZES_CREATE);
        database.execSQL(COLORS_CREATE);
        database.execSQL(IMAGES_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIZES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }

}