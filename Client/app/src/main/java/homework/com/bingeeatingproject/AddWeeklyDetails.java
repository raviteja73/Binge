package homework.com.bingeeatingproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
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

public class AddWeeklyDetails extends AppCompatActivity {
    EditText weekNo;
    EditText events;
    EditText goodDays;
    EditText goalsReachedDays;EditText physicalActivityDays;
    Button saveWeeklyDetails;
    SeekBar totalBinges,totalvo,weight;
    JSONObject jsonObject;StringEntity wactivityObject;
    SharedPreferences tokenSP;
    String savedToken="";String userName="";
    String weeksNo,eventText,goodDaysText,goalsReachedText,pactivityDaysText;
    int totalNoOfBinges,totalNoOfVo,totalWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weekly_details);
        weekNo = (EditText) findViewById(R.id.editText_weekno);
        goodDays = (EditText) findViewById(R.id.editText_gdays);
        goalsReachedDays = (EditText) findViewById(R.id.editText_goal);
        physicalActivityDays = (EditText) findViewById(R.id.editText_gPhysical);
        events = (EditText) findViewById(R.id.editText_events);
        totalBinges = (SeekBar) findViewById(R.id.seekBar_tbinges);
        totalvo = (SeekBar) findViewById(R.id.seekBar_tcount);
        weight = (SeekBar) findViewById(R.id.seekBar_tweight);
        saveWeeklyDetails = (Button) findViewById(R.id.button_addwproceed);
        totalBinges.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    totalNoOfBinges=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        totalvo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                totalNoOfVo=i;
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
        saveWeeklyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weeksNo=weekNo.getText().toString();
                eventText=events.getText().toString();
                goodDaysText=goodDays.getText().toString();
                goalsReachedText=goalsReachedDays.getText().toString();
                pactivityDaysText=physicalActivityDays.getText().toString();
                if(weeksNo!=null && weeksNo!=" " && eventText!=null && eventText!=" " && goodDaysText != null && goodDaysText!=" "
                        && goalsReachedText!=null && goalsReachedText !=" " && pactivityDaysText!=null && pactivityDaysText!=" "
                        && totalNoOfBinges!=0 && totalNoOfVo!=0 && totalWeight!=0)
                {
                    tokenSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    savedToken = tokenSP.getString("AuthToken", " ");
                    userName = tokenSP.getString("userId", " ");
                    getJsonObject(userName, weeksNo, Integer.toString(totalNoOfBinges),Integer.toString(totalNoOfVo),Integer.toString(totalWeight),goodDaysText,goalsReachedText, pactivityDaysText, eventText);
                    if (!jsonObject.equals("")) {
                        Log.d("demo",jsonObject.toString());
                        new saveWeeklyActivityDetails().execute(wactivityObject);
                    }
                } else {
                    Toast.makeText(AddWeeklyDetails.this, "Please enter all the Details", Toast.LENGTH_LONG);
                }
                }


        });

    }
    private void getJsonObject(String userName,String weekNo,String totalBinges,String goalsVO,
                               String weight,String goodDays,String reached,String pactivity,String events)
    {
        try {
            jsonObject = new JSONObject();
            jsonObject.put("username", userName);
            jsonObject.put("weekNumber", weekNo);
            jsonObject.put("binges", totalBinges);
            jsonObject.put("weightControlUsage", goalsVO);
            jsonObject.put("weight", weight);
            jsonObject.put("goodDays", goodDays);
            jsonObject.put("fruitVegetableCount", reached);
            jsonObject.put("physicallyActiveDays", pactivity);
            jsonObject.put("events", events);
            wactivityObject = new StringEntity(jsonObject.toString());
            wactivityObject.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    class saveWeeklyActivityDetails extends AsyncTask<StringEntity, Void, String> {
        String response;
        JSONObject respObj;
        Intent intent;

        @Override
        protected String doInBackground(StringEntity... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://52.26.156.48:3000/activity/weeklyActivityLog");
            httpPost.setEntity(params[0]);
            httpPost.setHeader("Auth",savedToken);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                response = httpClient.execute(httpPost, responseHandler);
                Log.d("app",response.toString());
                respObj=new JSONObject(response);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
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
                Toast.makeText(AddWeeklyDetails.this, "Saved Details Successfully", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(AddWeeklyDetails.this, "There is an error while saving details", Toast.LENGTH_LONG).show();
            }

        }
    }
}
