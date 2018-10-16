package datamodels;

import java.io.Serializable;

/**
 * Created by Shamyyoun on 12/1/2015.
 */
public class Product implements Serializable {
    private int id;
    private String title;
    private int price;
    private String[] sizes;
    private Color[] colors;
    // variables used in order
    private int count = 1;
    private String selectedSize;
    private String selectedColor;

    public Product(int id) {
        this.id = id;
    }

    public Product(int id, String title, int price) {
        this.id = id;
        this.title = title;
        this.price = price;
        colors = new Color[0];
        sizes = new String[0];
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Color[] getColors() {
        return colors;
    }

    public void setColors(Color[] colors) {
        this.colors = colors;
    }

    public String[] getSizes() {
        return sizes;
    }

    public void setSizes(String[] sizes) {
        this.sizes = sizes;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }

    public String getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(String selectedSize) {
        this.selectedSize = selectedSize;
    }

    /**
     * *** Color ***
     */
    public static class Color implements Serializable {
        private String title;
        private String color;
        private String[] images;

        public Color(String title, String color, String[] images) {
            this.title = title;
            this.color = color;
            this.images = images;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String[] getImages() {
            return images;
        }

        public void setImages(String[] images) {
            this.images = images;
        }
    }
}
