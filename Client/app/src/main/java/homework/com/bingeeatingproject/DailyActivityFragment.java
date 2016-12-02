package homework.com.bingeeatingproject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DailyActivityFragment extends android.support.v4.app.Fragment implements DailyActivityAdapter.CallbackInterface {

    StringEntity userObject;
    JSONObject jsonObject;
    SharedPreferences tokenSP;
    String savedToken="";
    ImageView physicalActivity;
    ImageView foodActivity;
    ImageView camera;
    String userName;
    Calendar myCalendar = Calendar.getInstance();
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ProgressDialog progressDialog;

    private FragmentActivity myContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_activity, container, false);
        physicalActivity= (ImageView) view.findViewById(R.id.imageView);
        foodActivity= (ImageView) view.findViewById(R.id.imageView2);
        camera=(ImageView) view.findViewById(R.id.imageView3);
        recyclerView=(RecyclerView)view.findViewById(R.id.dactivity_list);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(getActivity().getApplicationContext());
        progressDialog=new ProgressDialog(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        try {
            tokenSP = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
            savedToken = tokenSP.getString("AuthToken", " ");
            Log.d("demo","token"+savedToken);
            userName = tokenSP.getString("userId", " ");
            jsonObject = new JSONObject();
            jsonObject.put("username", userName);
            Log.d("demo",jsonObject.toString());
            userObject = new StringEntity(jsonObject.toString());
            userObject.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        }catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new GetDailyActivityDetails().execute(userObject);


       final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }


        };

        camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent logActivityIntent=new Intent(getActivity(),QuickLog.class);
                startActivity(logActivityIntent);
            }
        });
        physicalActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent physicalActivityIntent=new Intent(getActivity(),PhysicalActivity.class);
                     startActivityForResult(physicalActivityIntent,1);

            }
        });
        foodActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent foodActivityIntent=new Intent(getActivity(),FoodActivity.class);
                startActivityForResult(foodActivityIntent,1);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void callEditPhysicalDetails(PhysicalActivityDetails details) {
        Intent foodActivityIntent = new Intent(this.getActivity().getApplicationContext(), EditPhysicalDetails.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("physicalActivity", details);
        foodActivityIntent.putExtras(bundle);
        startActivityForResult(foodActivityIntent,1);
    }

    @Override
    public void callEditFoodDetails(FoodActivityDetails details) {
        Intent foodActivityIntent = new Intent(this.getActivity().getApplicationContext(), EditFoodDetails.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("foodActivity", details);
        foodActivityIntent.putExtras(bundle);
        startActivityForResult(foodActivityIntent,1);
    }

    @Override
    public void callFoodDetails(FoodActivityDetails details) {
        Intent foodActivityIntent=new Intent(this.getActivity().getApplicationContext(),DetailsFoodActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("foodActivity",details);
        foodActivityIntent.putExtras(bundle);
        startActivity(foodActivityIntent);

    }

    @Override
    public void callPhysicalDetails(PhysicalActivityDetails details) {
        Intent foodActivityIntent=new Intent(this.getActivity().getApplicationContext(),DetailsPhysical.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("physicalActivity",details);
        foodActivityIntent.putExtras(bundle);
        startActivity(foodActivityIntent);

    }

    class GetDailyActivityDetails extends AsyncTask<StringEntity, Void, String> {
        String response;
        JSONObject respObj;
        Intent intent;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

           /** progressDialog.setMessage("Loading");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();**/

        }

        @Override
        protected String doInBackground(StringEntity... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://52.26.156.48:3000/activity/getDailyActivityLog");
            httpPost.setEntity(params[0]);
            httpPost.setHeader("Auth",savedToken);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                response = httpClient.execute(httpPost, responseHandler);
                Log.d("demo","Response:"+ response.toString());
                respObj=new JSONObject(response);
                Log.d("demo","List of daily Activities:"+ respObj.toString());
             } catch (IOException e) {
             e.printStackTrace();
             } catch (JSONException e) {
             e.printStackTrace();
             }
             return respObj.toString();
        }

        @Override
        protected void onPostExecute(String s) {

              ArrayList<DailyActivity> activities=getAllActivities(s);
           // progressDialog.dismiss();
             DailyActivityAdapter adapter = new DailyActivityAdapter(DailyActivityFragment.this,activities);
             recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }


    }

    public ArrayList<DailyActivity> getAllActivities(String s)
    {
        ArrayList<DailyActivity> activities= new ArrayList<>();
        try {
            JSONObject jsonObject1=new JSONObject(s);

            JSONObject object=jsonObject1.getJSONObject("result");
            JSONArray pactivityArray=object.getJSONArray("physicalActivityLog");
            JSONArray factivityArray=object.getJSONArray("foodActivityLog");
            for(int i=0;i<pactivityArray.length();i++)
            {
                DailyActivity activity=new DailyActivity();
                activity.setActivityName("Physical activity");
                String date=pactivityArray.getJSONObject(i).get("date").toString();
                String time=pactivityArray.getJSONObject(i).get("time").toString();
                activity.setDateTime(date +" " +time);
                activity.setFoodActivity(null);
                PhysicalActivityDetails physicalActivity=new PhysicalActivityDetails();
                physicalActivity.set_id(pactivityArray.getJSONObject(i).get("_id").toString());
                physicalActivity.setActivity(pactivityArray.getJSONObject(i).get("activity").toString());
                physicalActivity.setDate(date);
                physicalActivity.setTime(time);
                physicalActivity.setDuration(pactivityArray.getJSONObject(i).get("duration").toString());
                physicalActivity.setLoggedAt(pactivityArray.getJSONObject(i).get("loggedAt").toString());
                Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noimageavailable);
                activity.setBitmap(imageBitmap);
                activity.setPhysicalActivity(physicalActivity);
                activities.add(activity);
            }
            for(int i=0;i<factivityArray.length();i++)
            {
                DailyActivity activity=new DailyActivity();
                activity.setActivityName("Food activity");
                String date=factivityArray.getJSONObject(i).get("date").toString();
                String time=factivityArray.getJSONObject(i).get("time").toString();
                activity.setDateTime(date +" " +time);
                //byte[] decodedString = Base64.decode(factivityArray.getJSONObject(i).get("image").toString(), Base64.DEFAULT);
                //Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                //activity.setBitmap(decodedByte);
                Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noimageavailable);
                activity.setBitmap(imageBitmap);
                activity.setPhysicalActivity(null);
                FoodActivityDetails details=new FoodActivityDetails();
                details.setNotes(factivityArray.getJSONObject(i).get("notes").toString());
                details.setImage(imageBitmap);
                details.setLexative(factivityArray.getJSONObject(i).getBoolean("laxating"));
                details.setVomiting(factivityArray.getJSONObject(i).getBoolean("vomiting"));
                details.setBinge(factivityArray.getJSONObject(i).getBoolean("binge"));
                details.setDrinks(factivityArray.getJSONObject(i).getString("drinks"));
                details.setFood(factivityArray.getJSONObject(i).getString("food"));
                details.setPlace(factivityArray.getJSONObject(i).getString("place"));
                details.setDate(date);
                details.setTime(time);
                details.setLoggedAt(factivityArray.getJSONObject(i).getString("loggedAt"));
                details.set_id(factivityArray.getJSONObject(i).getString("_id"));
                activity.setFoodActivity(details);
                activities.add(activity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return activities;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                new GetDailyActivityDetails().execute(userObject);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
