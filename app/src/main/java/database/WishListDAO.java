package database;

import android.content.Context;

import java.util.List;

import datamodels.Product;

public class WishListDAO {
    private ProductsDAO dao;

    public WishListDAO(Context context) {
        dao = new ProductsDAO(context, ProductsDAO.TYPE_WISH_LIST);
    }

    /**
     * method, used to add product to wish list
     */
    public void add(Product product) {
        dao.open();
        dao.add(product);
        dao.close();
    }

    /**
     * method, used to delete item from database filtered by id
     */
    public void delete(int productId) {
        dao.open();
        dao.delete(productId);
        dao.close();
    }

    /**
     * method, used to get all items from db
     */
    public List<Product> getAll() {
        dao.open();
        List<Product> products = dao.getAll();
        dao.close();
        return products;
    }

    /**
     * method, used to check if item exists in database or not
     */
    public boolean existsInDB(int productId) {
        dao.open();
        boolean exists = dao.existsInDB(productId);
        dao.close();
        return exists;
    }
}