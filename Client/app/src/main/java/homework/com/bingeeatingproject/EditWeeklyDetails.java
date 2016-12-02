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
import android.widget.EditText;
import android.widget.SeekBar;
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

public class EditWeeklyDetails extends AppCompatActivity {

    EditText weekNo,events,goodDays,goalsReachedDays,gPhysicalDays;
    SeekBar binges,weight,count;
    Button edit ,delete;
    JSONObject jsonObject;
    StringEntity activityObject;
    SharedPreferences tokenSP;
    String savedToken = "";
    String userName = "";
    String weekNoText,eventsText,goodDaysText,goalsReachedText,gphysicalDaysText;
    int totalBinges,totalCount,totalWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_weekly_details);
        Bundle bundle=getIntent().getExtras();
        final WeeklyActivity details= (WeeklyActivity) bundle.get("weeklyActivity");

        weekNo=(EditText) findViewById(R.id.editText_edweekNo);
        goodDays=(EditText) findViewById(R.id.editText_edwgdays);
        goalsReachedDays=(EditText) findViewById(R.id.editText_edgoals);
        gPhysicalDays=(EditText) findViewById(R.id.editText_edphysical);
        binges=(SeekBar) findViewById(R.id.seekBar_edbinges);
        count=(SeekBar) findViewById(R.id.seekBar_edcount);
        weight=(SeekBar) findViewById(R.id.seekBar_edweight);
        edit=(Button) findViewById(R.id.button_ewdedit);
        delete=(Button) findViewById(R.id.button_ewddelete);
        events=(EditText)findViewById(R.id.editText_ewevents) ;

        weekNo.setText(details.getWeekNo());
        weekNo.setEnabled(false);
        goodDays.setText(details.getGoodDays());
        gPhysicalDays.setText(details.getgPhysicaldays());
        goalsReachedDays.setText(details.getFgoalReachedDays());
        binges.setProgress(details.getTotalNoOfBinges());
        totalWeight=details.getTotalWeight();
        totalBinges=details.getTotalNoOfBinges();
        totalCount=details.getTotalNoOfVo();
        count.setProgress(details.getTotalNoOfVo());
        weight.setProgress(details.getTotalWeight());
        events.setText(details.getEvents());
        tokenSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        savedToken = tokenSP.getString("AuthToken", " ");
        userName = tokenSP.getString("userId", " ");

        binges.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                totalBinges=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        count.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                totalCount=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        weight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                totalWeight=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                weekNoText=weekNo.getText().toString();
                eventsText=events.getText().toString();
                goodDaysText=goodDays.getText().toString();
                goalsReachedText=goalsReachedDays.getText().toString();
                gphysicalDaysText=gPhysicalDays.getText().toString();

                if(weekNoText!=null && weekNoText!=" " && eventsText!=null && eventsText!=" " && goodDaysText != null && goodDaysText!=" "
                        && goalsReachedText!=null && goalsReachedText !=" " && gphysicalDaysText!=null && gphysicalDaysText!=" "
                        && totalBinges!=0 && totalCount!=0 && totalWeight!=0)
                {
                    tokenSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    savedToken = tokenSP.getString("AuthToken", " ");
                    userName = tokenSP.getString("userId", " ");

                    try {
                    jsonObject = new JSONObject();
                    jsonObject.put("username", userName);
                    jsonObject.put("weekNumber", weekNoText);
                    jsonObject.put("binges", totalBinges);
                    jsonObject.put("weightControlUsage", totalCount);
                    jsonObject.put("weight", totalWeight);
                    jsonObject.put("goodDays", goodDaysText);
                    jsonObject.put("fruitVegetableCount", goalsReachedText);
                    jsonObject.put("physicallyActiveDays", gphysicalDaysText);
                    jsonObject.put("events", eventsText);
                        activityObject = new StringEntity(jsonObject.toString());
                        activityObject.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                        if (!jsonObject.equals("")) {
                            new EditWeeklyActivityDetails().execute(activityObject);
                        }
                } catch (JSONException e) {
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
                weekNoText=weekNo.getText().toString();
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("username", userName);
                    jsonObject.put("weekNumber", weekNoText);
                    Log.d("demo","delete"+jsonObject.toString());
                    activityObject = new StringEntity(jsonObject.toString());
                    activityObject.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    if (!jsonObject.equals("")) {
                        new DeleteWeeklyActivityDetails().execute(activityObject);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    class EditWeeklyActivityDetails extends AsyncTask<StringEntity, Void, String> {
        String response;
        JSONObject respObj;
        Intent intent;

        @Override
        protected String doInBackground(StringEntity... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://52.26.156.48:3000/activity/editWeeklyActivityLog");
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
                Toast.makeText(EditWeeklyDetails.this, "Saved Details Successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(EditWeeklyDetails.this, "There is an error while saving details", Toast.LENGTH_LONG).show();
            }

        }
    }

    class DeleteWeeklyActivityDetails extends AsyncTask<StringEntity, Void, String> {
        String response;
        JSONObject respObj;
        Intent intent;

        @Override
        protected String doInBackground(StringEntity... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://52.26.156.48:3000/activity/deleteWeeklyActivityLog");
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

                Toast.makeText(EditWeeklyDetails.this, "Deleted Details Successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(EditWeeklyDetails.this, "There is an error while saving details", Toast.LENGTH_LONG).show();
            }

        }
    }
}
