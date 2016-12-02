package homework.com.bingeeatingproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class EditFoodDetails extends AppCompatActivity {
    EditText date,time,food,place,notes,drink;
    Button edit ,delete;
    ImageView imageView;
    Switch bingeSwitch,vomitingSwitch,lexativeSwitch;
    boolean binge,vomiting,lexative;
    JSONObject jsonObject;
    StringEntity activityObject;
    SharedPreferences tokenSP;
    String savedToken = "";
    String userName = "";
    String foodText,drinkText,placeText,notesText,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_details);

        Bundle bundle=getIntent().getExtras();
        final FoodActivityDetails details= (FoodActivityDetails) bundle.get("foodActivity");

        date=(EditText) findViewById(R.id.editText_efddate);
        time=(EditText) findViewById(R.id.editText_efdtime);
        food=(EditText) findViewById(R.id.editText_efdfood);
        place=(EditText) findViewById(R.id.editText_efdplace);
        drink=(EditText) findViewById(R.id.editText_efddrink);
        notes=(EditText) findViewById(R.id.editText_efdnotes);
        edit=(Button) findViewById(R.id.button_efdedit);
        delete=(Button) findViewById(R.id.button_efddelete);
        imageView=(ImageView)findViewById(R.id.imageView_efd);
        bingeSwitch=(Switch)findViewById(R.id.switch_efdbinge);
        vomitingSwitch=(Switch)findViewById(R.id.switch_efdvomiting);
        lexativeSwitch=(Switch)findViewById(R.id.switch_efdlexative);


        date.setText(details.getDate());
        date.setEnabled(false);
        time.setText(details.getTime());
        time.setEnabled(false);
        food.setText(details.getFood());
        place.setText(details.getPlace());
        drink.setText(details.getDrinks());
        notes.setText(details.getNotes());
        imageView.setImageBitmap(details.getImage());
        tokenSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        savedToken = tokenSP.getString("AuthToken", " ");
        userName = tokenSP.getString("userId", " ");
        id=details.get_id();
        bingeSwitch.setChecked(details.isBinge());
        vomitingSwitch.setChecked(details.isVomiting());
        lexativeSwitch.setChecked(details.isLexative());
        bingeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                binge = b;
            }
        });
        vomitingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                vomiting = b;
            }
        });
        lexativeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                lexative = b;
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                    foodText=food.getText().toString();
                placeText=place.getText().toString();
                drinkText=drink.getText().toString();
                notesText=notes.getText().toString();
                if(foodText!=null && foodText!=" "&& placeText!=null && placeText!=" "&& drinkText!=null && drinkText!=" "
                        && notesText!=null && notesText!=" ")
                {
                    try {

                        jsonObject = new JSONObject();
                        jsonObject.put("username", userName);
                        jsonObject.put("date", date.getText().toString());
                        jsonObject.put("time", time.getText().toString());
                        jsonObject.put("place", placeText);
                        jsonObject.put("notes", notesText);
                        jsonObject.put("activityType", "food");
                        jsonObject.put("food", foodText);
                        jsonObject.put("drinks", drinkText);
                        jsonObject.put("image", details.getImage());
                        jsonObject.put("binge", binge);
                        jsonObject.put("vomiting", vomiting);
                        jsonObject.put("laxating", lexative);
                        Log.d("demo","EF"+jsonObject.toString());
                        activityObject = new StringEntity(jsonObject.toString());
                        activityObject.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                        if (!jsonObject.equals("")) {
                            new EditFoodActivityDetails().execute(activityObject);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("username", userName);
                    jsonObject.put("date", date.getText().toString());
                    jsonObject.put("time", time.getText().toString());
                    jsonObject.put("activityType", "food");
                    Log.d("demo","delete"+jsonObject.toString());
                    activityObject = new StringEntity(jsonObject.toString());
                    activityObject.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    if (!jsonObject.equals("")) {
                        new DeletePhysicalActivityDetails().execute(activityObject);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    class EditFoodActivityDetails extends AsyncTask<StringEntity, Void, String> {
        String response;
        JSONObject respObj;
        Intent intent;

        @Override
        protected String doInBackground(StringEntity... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://52.26.156.48:3000/activity/editDailyActivityLog");
            httpPost.setEntity(params[0]);
            httpPost.setHeader("Auth", savedToken);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                response = httpClient.execute(httpPost, responseHandler);
                Log.d("app", response.toString());
                respObj = new JSONObject(response);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "success";


        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("success")) {
                Intent returnIntent = new Intent();
                //returnIntent.putExtra("result",result);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                Toast.makeText(EditFoodDetails.this, "Saved Details Successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(EditFoodDetails.this, "There is an error while saving details", Toast.LENGTH_LONG).show();
            }

        }
    }

    class DeletePhysicalActivityDetails extends AsyncTask<StringEntity, Void, String> {
        String response;
        JSONObject respObj;
        Intent intent;

        @Override
        protected String doInBackground(StringEntity... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://52.26.156.48:3000/activity/deleteDailyActivityLog");
            httpPost.setEntity(params[0]);
            httpPost.setHeader("Auth", savedToken);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                response = httpClient.execute(httpPost, responseHandler);
                Log.d("app", response.toString());
                respObj = new JSONObject(response);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "success";


        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("success")) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                Toast.makeText(EditFoodDetails.this, "Saved Details Successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(EditFoodDetails.this, "There is an error while saving details", Toast.LENGTH_LONG).show();
            }

        }
    }

}
