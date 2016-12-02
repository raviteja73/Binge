package homework.com.bingeeatingproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by svemulapalli on 11/28/16.
 */

public class DailyActivityAdapter extends RecyclerView.Adapter<DailyActivityAdapter.DailyActivityHolder> {

    private ArrayList<DailyActivity> mDataSet;
    static Context context;
     private CallbackInterface cli;
    public DailyActivityAdapter(CallbackInterface mcontext,ArrayList<DailyActivity> mDataSet) {
        cli=(CallbackInterface)mcontext;
        this.mDataSet = mDataSet;
    }

    @Override
    public DailyActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_activity_row_item, parent, false);
        DailyActivityHolder userViewHolder = new DailyActivityHolder(v);
        context=parent.getContext();
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(DailyActivityHolder holder, int position) {
        final DailyActivity dailyActivity=mDataSet.get(position);
        holder.activityName.setText(dailyActivity.getActivityName());
        holder.dateTime.setText(dailyActivity.getDateTime());
        holder.icon_entry.setImageBitmap(dailyActivity.getBitmap());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dailyActivity.getFoodActivity()!=null) {
                    /**Intent foodActivityIntent = new Intent(context, EditFoodDetails.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("foodActivity", dailyActivity.getFoodActivity());
                    foodActivityIntent.putExtras(bundle);
                    ((Activity)context).startActivityForResult(foodActivityIntent,1);**/
                   cli.callEditFoodDetails(dailyActivity.getFoodActivity());
                }
                if(dailyActivity.getPhysicalActivity()!=null)
                {
                 /**   Intent foodActivityIntent = new Intent(context, EditPhysicalDetails.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("physicalActivity", dailyActivity.getPhysicalActivity());
                    foodActivityIntent.putExtras(bundle);
                    ((Activity)context).startActivityForResult(foodActivityIntent,1);**/
                cli.callEditPhysicalDetails(dailyActivity.getPhysicalActivity());
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dailyActivity.getFoodActivity()!=null)
                {
                    /**Intent foodActivityIntent=new Intent(context,DetailsFoodActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("foodActivity",dailyActivity.getFoodActivity());
                    foodActivityIntent.putExtras(bundle);
                    context.startActivity(foodActivityIntent);**/
                cli.callFoodDetails(dailyActivity.getFoodActivity());

                }if(dailyActivity.getPhysicalActivity()!=null)
                {
                    /**Intent foodActivityIntent=new Intent(context,DetailsPhysical.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("physicalActivity",dailyActivity.getPhysicalActivity());
                    foodActivityIntent.putExtras(bundle);
                    context.startActivity(foodActivityIntent);**/
                cli.callPhysicalDetails(dailyActivity.getPhysicalActivity());
                }

            }
        });

        //holder.icon_entry.setText(""+mDataSet.get(position).getUser_name().charAt(0));
    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class DailyActivityHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView activityName, dateTime;
        ImageView icon_entry;
        TextView edit;


        DailyActivityHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setPreventCornerOverlap(false);
            activityName = (TextView) itemView.findViewById(R.id.name_entry);
            dateTime = (TextView) itemView.findViewById(R.id.date_entry);
            icon_entry = (ImageView) itemView.findViewById(R.id.imageView4);
            edit = (TextView) itemView.findViewById(R.id.textView_darEdit);
           // context=itemView.getContext();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface CallbackInterface
    {
        public void callEditPhysicalDetails(PhysicalActivityDetails details);
        public void callEditFoodDetails(FoodActivityDetails details);
        public void callFoodDetails(FoodActivityDetails details);
        public void callPhysicalDetails(PhysicalActivityDetails details);
    }


}
