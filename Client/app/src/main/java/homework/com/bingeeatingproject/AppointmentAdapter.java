package homework.com.bingeeatingproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by svemulapalli on 12/1/16.
 */

public class AppointmentAdapter extends ArrayAdapter<AppointmentDetails> {

    Context context;
    int layoutResourceId;
    ArrayList<AppointmentDetails>  data=new ArrayList<>();

    public AppointmentAdapter(Context context, int layoutResourceId, ArrayList<AppointmentDetails> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AppointmentAdapter.appointmentsHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new AppointmentAdapter.appointmentsHolder();
            holder.date = (TextView) row.findViewById(R.id.textView_appDate);
            holder.time = (TextView) row.findViewById(R.id.textView_appTime);
            holder.status = (TextView) row.findViewById(R.id.textView_appStatus);

            row.setTag(holder);
        } else {
            holder = (AppointmentAdapter.appointmentsHolder) row.getTag();
        }

        AppointmentDetails weather = data.get(position);
        holder.date.setText(weather.date);
        holder.time.setText(weather.time);
        holder.status.setText(weather.status);

        return row;
    }

    static class appointmentsHolder {
        TextView date;
        TextView time;
        TextView status;

    }
}
