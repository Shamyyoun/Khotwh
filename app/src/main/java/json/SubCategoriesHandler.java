package json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import datamodels.SubCategory;

public class SubCategoriesHandler {
    private JSONArray jsonArray;

    public SubCategoriesHandler(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public SubCategoriesHandler(String jsonArray) {
        try {
            this.jsonArray = new JSONArray(jsonArray);
        } catch (JSONException e) {
        }
    }

    public SubCategory[] handle() {
        SubCategory[] subCategories = new SubCategory[jsonArray.length()];
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // parse object
                int id = jsonObject.getInt("id");
                String title = jsonObject.getString("name");
                String image = jsonObject.getString("image");

                SubCategory subCategory = new SubCategory(id, title, image);
                subCategories[i] = subCategory;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            subCategories = null;
        }

        return subCategories;
    }
}
