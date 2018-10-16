package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.smartinnovationtechnology.khotwh.AppController;
import net.smartinnovationtechnology.khotwh.R;

import java.util.List;

import datamodels.Product;
import utils.ViewUtil;

/**
 * Created by Shamyyoun on 2/8/2015.
 */
public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private Context context;
    private List<Product> data;
    private int layoutResourceId;
    private OnRemoveClickListener onRemoveClickListener;
    private OnItemClickListener onItemClickListener;

    public ProductsAdapter(Context context, List<Product> data, int layoutResourceId) {
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Product product = data.get(position);

        // set data
        holder.textTitle.setText(product.getTitle());
        holder.textPrice.setText(product.getPrice() + " " + AppController.CURRENCY_UNIT);

        // customize remove icon
        holder.buttonRemove.setVisibility(onRemoveClickListener == null ? View.GONE : View.VISIBLE);

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

        // check position to add suitable margin
        int margin = (int) context.getResources().getDimension(R.dimen.dp3);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.layoutRoot.getLayoutParams();
        if (position % 2 == 0) {
            params.rightMargin = margin;
        } else {
            params.leftMargin = margin;
        }
        if (position != 0 && position != 1) {
            params.topMargin = margin;
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
     * remove click interface
     */
    public interface OnRemoveClickListener {
        public void onRemoveClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View layoutRoot;
        View layoutDefImage;
        ImageView imageImage;
        TextView textTitle;
        TextView textPrice;
        ImageButton buttonRemove;

        public ViewHolder(View v) {
            super(v);
            layoutRoot = v;
            layoutDefImage = v.findViewById(R.id.layout_defImage);
            imageImage = (ImageView) v.findViewById(R.id.image_image);
            textTitle = (TextView) v.findViewById(R.id.text_title);
            textPrice = (TextView) v.findViewById(R.id.text_price);
            buttonRemove = (ImageButton) v.findViewById(R.id.button_remove);

            // add click listeners
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