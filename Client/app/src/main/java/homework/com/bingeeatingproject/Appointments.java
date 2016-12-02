package homework.com.bingeeatingproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Appointments extends AppCompatActivity {
    SharedPreferences tokenSP;
    String savedToken = "";
    String userName = "";
    JSONObject jsonObject;
    StringEntity activityObject;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        listView=(ListView) findViewById(R.id.appointments);
        tokenSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        savedToken = tokenSP.getString("AuthToken", " ");
        userName = tokenSP.getString("userId", " ");
        new getAppointmentActivityDetails().execute(getJsonObject());
    }

    private StringEntity getJsonObject()
    {
        try {
            jsonObject = new JSONObject();
            jsonObject.put("username", userName);
            jsonObject.put("role", "user");
            Log.d("Demo", jsonObject.toString());
            activityObject = new StringEntity(jsonObject.toString());
            activityObject.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return activityObject;
    }

    class getAppointmentActivityDetails extends AsyncTask<StringEntity, Void, String> {
        String response;
        JSONObject respObj;
        Intent intent;

        @Override
        protected String doInBackground(StringEntity... params) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://52.26.156.48:3000/appointment/getAllAppointments");
            httpPost.setEntity(params[0]);
            httpPost.setHeader("Auth", savedToken);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                response = httpClient.execute(httpPost, responseHandler);
                Log.d("demo","AppointmentsList"+ response.toString());
                respObj = new JSONObject(response);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return respObj.toString();


        }

        @Override
        protected void onPostExecute(String s) {

               ArrayList<AppointmentDetails> details=getAppointments(s);
                AppointmentAdapter adapter = new AppointmentAdapter(Appointments.this, R.layout.row_item_appointment, details);
                listView.setAdapter(adapter);
                Toast.makeText(Appointments.this, "Retrieved Details Successfully", Toast.LENGTH_LONG).show();


        }
    }

    private ArrayList<AppointmentDetails> getAppointments(String s)
    {
        ArrayList<AppointmentDetails> activities=new ArrayList<>();
        try {
            JSONObject jsonObject1 = new JSONObject(s);

            JSONObject object = jsonObject1.getJSONObject("result");
            JSONArray activityArray = object.getJSONArray("supporterAppointments");

            for (int i = 0; i < activityArray.length(); i++) {
                JSONObject appointments=activityArray.getJSONObject(i);
                JSONArray appointmentsJSONObject=appointments.getJSONArray("userAppointments");
                for(int j=0;j<appointmentsJSONObject.length();j++) {
                    JSONObject object1=appointmentsJSONObject.getJSONObject(i);
                    AppointmentDetails activity = new AppointmentDetails();
                    String date = object1.getString("date").toString();
                    String time = object1.getString("time").toString();
                    activity.setStatus(object1.getString("status").toString());
                    //activity.setUser(activityArray.getJSONObject(i).getString("user").toString());
                    //activity.set_id(appointments.getString("_id").toString());
                    activity.setTime(time);
                    activity.setDate(date);
                    activities.add(activity);
                }

            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return activities;
    }

}
