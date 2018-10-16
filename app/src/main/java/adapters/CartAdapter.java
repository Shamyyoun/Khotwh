package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.smartinnovationtechnology.khotwh.AppController;
import net.smartinnovationtechnology.khotwh.R;

import java.util.List;

import database.WishListDAO;
import datamodels.Product;
import utils.ViewUtil;

/**
 * Created by Shamyyoun on 2/8/2015.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context context;
    private WishListDAO wishListDAO;
    private List<Product> data;
    private int layoutResourceId;
    private OnFavoriteClickListener onFavoriteClickListener;
    private OnRemoveClickListener onRemoveClickListener;
    private OnItemClickListener onItemClickListener;

    public CartAdapter(Context context, List<Product> data, int layoutResourceId) {
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
        wishListDAO = new WishListDAO(context);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Product product = data.get(position);

        // set data
        holder.textTitle.setText(product.getTitle());
        holder.textPrice.setText(product.getPrice() + " " + AppController.CURRENCY_UNIT);
        holder.textCount.setText("" + product.getCount());

        // enable / disable favorite button
        holder.buttonFavorite.setEnabled(!wishListDAO.existsInDB(product.getId()));

        // add increase & decrease click listener
        holder.buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // increase
                product.setCount(product.getCount() + 1);
                holder.textCount.setText("" + product.getCount());
            }
        });
        holder.buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check count
                if (product.getCount() > 1) {
                    // decrease
                    product.setCount(product.getCount() - 1);
                    holder.textCount.setText("" + product.getCount());
                }
            }
        });

        // load first image if possible
        // check colors array length
        if (product.getColors().length >= 1) {
            // get first color in the array
            Product.Color color = product.getColors()[0];
            // check if this color has images
            if (color.getImages().length >= 1) {
                // get first image
                String image = color.getImages()[0];
                // load the image if possible
                if (!image.isEmpty()) {
                    Picasso.with(context).load(image).into(holder.imageImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            ViewUtil.fadeView(holder.layoutDefImage, false, 150);
                        }

                        @Override
                        public void onError() {
                            ViewUtil.fadeView(holder.layoutDefImage, true, 150);
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutResourceId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    /**
     * setter
     */
    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * setter
     */
    public void setOnFavoriteClickListener(final OnFavoriteClickListener onFavoriteClickListener) {
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    /**
     * setter
     */
    public void setOnRemoveClickListener(final OnRemoveClickListener onRemoveClickListener) {
        this.onRemoveClickListener = onRemoveClickListener;
    }

    /**
     * item click interface
     */
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    /**
     * favorite click interface
     */
    public interface OnFavoriteClickListener {
        public void onFavoriteClick(int position);
    }

    /**
     * remove click interface
     */
    public interface OnRemoveClickListener {
        public void onRemoveClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View layoutDefImage;
        ImageView imageImage;
        TextView textTitle;
        TextView textPrice;
        TextView textCount;
        ImageButton buttonIncrease;
        ImageButton buttonDecrease;
        Button buttonFavorite;
        Button buttonRemove;

        public ViewHolder(View v) {
            super(v);
            layoutDefImage = v.findViewById(R.id.layout_defImage);
            imageImage = (ImageView) v.findViewById(R.id.image_image);
            textTitle = (TextView) v.findViewById(R.id.text_title);
            textPrice = (TextView) v.findViewById(R.id.text_price);
            textCount = (TextView) v.findViewById(R.id.text_count);
            buttonIncrease = (ImageButton) v.findViewById(R.id.button_increase);
            buttonDecrease = (ImageButton) v.findViewById(R.id.button_decrease);
            buttonFavorite = (Button) v.findViewById(R.id.button_favorite);
            buttonRemove = (Button) v.findViewById(R.id.button_remove);

            // add global click listeners
            buttonFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onFavoriteClickListener != null) {
                        onFavoriteClickListener.onFavoriteClick(getPosition());
                    }
                }
            });
            buttonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRemoveClickListener != null) {
                        onRemoveClickListener.onRemoveClick(getPosition());
                    }
                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, getPosition());
                    }
                }
            });
        }
    }
}