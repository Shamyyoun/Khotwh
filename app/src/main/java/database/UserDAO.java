package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import datamodels.User;

public class UserDAO {
    private SQLiteDatabase database;
    private DatabaseSQLiteHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DatabaseSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * method, used to add user to database
     */
    public void add(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteHelper.USERS_ID, user.getId());
        values.put(DatabaseSQLiteHelper.USERS_FIRST_NAME, user.getFirstName());
        values.put(DatabaseSQLiteHelper.USERS_LAST_NAME, user.getLastName());
        values.put(DatabaseSQLiteHelper.USERS_MOBILE, user.getMobile());
        values.put(DatabaseSQLiteHelper.USERS_EMAIL, user.getEmail());
        values.put(DatabaseSQLiteHelper.USERS_CITY, user.getCity());
        values.put(DatabaseSQLiteHelper.USERS_ADDRESS, user.getAddress());

        database.insert(DatabaseSQLiteHelper.TABLE_USERS, null, values);
    }

    /**
     * method, used to update user in database
     */
    public void update(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteHelper.USERS_FIRST_NAME, user.getFirstName());
        values.put(DatabaseSQLiteHelper.USERS_LAST_NAME, user.getLastName());
        values.put(DatabaseSQLiteHelper.USERS_MOBILE, user.getMobile());
        values.put(DatabaseSQLiteHelper.USERS_EMAIL, user.getEmail());
        values.put(DatabaseSQLiteHelper.USERS_CITY, user.getCity());
        values.put(DatabaseSQLiteHelper.USERS_ADDRESS, user.getAddress());

        database.update(DatabaseSQLiteHelper.TABLE_USERS, values, DatabaseSQLiteHelper.USERS_ID + " = " + user.getId(), null);
    }

    /**
     * method, used to delete all users from database
     */
    public void clear() {
        database.delete(DatabaseSQLiteHelper.TABLE_USERS, null, null);
    }

    /**
     * method, used to get last user from db
     */
    public User get() {
        User user = null;
        Cursor cursor = database.query(DatabaseSQLiteHelper.TABLE_USERS, null, null, null, null, null, null);

        if (cursor.getCount() != 0) {
            cursor.moveToLast();

            int id = cursor.getInt(cursor.getColumnIndex(DatabaseSQLiteHelper.USERS_ID));
            String firstName = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.USERS_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.USERS_LAST_NAME));
            String mobile = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.USERS_MOBILE));
            String email = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.USERS_EMAIL));
            String city = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.USERS_CITY));
            String address = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.USERS_ADDRESS));

            user = new User(id);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setMobile(mobile);
            user.setEmail(email);
            user.setCity(city);
            user.setAddress(address);
        }

        cursor.close();
        return user;
    }

    /**
     * method, used to check if database has users or not based on count
     */
    public boolean hasUsers() {
        Cursor mCount = database.rawQuery("SELECT COUNT(" + DatabaseSQLiteHelper.PRODUCTS_ID
                + ") FROM " + DatabaseSQLiteHelper.TABLE_PRODUCTS, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();

        if (count == 0)
            return false;
        else
            return true;
    }
}