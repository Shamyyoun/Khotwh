package datamodels;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Shamyyoun on 3/29/2015.
 */
public class Cache {
    /**
     * method used to update sub categories response in SP
     */
    public static void updateSubCategoriesResponse(Context context, String value, String categoryTitle) {
        updateCachedString(context, Constants.SP_RESPONSES, Constants.SP_KEY_SUB_CATEGORIES + categoryTitle, value);
    }

    /**
     * method used to get sub categories response from SP
     */
    public static String getSubCategoriesResponse(Context context, String categoryTitle) {
        return getCachedString(context, Constants.SP_RESPONSES, Constants.SP_KEY_SUB_CATEGORIES + categoryTitle);
    }

    /**
     * method used to update products response in SP
     */
    public static void updateProductsResponse(Context context, String value, int subCategoryId) {
        updateCachedString(context, Constants.SP_RESPONSES, Constants.SP_KEY_PRODUCTS + subCategoryId, value);
    }

    /**
     * method used to get products response from SP
     */
    public static String getProductsResponse(Context context, int subCategoryId) {
        return getCachedString(context, Constants.SP_RESPONSES, Constants.SP_KEY_PRODUCTS + subCategoryId);
    }


    /*
     * method, used to update string value in SP
     */
    private static void updateCachedString(Context context, String spName, String valueName, String value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(valueName, value);
        editor.commit();
    }

    /*
     * method, used to get cached String from SP
     */
    private static String getCachedString(Context context, String spName, String valueName) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        String value = sp.getString(valueName, null);

        return value;
    }
}
