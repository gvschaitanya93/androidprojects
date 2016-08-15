package com.app.crazyheads;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AddTicket extends ActionBarActivity {

    Button mSubmit;
    static final String TAG_SUCCESS = "success";
    static final String TAG_MESSAGE = "message";
    static final String ADD_TICKET_URL = "http://192.168.1.23/crazyheads/addcomment.php";
    EditText subjectET, DetailsET;
    JSONParser jsonParser;
    ProgressDialog pDialog;
    String subject;
    String details;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ticket);
        Toolbar tb = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(tb);


        Bundle args = getIntent().getExtras();
        username = args.getString("username");
        subjectET = (EditText) findViewById(R.id.ticket_subject);
        DetailsET = (EditText) findViewById(R.id.user_details);
        mSubmit = (Button) findViewById(R.id.button_submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SubmitTicket().execute();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_ticket, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class SubmitTicket extends AsyncTask<String, String, String> {

        SubmitTicket() {
            jsonParser = new JSONParser();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddTicket.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            subject = subjectET.getText().toString();
            details = DetailsET.getText().toString();
            int success;

            try {
                List<NameValuePair> ticket_params = new ArrayList<NameValuePair>();
                ticket_params.add(new BasicNameValuePair("user", username));
                ticket_params.add(new BasicNameValuePair("subject", subject));
                ticket_params.add(new BasicNameValuePair("details", details));

                Log.d("CrazeAPP_request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(ADD_TICKET_URL, "POST", ticket_params);

                // check your log for json response
                Log.d("CrazeAPP_Ticket attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("CrazeAPP_Ticket Successful!", json.toString());

                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("CrazeAPP_Ticket Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            Toast.makeText(AddTicket.this, "Ticket submitted.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddTicket.this, ResultTicket.class));
        }


    }
}
