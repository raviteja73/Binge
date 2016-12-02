package homework.com.bingeeatingproject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PhysicalActivity extends AppCompatActivity {
    EditText time;
    EditText pdate;
    EditText name;
    EditText timePerformed;
    Button savePhysicalActivityDetails;
    CustomDateTimePicker custom;
    String noOfMinutes, physicalActivityname, activityTime, activityDate;
    JSONObject jsonObject;
    StringEntity activityObject;
    Calendar myCalendar;
    DatePickerDialog dialog;
    String myFormat = "MM/dd/yy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    TextView errorTextView;
    int cyear, month, day;
    SharedPreferences tokenSP;
    String savedToken = "";
    String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical);
        time = (EditText) findViewById(R.id.editText_ptime);
        pdate = (EditText) findViewById(R.id.editText_pdate);
        name = (EditText) findViewById(R.id.editText_paName);
        errorTextView = (TextView) findViewById(R.id.textView_pe);
        timePerformed = (EditText) findViewById(R.id.editText_noOfMinutes);
        savePhysicalActivityDetails = (Button) findViewById(R.id.button_proceedp);
        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                cyear = year;
                month = monthOfYear;
                day = dayOfMonth;
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                pdate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        pdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog = new DatePickerDialog(PhysicalActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                errorTextView.setVisibility(View.INVISIBLE);
                savePhysicalActivityDetails.setEnabled(true);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(PhysicalActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar datetime = Calendar.getInstance();
                        Calendar c = Calendar.getInstance();
                        datetime.set(Calendar.YEAR, cyear);
                        datetime.set(Calendar.MONTH, month);
                        datetime.set(Calendar.DAY_OF_MONTH, day);
                        datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        datetime.set(Calendar.MINUTE, selectedMinute);
                        if (datetime.getTimeInMillis() > c.getTimeInMillis()) {
                            errorTextView.setText("Please select correct time");
                            errorTextView.setVisibility(View.VISIBLE);
                            savePhysicalActivityDetails.setEnabled(false);
                        } else {
//            it's before current'
                        }
                        time.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                //mTimePicker.
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        savePhysicalActivityDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noOfMinutes = timePerformed.getText().toString();
                physicalActivityname = name.getText().toString();
                activityTime = time.getText().toString();
                activityDate = pdate.getText().toString();
                if (noOfMinutes != null && noOfMinutes != " " && physicalActivityname != null &&
                        physicalActivityname != " " && activityTime != null &&
                        activityTime != " " && activityDate != null && activityDate != " ") {
                    tokenSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    savedToken = tokenSP.getString("AuthToken", " ");
                    userName = tokenSP.getString("userId", " ");
                    getJsonObject(userName, activityTime, activityDate, physicalActivityname, noOfMinutes);
                    if (!jsonObject.equals("")) {
                        new savePhysicalActivityDetails().execute(activityObject);
                    }
                } else {
                    Toast.makeText(PhysicalActivity.this, "Please enter all the Details", Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void getJsonObject(String id, String time, String date, String name, String minutes) {
        try {
            jsonObject = new JSONObject();
            jsonObject.put("username", userName);
            jsonObject.put("date", date);
            jsonObject.put("time", time);
            jsonObject.put("activityType", "physical");
            jsonObject.put("activity", name);
            jsonObject.put("duration", minutes);
            Log.d("Demo", jsonObject.toString());
            activityObject = new StringEntity(jsonObject.toString());
            activityObject.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    class savePhysicalActivityDetails extends AsyncTask<StringEntity, Void, String> {
        String response;
        JSONObject respObj;
        Intent intent;

        @Override
        protected String doInBackground(StringEntity... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://52.26.156.48:3000/activity/dailyActivityLog");
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
                finish();
                Toast.makeText(PhysicalActivity.this, "Saved Details Successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PhysicalActivity.this, "There is an error while saving details", Toast.LENGTH_LONG).show();
            }

        }
    }
}

