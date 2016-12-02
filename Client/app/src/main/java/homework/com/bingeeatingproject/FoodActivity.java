package homework.com.bingeeatingproject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class FoodActivity extends AppCompatActivity {
    EditText ftime;
    EditText fdate;
    EditText place;
    EditText food;EditText fqty;EditText fnotes;
    EditText drink;EditText dqty;
    Switch consumeDrinks,bingeSwitch,vomitingSwitch,lexativeSwitch;
    Button proceed;
    int f,d;
    Calendar myCalendar;
    DatePickerDialog dialog;
    int fyear,day,month;
    String myFormat = "MM/dd/yy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    TextView errorTextView;
    SharedPreferences tokenSP;
    String savedToken="";String userName="";
    Button saveFoodActivityDetails;
    JSONObject jsonObject;StringEntity factivityObject;
    String factivityTime,factivityDate,fplace,activityFood,foodqty,activityDrink,drinkqty,notes;
    Object foodjson,foodqtyjson,drinkjson,drinkqtyjson;
    boolean binge,vomiting,lexative;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;ByteArrayOutputStream baos; String encodedImage;
    byte[] b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noimageavailable);
        baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        b = baos.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        ftime = (EditText) findViewById(R.id.editText_ftime);
        fnotes = (EditText) findViewById(R.id.editText_fNotes);
        fdate = (EditText) findViewById(R.id.editText_fdate);
        place = (EditText) findViewById(R.id.editText_fplace);
        food = (EditText) findViewById(R.id.editText_food);
        drink = (EditText) findViewById(R.id.editText_drink);
        bingeSwitch = (Switch) findViewById(R.id.switch_binge);
        vomitingSwitch = (Switch) findViewById(R.id.switch_vomiting);
        lexativeSwitch = (Switch) findViewById(R.id.switch_lexative);
        saveFoodActivityDetails = (Button) findViewById(R.id.button_fproceed);
        errorTextView = (TextView) findViewById(R.id.textView_ferror);

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                fyear = year;
                month = monthOfYear;
                day = dayOfMonth;
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                fdate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        fdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog = new DatePickerDialog(FoodActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });

        ftime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                errorTextView.setVisibility(View.INVISIBLE);
                saveFoodActivityDetails.setEnabled(true);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(FoodActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar datetime = Calendar.getInstance();
                        Calendar c = Calendar.getInstance();
                        datetime.set(Calendar.YEAR, fyear);
                        datetime.set(Calendar.MONTH, month);
                        datetime.set(Calendar.DAY_OF_MONTH, day);
                        datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        datetime.set(Calendar.MINUTE, selectedMinute);
                        if (datetime.getTimeInMillis() > c.getTimeInMillis()) {
                            errorTextView.setText("Please select correct time");
                            errorTextView.setVisibility(View.VISIBLE);
                            saveFoodActivityDetails.setEnabled(false);
                        } else {
//            it's before current'
                        }
                        ftime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                //mTimePicker.
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

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

        saveFoodActivityDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activityDrink = drink.getText().toString();
                activityFood =  food.getText().toString() ;
                factivityTime = ftime.getText().toString();
                factivityDate = fdate.getText().toString();
                fplace = place.getText().toString();
                notes=fnotes.getText().toString();

                if (activityFood != null && activityFood != " " && activityDrink!=null && activityDrink!=" " &&  fplace != null &&
                        fplace != " " && factivityTime != null &&
                        factivityTime != " " && factivityDate != null && factivityDate != " ") {
                    tokenSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    savedToken = tokenSP.getString("AuthToken", " ");
                    userName = tokenSP.getString("userId", " ");
                    if(notes==null || notes==" ")
                    {
                        notes="none";
                    }
                    getJsonObject(userName, factivityTime, factivityDate, fplace,notes, activityFood, activityDrink, binge, vomiting, lexative);
                    if (!jsonObject.equals("")) {
                        Log.d("demo",jsonObject.toString());
                        new saveFoodActivityDetails().execute(factivityObject);
                    }
                } else {
                    Toast.makeText(FoodActivity.this, "Please enter all the Details", Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void getJsonObject(String id,String time,String date,String place,String notes,String food,String drink,boolean binge,boolean vomiting,
                                boolean lexative)
    {
        try {
            jsonObject = new JSONObject();
            jsonObject.put("username", id);
            jsonObject.put("date", date);
            jsonObject.put("time", time);
            jsonObject.put("place", place);
            jsonObject.put("notes", notes);
            jsonObject.put("activityType", "food");
            jsonObject.put("food", food);
            jsonObject.put("drinks", drink);
            jsonObject.put("image", encodedImage);
            jsonObject.put("binge", binge);
            jsonObject.put("vomiting", vomiting);
            jsonObject.put("laxating", lexative);
            factivityObject = new StringEntity(jsonObject.toString());
            factivityObject.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    class saveFoodActivityDetails extends AsyncTask<StringEntity, Void, String> {
        String response;
        JSONObject respObj;
        Intent intent;

        @Override
        protected String doInBackground(StringEntity... params) {
            HttpClient httpClient = new DefaultHttpClient();
             HttpPost httpPost = new HttpPost("http://52.26.156.48:3000/activity/dailyActivityLog");
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
                Toast.makeText(FoodActivity.this, "Saved Details Successfully", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(FoodActivity.this, "There is an error while saving details", Toast.LENGTH_LONG).show();
            }

        }
    }

    }

