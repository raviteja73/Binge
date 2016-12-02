package homework.com.bingeeatingproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {

    StringEntity userObject;
    JSONObject jsonObject;
    SharedPreferences tokenSP;
    String savedToken="";
    EditText editTextUserName;
    EditText editTextPassword;
    Button login;
    String userName,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextUserName= (EditText) findViewById(R.id.editTextUsername);
        editTextPassword= (EditText) findViewById(R.id.editTextPassword);
        login= (Button) findViewById(R.id.buttonLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName=editTextUserName.getText().toString();
                password=editTextPassword.getText().toString();
                if (userName != null && userName != " " && password != null && password != " ") {
                       getJsonObject(userName,password);
                    if (!jsonObject.equals("")) {
                        new GetUserDetails().execute(userObject);
                    }

                }else
                {
                    Toast.makeText(MainActivity.this,"Please enter the Login credentials",Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void getJsonObject(String userName,String password)
    {
        try {
            jsonObject = new JSONObject();
            jsonObject.put("username", userName);
            jsonObject.put("password", password);
            userObject = new StringEntity(jsonObject.toString());
            userObject.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    class GetUserDetails extends AsyncTask<StringEntity, Void, String> {
        String response;
        JSONObject respObj;
        Intent intent;

        @Override
        protected String doInBackground(StringEntity... params) {
          HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://52.26.156.48:3000/user/validateUser");
            httpPost.setEntity(params[0]);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                response = httpClient.execute(httpPost, responseHandler);
                Log.d("app", response.toString());
                respObj=new JSONObject(response);
                Log.d("demo",respObj.toString());
                tokenSP= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = tokenSP.edit();
                editor.putString("AuthToken",respObj.get("token").toString());
                editor.putString("userId",respObj.get("username").toString());
                editor.apply();
                savedToken=respObj.get("token").toString();
            }catch(JSONException e)
            {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
                return "success";

        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("success")) {
                intent=new Intent(MainActivity.this,HomeActivity.class);
                intent.putExtra("authorizationToken",savedToken);
                startActivity(intent);
            }else{

            }
        }
    }
}
