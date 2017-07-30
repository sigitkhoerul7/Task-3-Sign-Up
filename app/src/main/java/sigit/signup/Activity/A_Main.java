package sigit.signup.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import sigit.signup.Handler.SQLiteHandler;
import sigit.signup.Handler.SessionManager;
import sigit.signup.R;

/**
 * Created by sigit on 13/07/17.
 */

public class A_Main extends Activity {
    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;

    private SQLiteHandler db;
    private SessionManager session;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        txtName = (TextView)findViewById(R.id.name);
        txtEmail = (TextView)findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        db= new SQLiteHandler(getApplicationContext());

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()){
            logoutUser();
        }

        HashMap<String, String>user = db.getUserDetails();

        String name = user.get("name");
        String email= user.get("email");

        txtName.setText(name);
        txtEmail.setText(email);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
    }

    private void logoutUser(){
        session.setLogin(false);

        db.deleteUsers();

        Intent intent = new Intent(A_Main.this, ActivityLogin.class);
        startActivity(intent);
        finish();
    }
}
