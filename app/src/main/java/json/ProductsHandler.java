package json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import datamodels.Product;

public class ProductsHandler {
    private JSONArray jsonArray;

    public ProductsHandler(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public ProductsHandler(String jsonArray) {
        try {
            this.jsonArray = new JSONArray(jsonArray);
        } catch (JSONException e) {
        }
    }

    public List<Product> handle() {
        List<Product> products = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // parse basic data
                int id = jsonObject.getInt("id");
                String title = jsonObject.getString("name");
                int price = jsonObject.getInt("price");

                // get sizes array
                JSONArray sizesJA = jsonObject.getJSONArray("sizes");
                String[] sizes = new String[sizesJA.length()];
                for (int j = 0; j < sizesJA.length(); j++) {
                    sizes[j] = sizesJA.getString(j);
                }

                // get colors array
                JSONArray colorsJA = jsonObject.getJSONArray("colors");
                Product.Color[] colors = new Product.Color[colorsJA.length()];
                for (int j = 0; j < colorsJA.length(); j++) {
                    JSONObject colorJO = colorsJA.getJSONObject(j);
                    // String colorTitle = colorJO.getString("title");
                    String colorTitle = null;
                    String colorCode = colorJO.getString("color");

                    // get color's images
                    JSONArray imagesJA = colorJO.getJSONArray("images");
                    String[] images = new String[imagesJA.length()];
                    for (int k = 0; k < imagesJA.length(); k++) {
                        images[k] = imagesJA.getString(k);
                    }

                    // create color object and add it to colors array
                    Product.Color color = new Product.Color(colorTitle, colorCode, images);
                    colors[j] = color;
                }

                // create product object
                Product product = new Product(id, title, price);
                product.setSizes(sizes);
                product.setColors(colors);

                products.add(product);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            products = null;
        }

        return products;
    }
}
