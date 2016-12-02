package homework.com.bingeeatingproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by svemulapalli on 11/10/16.
 */

public class SettingsAdapter extends ArrayAdapter<Settings> {

    Context context;
    int layoutResourceId;
    Settings data[] = null;

    public SettingsAdapter(Context context, int layoutResourceId, Settings[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        settingsHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new settingsHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.imageView_s);
            holder.txtTitle = (TextView) row.findViewById(R.id.textView_s);

            row.setTag(holder);
        } else {
            holder = (settingsHolder) row.getTag();
        }

        Settings weather = data[position];
        holder.txtTitle.setText(weather.title);
        holder.imgIcon.setImageResource(weather.icon);

        return row;
    }

    static class settingsHolder {
        ImageView imgIcon;
        TextView txtTitle;

    }
}