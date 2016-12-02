package homework.com.bingeeatingproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class QuickLog extends AppCompatActivity {


    Button addPicture,saveLog;
    ImageView capturedImage;
    Object[] foodArray,drinkArray;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;ByteArrayOutputStream baos; String encodedImage;
    byte[] b;
    JSONObject jsonObject;StringEntity foodActivityObject;
    SharedPreferences tokenSP;
    String savedToken="";String userName="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_log);


        addPicture=(Button) findViewById(R.id.button_addPicture);
        saveLog=(Button) findViewById(R.id.button_saveLog);
        capturedImage=(ImageView) findViewById(R.id.cameraImage);

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check permission for CAMERA
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Check Permissions Now
                    // Callback onRequestPermissionsResult interceptado na Activity MainActivity
                    ActivityCompat.requestPermissions(QuickLog.this,
                            new String[]{Manifest.permission.CAMERA},
                            QuickLog.REQUEST_IMAGE_CAPTURE);
                } else {
                    // permission has been granted, continue as usual

                    Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(captureIntent,REQUEST_IMAGE_CAPTURE );
                }

            }
        });

        saveLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(imageBitmap==null)
                {
                    imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noimageavailable);
                }
                 baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                 b = baos.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                Calendar cal=Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String date = df.format(cal.getTime());
                SimpleDateFormat datef = new SimpleDateFormat("HH::mm");
                datef.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
                String localTime = datef.format(cal.getTime());

                    tokenSP= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    savedToken=tokenSP.getString("AuthToken"," ");
                    userName=tokenSP.getString("userId"," ");
                  foodArray=new Object[2];
                   drinkArray=new Object[2];
                   Object foodObject="none"; Object foodqtyObj="none"; Object drinkObject="none"; Object drinkQtyObject="none";
                   foodArray[0]="none";
                   foodArray[1]="none";
                drinkArray[0]="none";
                drinkArray[1]="none";
                    getJsonObject(userName,localTime,date,foodArray,drinkArray,encodedImage);
                    if (!jsonObject.equals("")) {
                        new saveLogActivityDetails().execute(foodActivityObject);
                    }


            }
        });

    }
    private void getJsonObject(String id,String time,String date,Object[] food,Object[] drink,String image)
    {
        try {
            jsonObject = new JSONObject();
            jsonObject.put("username", id);
            jsonObject.put("date", date);
            jsonObject.put("time", time);
            jsonObject.put("place", " ");
            jsonObject.put("activityType", "food");
            jsonObject.put("food", food);
            jsonObject.put("drinks", drink);
            jsonObject.put("image", image);
            jsonObject.put("binge", false);
            jsonObject.put("vomiting", false);
            jsonObject.put("laxating", false);
            foodActivityObject = new StringEntity(jsonObject.toString());
            foodActivityObject.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            capturedImage.setImageBitmap(imageBitmap);
        }
    }

    class saveLogActivityDetails extends AsyncTask<StringEntity, Void, String> {
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
            if(respObj!=null)
            {
                return "success";
            }else {
                Toast.makeText(QuickLog.this, "There is some error in Login", Toast.LENGTH_LONG).show();
            }
            return " ";
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("success")) {
                finish();
                Toast.makeText(QuickLog.this, "Saved Image Successfully", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(QuickLog.this, "There is an error while saving Image", Toast.LENGTH_LONG).show();
            }
        }
    }
}
