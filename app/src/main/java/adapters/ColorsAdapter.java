package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import net.smartinnovationtechnology.khotwh.R;

import datamodels.Product;
import views.RoundedRectView;

/**
 * Created by Shamyyoun on 4/30/2015.
 */
public class ColorsAdapter extends ArrayAdapter<Product.Color> {
    private int layoutRes;
    private LayoutInflater mInflater;
    private Product.Color[] colors;

    public ColorsAdapter(Context context, int layoutRes, int textViewId, Product.Color[] colors) {
        super(context, layoutRes, textViewId, colors);
        mInflater = LayoutInflater.from(context);
        this.layoutRes = layoutRes;
        this.colors = colors;
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Product.Color getItem(int position) {
        return colors[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(layoutRes, null);
            holder = new ViewHolder();
            holder.roundedRectView = (RoundedRectView) view.findViewById(R.id.roundedRectView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Product.Color color = colors[position];
        holder.roundedRectView.setColor(color.getColor());

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final DropDownViewHolder holder;
        View dropDownView = convertView;
        if (dropDownView == null) {
            dropDownView = mInflater.inflate(layoutRes, null);
            holder = new DropDownViewHolder();
            holder.roundedRectView = (RoundedRectView) dropDownView.findViewById(R.id.roundedRectView);
            dropDownView.setTag(holder);
        } else {
            holder = (DropDownViewHolder) dropDownView.getTag();
        }

        Product.Color color = colors[position];
        holder.roundedRectView.setColor(color.getColor());

        return dropDownView;
    }

    static class ViewHolder {
        RoundedRectView roundedRectView;
    }

    static class DropDownViewHolder {
        RoundedRectView roundedRectView;
    }
}
