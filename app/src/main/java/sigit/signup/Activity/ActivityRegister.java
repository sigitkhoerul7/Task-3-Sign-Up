package sigit.signup.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sigit.signup.Handler.SQLiteHandler;
import sigit.signup.Handler.SessionManager;
import sigit.signup.R;
import sigit.signup.Volley.AppController;
import sigit.signup.Volley.ConfigURL;

/**
 * Created by sigit on 13/07/17.
 */

public class ActivityRegister extends Activity {
    private static final String TAG = ActivityRegister.class.getSimpleName();

    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText)findViewById(R.id.name);
        inputEmail = (EditText)findViewById(R.id.email);
        inputPassword = (EditText)findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button)findViewById(R.id.btnLinkToLoginScreen);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());

        db = new SQLiteHandler(getApplicationContext());

        if(session.isLoggedIn()){
            Intent intent = new Intent(ActivityRegister.this, A_Main.class);
            startActivity(intent);
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                if(!name.isEmpty() && !email.isEmpty()&& !password.isEmpty()){
                    registerUser(name, email, password);

                }else{
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!",Toast.LENGTH_LONG)
                            .show();
                }

            }
        });
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        ActivityLogin.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.animator.push_left_in,R.animator.push_left_out);
            }
        });

    }
    private void registerUser(final String name, final String email,final String password){

        String tag_string_req = "req_register";
        pDialog.setMessage("Registering...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ConfigURL.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");

                        db.addUser(name, email, uid, created_at);

                        Intent inten = new Intent(ActivityRegister.this, ActivityLogin.class);
                        startActivity(inten);
                        finish();
                    } else {
                        String errorMsg = jObj.getString("error_Msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"Registration Eror:" + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(),Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }
        ){
            protected Map<String, String>getParams(){

                Map<String, String> params = new HashMap<String, String>();
                params.put("tag","register");
                params.put("name", name);
                params.put("email",email);
                params.put("password", password);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showDialog(){
        if(!pDialog.isShowing());
        pDialog.isShowing();
    }
    private void hideDialog(){
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
