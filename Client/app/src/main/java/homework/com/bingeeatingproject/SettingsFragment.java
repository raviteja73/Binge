package homework.com.bingeeatingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class SettingsFragment extends Fragment {

    ArrayAdapter<String> adapter;
    SharedPreferences tokenSP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_settings, container, false);
        ListView settingsView=(ListView) view.findViewById(R.id.settingsList);
        final Settings data[] = new Settings[]
                {
                        new Settings(R.drawable.appointment, "Appointments"),
                        new Settings(R.drawable.messages, "Motivational Messages"),
                        new Settings(R.drawable.task, "Tasks"),
                        new Settings(R.drawable.progress, "Progress"),
                        new Settings(R.drawable.logout, "Logout")
                };
        SettingsAdapter adapter = new SettingsAdapter(this.getActivity(), R.layout.row_item, data);

        settingsView.setAdapter(adapter);
        settingsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                   Settings selected=data[i];
                String title=selected.getTitle();
                if(title.equals("Appointments"))
                {
                    Intent intent=new Intent(getActivity(),Appointments.class);
                    startActivity(intent);
                } else if(title.equals("Logout"))
                {
                    tokenSP = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    SharedPreferences.Editor editor=tokenSP.edit();
                    editor.remove("AuthToken");
                    editor.remove("userId");
                    editor.commit();
                    getActivity().finish();
                }
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }
    public SettingsFragment()
    {}


}
