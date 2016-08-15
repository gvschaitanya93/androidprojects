package com.app.crazyheads;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Messi10 on 04-Jan-15.
 */
public class Welcome extends ActionBarActivity implements View.OnClickListener {

    Button request;
    Button sign_in;
    EditText userNameET, passwordET;
    String username1, password1;
    ProgressDialog pDialog;
    JSONParser jsonParser= new JSONParser();
    public final String PREFS_NAME="login";
    static final String TAG_SUCCESS = "success";
    static final String TAG_MESSAGE = "message";
    static final String LOGIN_URL = "http://192.168.1.7/crazyheads/login.php";
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar  toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
        sign_in = (Button) findViewById(R.id.button_sign_in);
        request = (Button) findViewById(R.id.button_request);
        request.setOnClickListener(this);
        sign_in.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_sign_in:
                userNameET = (EditText) findViewById(R.id.user_sign_in);
                passwordET = (EditText) findViewById(R.id.user_password);
                username1 = userNameET.getText().toString();
                password1 = passwordET.getText().toString();

                if (username1.isEmpty()&&password1.isEmpty()) {
                    TextView tv = (TextView) findViewById(R.id.emptyDetails);
                    tv.setText("Please enter username and password.");
                    tv.setVisibility(View.VISIBLE);

                } else if (password1.isEmpty()) {
                    TextView tv = (TextView) findViewById(R.id.emptyDetails);
                    tv.setText("Please enter password.");
                    tv.setVisibility(View.VISIBLE);

                } else if(username1.isEmpty()){
                    TextView tv = (TextView) findViewById(R.id.emptyDetails);
                    tv.setText("Please enter username.");
                    tv.setVisibility(View.VISIBLE);
                }

                else {

                    new AttemptLogin().execute();

                }

                break;
            case R.id.button_request:
                startActivity(new Intent(this, Register.class));
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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

    class AttemptLogin extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Welcome.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;

            String username = userNameET.getText().toString();
            String password = passwordET.getText().toString();
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                Log.d("CrazeAPP_request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);

                // check your log for json response
                Log.d("CrazeAPP_Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    editor.putBoolean("login_successful", true);
                    editor.commit();
                    Log.d("CrazeAPP_Login Successful!", json.toString());

                    return json.getString(TAG_MESSAGE);
                }else{

                    editor.putBoolean("login_successful", false);
                    editor.commit();
                    Log.d("CrazeAPP_Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(Welcome.this, file_url, Toast.LENGTH_LONG).show();
                Intent intent= new Intent(Welcome.this,Dashboard.class);
                intent.putExtra("username",username1);
                startActivity(intent);
            }

        }

    }
}
