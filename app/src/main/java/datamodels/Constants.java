package datamodels;

/**
 * Created by Shamyyoun on 11/26/2015.
 */
public class Constants {
    // keys
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_SUB_CATEGORY = "sub_category";
    public static final String KEY_PRODUCT = "product";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_KEYWORD = "keyword";
    public static final String KEY_REMOVED_FROM_WISH_LIST = "removed_from_wish_list";
    public static final String KEY_POSITION = "position";

    // categories
    public static final String CAT_MEN = "men";
    public static final String CAT_WOMEN = "women";
    public static final String CAT_KIDS = "kids";

    // SP
    public static final String SP_RESPONSES = "responses";
    public static final String SP_KEY_SUB_CATEGORIES = "sub_categories";
    public static final String SP_KEY_PRODUCTS = "products";

    // volley request tags
    public static final String VOLLEY_REQ_SUB_CATEGORIES = "sub_categories";
    public static final String VOLLEY_REQ_PRODUCTS = "products";
    public static final String VOLLEY_REQ_SEARCH = "search";
    public static final String VOLLEY_REQ_UPDATE_USER = "updated";
    public static final String VOLLEY_REQ_ORDER = "order";

    // connection request timeouts
    public static final int CON_TIMEOUT_SUB_CATEGORIES = 30 * 1000;
    public static final int CON_TIMEOUT_PRODUCTS = 60 * 1000;
    public static final int CON_TIMEOUT_UPDATE_USER = 30 * 1000;
    public static final int CON_TIMEOUT_ORDER = 30 * 1000;

    // server data limits
    public static final int SERVER_LIMIT_PRODUCTS = 30;

    // activity requests
    public static final int REQ_PRODUCT_DETAILS = 1;
    public static final int REQ_USER_REGISTRATION = 2;
    public static final int REQ_USER_UPDATE = 3;

    // json result constants
    public static final String JSON_OK = "ok";
    public static final String JSON_ERROR = "error";
}
