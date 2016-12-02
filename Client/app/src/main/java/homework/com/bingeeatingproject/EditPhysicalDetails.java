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
import android.widget.TextView;
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

public class EditPhysicalDetails extends AppCompatActivity {
    EditText date,time,duration,activity;
    Button edit ,delete;
    JSONObject jsonObject;
    StringEntity activityObject;
    SharedPreferences tokenSP;
    String savedToken = "";
    String userName = "";
    String activityText,durationText,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_physical_details);
        Bundle bundle=getIntent().getExtras();
        final PhysicalActivityDetails details= (PhysicalActivityDetails) bundle.get("physicalActivity");

        date=(EditText) findViewById(R.id.editText_epddate);
        time=(EditText) findViewById(R.id.editText_eptime);
        duration=(EditText) findViewById(R.id.editText_epdduration);
        activity=(EditText) findViewById(R.id.editText_epdactvity);
        edit=(Button) findViewById(R.id.button_epdedit);
        delete=(Button) findViewById(R.id.button_epdelete);

        date.setText(details.getDate());
        date.setEnabled(false);
        time.setText(details.getTime());
        time.setEnabled(false);
        activity.setText(details.getActivity());
        duration.setText(details.getDuration());
        tokenSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        savedToken = tokenSP.getString("AuthToken", " ");
        userName = tokenSP.getString("userId", " ");

        id=details.get_id();

        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                activityText=activity.getText().toString();
                durationText=duration.getText().toString();
                if(activityText!=null && activityText!=" "&& durationText!=null && durationText!=" ")
                {
                    try {

                        jsonObject = new JSONObject();
                        jsonObject.put("username", userName);
                        jsonObject.put("date", date.getText().toString());
                        jsonObject.put("time", time.getText().toString());
                        jsonObject.put("activityType", "physical");
                        jsonObject.put("activity", activityText);
                        jsonObject.put("duration", durationText);
                        Log.d("Demo", jsonObject.toString());
                        activityObject = new StringEntity(jsonObject.toString());
                        activityObject.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                        if (!jsonObject.equals("")) {
                            new EditPhysicalActivityDetails().execute(activityObject);
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
                    jsonObject.put("activityType", "physical");
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

    class EditPhysicalActivityDetails extends AsyncTask<StringEntity, Void, String> {
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
                Toast.makeText(EditPhysicalDetails.this, "Saved Details Successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(EditPhysicalDetails.this, "There is an error while saving details", Toast.LENGTH_LONG).show();
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
                //returnIntent.putExtra("result",result);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                Toast.makeText(EditPhysicalDetails.this, "Saved Details Successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(EditPhysicalDetails.this, "There is an error while saving details", Toast.LENGTH_LONG).show();
            }

        }
    }
}
