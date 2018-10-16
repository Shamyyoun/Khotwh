package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.smartinnovationtechnology.khotwh.R;

import datamodels.SubCategory;
import utils.ViewUtil;

public class SubCategoriesAdapter extends BaseAdapter {
    private Context context;
    private int layoutResId;
    private SubCategory[] subCategories;
    private LayoutInflater inflater;

    public SubCategoriesAdapter(Context context, int layoutResId, SubCategory[] subCategories) {
        this.context = context;
        this.layoutResId = layoutResId;
        this.subCategories = subCategories;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView = convertView;
        final ViewHolder holder;

        if (gridView == null) {
            gridView = inflater.inflate(layoutResId, parent, false);
            holder = new ViewHolder();
            holder.layoutDefImage = gridView.findViewById(R.id.layout_defImage);
            holder.imageImage = (ImageView) gridView.findViewById(R.id.image_image);
            holder.textTitle = (TextView) gridView.findViewById(R.id.text_title);
            gridView.setTag(holder);
        } else {
            holder = (ViewHolder) gridView.getTag();
        }

        // set title
        SubCategory subCategory = subCategories[position];
        holder.textTitle.setText(subCategory.getTitle());

        // load image
        if (!subCategory.getImage().isEmpty()) {
            Picasso.with(context).load(subCategory.getImage()).into(holder.imageImage, new Callback() {
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

        return gridView;
    }

    @Override
    public int getCount() {
        return subCategories.length;
    }

    @Override
    public SubCategory getItem(int position) {
        return subCategories[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        View layoutDefImage;
        ImageView imageImage;
        TextView textTitle;
    }
}
