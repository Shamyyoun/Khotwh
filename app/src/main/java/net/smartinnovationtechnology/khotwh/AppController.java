package net.smartinnovationtechnology.khotwh;

import android.app.Application;
import android.content.Context;

import database.UserDAO;
import datamodels.User;

/**
 * Created by Shamyyoun on 3/15/2015.
 */
public class AppController extends Application {
    public static final String END_POINT = "http://mahmoudelshamy.com/khotwah//ws";
    public static final String CURRENCY_UNIT = "LE.";
    private User activeUser;

    public AppController() {
        super();
    }

    /**
     * overridden method
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * method, used to get active user from runtime or from database
     */
    public static User getActiveUser(Context context) {
        // get active user from runtime
        User user = getInstance(context).activeUser;
        // check it
        if (user == null) {
            // get saved user from database if exists
            UserDAO userDAO = new UserDAO(context);
            userDAO.open();
            user = userDAO.get();
            userDAO.close();

            // set it in runtime
            setActiveUser(context, user);
        }

        // return it
        return user;
    }

    /**
     * method used to set active user in runtime
     */
    public static void setActiveUser(Context context, User user) {
        getInstance(context).activeUser = user;
    }

    /**
     * method used to return current application instance
     */
    public static AppController getInstance(Context context) {
        return (AppController) context.getApplicationContext();
    }
}
