package datamodels;

import java.io.Serializable;

/**
 * Created by Shamyyoun on 12/1/2015.
 */
public class SubCategory implements Serializable{
    private int id;
    private String title;
    private String image;

    public SubCategory(int id, String title, String image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
