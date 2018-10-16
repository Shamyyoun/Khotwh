package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Shamyyoun on 4/30/2015.
 */
public class SizesAdapter extends ArrayAdapter<String> {
    private int layoutRes;
    private LayoutInflater mInflater;
    private String[] sizes;

    public SizesAdapter(Context context, int layoutRes, String[] sizes) {
        super(context, layoutRes, sizes);
        mInflater = LayoutInflater.from(context);
        this.layoutRes = layoutRes;
        this.sizes = sizes;
    }

    @Override
    public int getCount() {
        return sizes.length;
    }

    @Override
    public String getItem(int position) {
        return sizes[position];
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

            holder.textView = (TextView) view;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.textView.setText(sizes[position]);

        return view;
    }

    static class ViewHolder {
        TextView textView;
    }
}
