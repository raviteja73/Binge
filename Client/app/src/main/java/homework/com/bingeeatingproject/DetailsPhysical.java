package homework.com.bingeeatingproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsPhysical extends AppCompatActivity {

    TextView date,time,duration,activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_physical);
        Bundle bundle=getIntent().getExtras();
        PhysicalActivityDetails details= (PhysicalActivityDetails) bundle.get("physicalActivity");

        date=(TextView) findViewById(R.id.textView_pddate);
        time=(TextView) findViewById(R.id.textView_pdtime);
        activity=(TextView) findViewById(R.id.textView_pdactivty);
        duration=(TextView) findViewById(R.id.textView_pdactivty);

        date.setText(details.getDate());
        time.setText(details.getTime());
        activity.setText(details.getActivity());
        duration.setText(details.getDuration());


    }
}
