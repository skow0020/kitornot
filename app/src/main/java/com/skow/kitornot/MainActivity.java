package com.skow.kitornot;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.skow.kitornot.UserHome.UserHomeActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener
{
    private EditText usernameField, passwordField;
    private TextView changeSignUpModeTextView;
    private Button signUpButton;
    private Boolean signUpModeActive;
    public static int catTapCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(catTapCount < 20)
                {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (catTapCount == 5)
                    {
                        showToast("Meow!!");
                        catTapCount++;
                    }
                    else if (catTapCount == 11)
                    {
                        showToast("Stop it!!");
                        catTapCount++;
                    }
                    else if (catTapCount == 17)
                    {
                        showToast("I bite you!!");
                        catTapCount++;
                    }
                }
            }
        });

        t1.start();

        //Set font of the title text
        TextView title = (TextView) findViewById(R.id.apptitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/pacifico.ttf");
        title.setTypeface(custom_font);

        signUpModeActive = false;

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        ImageView logo = (ImageView) findViewById(R.id.logo);
        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);
        changeSignUpModeTextView = (TextView) findViewById(R.id.login_signup_txt);
        signUpButton = (Button) findViewById(R.id.login_signup_btn);

        changeSignUpModeTextView.setOnClickListener(this);
        logo.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        usernameField.setOnClickListener(this);
        passwordField.setOnClickListener(this);
        usernameField.setOnKeyListener(this);
        passwordField.setOnKeyListener(this);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        if (ParseUser.getCurrentUser().getUsername() != null) userHome();
    }

    public void showToast(final String text)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 350);
                toast.show();
            }
        });
    }

    public void signUpOrLogIn(View view)
    {
        if (signUpModeActive)
        {
            final ParseUser user = new ParseUser();
            user.setUsername(String.valueOf(usernameField.getText()));
            user.setPassword(String.valueOf(passwordField.getText()));

            user.signUpInBackground(new SignUpCallback()
            {
                @Override
                public void done(ParseException e)
                {
                    if (e == null)
                    {
                        Toast.makeText(getApplicationContext(), "Welcome, " + user.getUsername(), Toast.LENGTH_LONG).show();
                        userHome();
                    }
                    else Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
        else
        {
            ParseUser.logInInBackground(String.valueOf(usernameField.getText()), String.valueOf(passwordField.getText()), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e)
                {
                    if (user != null)
                    {
                        Toast.makeText(getApplicationContext(), "Welcome back, " + user.getUsername(), Toast.LENGTH_LONG).show();
                        userHome();
                    }
                    else Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void userHome ()
    {
        Intent i =  new Intent(getApplicationContext(), UserHomeActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) signUpOrLogIn(v);
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_signup_txt)
        {
            if (signUpModeActive)
            {
                signUpModeActive = false;
                changeSignUpModeTextView.setText(R.string.sign_up_str);
                signUpButton.setText(R.string.log_in_str);
            }
            else
            {
                signUpModeActive = true;
                changeSignUpModeTextView.setText(R.string.log_in_str);
                signUpButton.setText(R.string.sign_up_str);
            }
        }
        else if (v.getId() == R.id.logo || v.getId() == R.id.relativeLayout)
        {
            if (v.getId() == R.id.logo) catTapCount++;

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        else if ( v.getId() == R.id.password) passwordField.setText("");
        else if (v.getId() == R.id.username) usernameField.setText("");
    }
}

class CatTalker extends Application implements Runnable
{
    private Thread t;

    public void run()
    {
        while(true) {
            if (MainActivity.catTapCount == 5)
            {
                Toast.makeText(this, "Meow!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void start() {
        if (t ==null) {
            t = new Thread (this);
            t.start();
        }
    }
}