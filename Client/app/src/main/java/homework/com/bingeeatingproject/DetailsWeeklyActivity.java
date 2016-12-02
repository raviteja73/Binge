package homework.com.bingeeatingproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsWeeklyActivity extends AppCompatActivity {

    TextView weekno,binges,count,weight,goodDays,goalsReachedDays,goodPhysicalActivity,events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_weekly);

        Bundle bundle=getIntent().getExtras();
        WeeklyActivity details= (WeeklyActivity) bundle.get("weeklyActivity");

        weekno=(TextView) findViewById(R.id.textView_wdweekno);
        binges=(TextView) findViewById(R.id.textView_wdtbinges);
        count=(TextView) findViewById(R.id.textView_wdtotalCount);
        weight=(TextView) findViewById(R.id.textView_wdweight);
        goodDays=(TextView) findViewById(R.id.textView_wdgooddays);
        goalsReachedDays=(TextView) findViewById(R.id.textView_wdgoals);
        goodPhysicalActivity=(TextView) findViewById(R.id.textView_wdgphysical);
        events=(TextView) findViewById(R.id.textView_wdevents);

        weekno.setText(details.getWeekNo());
        weekno.setEnabled(false);
        binges.setText(String.valueOf(details.getTotalNoOfBinges()));
        count.setText(String.valueOf(details.getTotalNoOfVo()));
        weight.setText(String.valueOf(details.getTotalWeight()));
        goodDays.setText(details.getGoodDays());
        goalsReachedDays.setText(details.getFgoalReachedDays());
        goodPhysicalActivity.setText(details.getgPhysicaldays());
        events.setText(details.getEvents());
    }
}
