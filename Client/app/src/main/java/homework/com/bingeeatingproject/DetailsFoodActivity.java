package homework.com.bingeeatingproject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class DetailsFoodActivity extends Activity {
    ImageView image;
    TextView date,time,food,drink,place,notes;
    Switch binge,lexative,vomiting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_food);
        Bundle bundle=getIntent().getExtras();
        FoodActivityDetails details= (FoodActivityDetails) bundle.get("foodActivity");
        image=(ImageView) findViewById(R.id.imageView5_fad);
        date=(TextView) findViewById(R.id.textView_fddate);
        time=(TextView) findViewById(R.id.textView_fdtime);
        food=(TextView) findViewById(R.id.textView_fdfood);
        drink=(TextView) findViewById(R.id.textView_fddrink);
        place=(TextView) findViewById(R.id.textView_fdplace);
        notes=(TextView) findViewById(R.id.textView_fdnotes);
        binge=(Switch) findViewById(R.id.switch1_fdbinge);
        lexative=(Switch) findViewById(R.id.switch_fdlexative);
        vomiting=(Switch) findViewById(R.id.switch_fdvomiting);

        image.setImageBitmap(details.getImage());
        date.setText(details.getDate());
        time.setText(details.getTime());
        food.setText(details.getFood());
        drink.setText(details.getDrinks());
        place.setText(details.getPlace());
        notes.setText(details.getNotes());
        binge.setChecked(details.isBinge());
        lexative.setChecked(details.isLexative());
        vomiting.setChecked(details.isVomiting());


    }
}
