package homework.com.bingeeatingproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by svemulapalli on 11/30/16.
 */

public class WeeklyActivityAdapter extends RecyclerView.Adapter<WeeklyActivityAdapter.WeeklyActivityHolder> {

    private ArrayList<WeeklyActivity> mDataSet;
    static Context context;
    CallbackInterface cli;

    public WeeklyActivityAdapter(CallbackInterface callbackInterface,ArrayList<WeeklyActivity> mDataSet) {
        this.cli=callbackInterface;
        this.mDataSet = mDataSet;
    }

    @Override
    public WeeklyActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.weekly_activity_row_item, parent, false);
        WeeklyActivityHolder userViewHolder = new WeeklyActivityAdapter.WeeklyActivityHolder(v);
        //context=parent.getContext();
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(WeeklyActivityAdapter.WeeklyActivityHolder holder, int position) {
        final WeeklyActivity dailyActivity=mDataSet.get(position);
        holder.weekNo.setText("Week " +dailyActivity.getWeekNo());
        holder.dateRange.setText(dailyActivity.getStartDate() + "-"+dailyActivity.getEndDate());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /** Intent foodActivityIntent=new Intent(context,EditWeeklyDetails.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("weeklyActivity",dailyActivity);
                foodActivityIntent.putExtras(bundle);
                ((Activity)context).startActivityForResult(foodActivityIntent,1);**/
                cli.callEditWeeklyDetails(dailyActivity);

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /** Intent foodActivityIntent=new Intent(context,DetailsWeeklyActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("weeklyActivity",dailyActivity);
                foodActivityIntent.putExtras(bundle);
                context.startActivity(foodActivityIntent);**/

                cli.callWeeklyDetails(dailyActivity);


            }
        });

        //holder.icon_entry.setText(""+mDataSet.get(position).getUser_name().charAt(0));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class WeeklyActivityHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView weekNo;
        TextView edit;
        TextView dateRange;


        WeeklyActivityHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.wcard_view);
            weekNo = (TextView) itemView.findViewById(R.id.textView_appDate);
            edit = (TextView) itemView.findViewById(R.id.textView_warEdit);
            dateRange=(TextView) itemView.findViewById(R.id.textView_dateRange);
           // context=itemView.getContext();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface CallbackInterface
    {
        public void callEditWeeklyDetails(WeeklyActivity activity);
        public void callWeeklyDetails(WeeklyActivity activity);
    }

}
