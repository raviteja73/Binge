package homework.com.bingeeatingproject;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import java.util.Calendar;


public class WeeklyActivityFragment extends Fragment implements WeeklyActivityAdapter.CallbackInterface {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    StringEntity userObject;
    JSONObject jsonObject;
    SharedPreferences tokenSP;
    String savedToken="";
    ImageView camera;
    String userName;
    ImageView add;
    Calendar myCalendar = Calendar.getInstance();
    public WeeklyActivityFragment()
    {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_activity, container, false);
        add= (ImageView) view.findViewById(R.id.imageView_wadd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),AddWeeklyDetails.class);
                startActivity(intent);
            }
        });
        recyclerView=(RecyclerView)view.findViewById(R.id.wactivity_list);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        try {
            tokenSP = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
            savedToken = tokenSP.getString("AuthToken", " ");
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

        new GetWeeklyActivityDetails().execute(userObject);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void callEditWeeklyDetails(WeeklyActivity activity) {
        Intent foodActivityIntent=new Intent(this.getActivity().getApplicationContext(),EditWeeklyDetails.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("weeklyActivity",activity);
        foodActivityIntent.putExtras(bundle);
        startActivityForResult(foodActivityIntent,1);

    }

    @Override
    public void callWeeklyDetails(WeeklyActivity activity) {
        Intent foodActivityIntent=new Intent(this.getActivity().getApplicationContext(),DetailsWeeklyActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("weeklyActivity",activity);
        foodActivityIntent.putExtras(bundle);
        startActivity(foodActivityIntent);

    }

    class GetWeeklyActivityDetails extends AsyncTask<StringEntity, Void, String> {
        String response;
        JSONObject respObj;
        Intent intent;

        @Override
        protected String doInBackground(StringEntity... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://52.26.156.48:3000/activity/getWeeklyActivityLog");
            httpPost.setEntity(params[0]);
            httpPost.setHeader("Auth",savedToken);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                response = httpClient.execute(httpPost, responseHandler);

                respObj=new JSONObject(response);
                Log.d("demo","Weekly Activity List:"+respObj.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respObj.toString();
        }

        @Override
        protected void onPostExecute(String s) {

            ArrayList<WeeklyActivity> activities=getAllActivities(s);
            WeeklyActivityAdapter adapter = new WeeklyActivityAdapter(WeeklyActivityFragment.this,activities);
            recyclerView.setAdapter(adapter);
        }


    }

    public ArrayList<WeeklyActivity> getAllActivities(String s)
    {
        ArrayList<WeeklyActivity> activities= new ArrayList<>();
        try {
            JSONObject object=new JSONObject(s);
            JSONObject resultObject=object.getJSONObject("result");
            JSONArray responseArray=resultObject.getJSONArray("weeklyLog");

            for(int i=0;i<responseArray.length();i++)
            {
                WeeklyActivity activity=new WeeklyActivity();
                String weekNo=responseArray.getJSONObject(i).get("weekNumber").toString();
                activity.setWeekNo(weekNo);
                activity.setEvents(responseArray.getJSONObject(i).get("events").toString());
                activity.setGoodDays(responseArray.getJSONObject(i).get("goodDays").toString());
                activity.setgPhysicaldays(responseArray.getJSONObject(i).get("physicallyActiveDays").toString());
                activity.setFgoalReachedDays(responseArray.getJSONObject(i).get("fruitVegetableCount").toString());
                activity.setTotalNoOfBinges(responseArray.getJSONObject(i).getInt("binges"));
                activity.setStartDate(responseArray.getJSONObject(i).getString("startDate"));
                activity.setEndDate(responseArray.getJSONObject(i).getString("endDate"));
                activity.setTotalNoOfVo(responseArray.getJSONObject(i).getInt("weightControlUsage"));
                activity.setTotalWeight(responseArray.getJSONObject(i).getInt("weight"));
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
                new GetWeeklyActivityDetails().execute(userObject);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
