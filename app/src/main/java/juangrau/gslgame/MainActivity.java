package juangrau.gslgame;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String REGISTER_URL = "http://192.168.1.102/gslgame/v1/register";
    private static final String LOGIN_URL = "http://192.168.1.102/gslgame/v1/login";
//    private String name = "Juan Grau";
//    private String email = "juangrau6@gmail.com";
//    private String password = "pepitona";

    EditText name, email, password;
    TextView error_text;
    String api_key;
    Database database;
    private String temail, tpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        error_text = (TextView) findViewById(R.id.error_text);
    }

    public void submitRegister(View view) {
        registerUser();
    }

    private void registerUser(){

        if (name.getText().length()>0 && email.getText().length()>0 && password.getText().length()>0) {
            error_text.setVisibility(View.GONE);
            temail = email.getText().toString();
            tpassword = password.getText().toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                            Log.d("post", "response: " + response.toString());
                            try {
                                JSONObject json = new JSONObject(response);
                                String json_error = json.getString("error");
                                String json_message = json.getString("message");
                                if (json_error=="true") {
                                    error_text.setVisibility(View.VISIBLE);
                                    error_text.setText(json_message);
                                } else {
                                    // go to next screen
                                    getApiKey();
                                }
                            } catch (Exception e) {
                                Log.d("post", "Exception: " + e.toString());
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("name",name.getText().toString());
                    params.put("email",email.getText().toString());
                    params.put("password", password.getText().toString());
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } else {
            error_text.setVisibility(View.VISIBLE);
            error_text.setText("Please fill all the fields");
        }
    }


    private void getApiKey() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                        Log.d("post", "response: " + response.toString());
                        try {
                            JSONObject json = new JSONObject(response);
                            api_key = json.getString("apiKey");
                            Log.d("db", "api key from json:" + api_key);
                            initDB();
                            storeApiKey(api_key);
                            Cursor cursor = database.getApiKey();
                            String api_key2 = cursor.getString(cursor.getColumnIndex(Database.API_KEY));
                            Log.d("db", "api key from db:" + api_key2);
                        } catch (Exception e) {
                            Log.d("post", "Exception: " + e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",temail);
                params.put("password", tpassword);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void initDB() {
        try {
            database = new Database(getApplicationContext());
            database.open();
            Log.d("DB", "DB Opened");
        } catch (Exception e) {
            Log.d("DBError", "Error opening DB:" + e.toString());
        }
    }

    private void storeApiKey(String api_key) {
        database.setApiKey(api_key);
    }


}
