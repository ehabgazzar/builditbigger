package com.udacity.gradle.builditbigger.pro;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eh.myapplication.backend.myApi.MyApi;
import com.example.jokeviewer.joke_viewer;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.R;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
class EndpointsAsyncTask extends AsyncTask<Context, Void, String> {
    private static MyApi myApiService = null;
    private Context context;
    ProgressDialog pd;

    public EndpointsAsyncTask(Context c) {
        context=c;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        pd= new ProgressDialog(context);
        pd.setMessage("loading");
        pd.show();
    }

    @Override
    protected String doInBackground(Context ... params) {
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }

        context = params[0];


        try {
            return myApiService.sayHi().execute().getData();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        pd.dismiss();

        if(!result.contains("connect timed out")) {
            Intent myIntent = new Intent(context, joke_viewer.class);
            myIntent.putExtra("joke", result); //Optional parameters
            context.startActivity(myIntent);
        }
        else
        {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }
    }
}

public class MainActivityFragment extends Fragment {
    Button button;

    public MainActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        button = (Button) root.findViewById(R.id.b1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new EndpointsAsyncTask(getActivity()).execute(getActivity());
            }
        });

        return root;
    }

}