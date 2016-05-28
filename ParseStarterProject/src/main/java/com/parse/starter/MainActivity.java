/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.ParseAnalytics;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

  EditText usernameField, passwordField;
  TextView changeSignUpModeTextView;
  Button signUpButton;
  ImageView logo;
  RelativeLayout relativeLayout;
  Boolean signUpModeActive;

  @Override
  public boolean onKey(View v, int keyCode, KeyEvent event) {


    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {

      signUpOrLogIn(v);

    }

    return false;

  }

  @Override
  public void onClick(View v) {

    if (v.getId() == R.id.login_signup_txt) {

      if (signUpModeActive == true) {

        signUpModeActive = false;
        changeSignUpModeTextView.setText("Sign Up");
        signUpButton.setText("Log In");


      } else {

        signUpModeActive = true;
        changeSignUpModeTextView.setText("Log In");
        signUpButton.setText("Sign Up");

      }

    } else if (v.getId() == R.id.logo || v.getId() == R.id.relativeLayout){

      InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }
    else if ( v.getId() == R.id.password)
    {
      passwordField.setText("");

    }
    else if (v.getId() == R.id.username)
    {
      usernameField.setText("");
    }

  }

  public void signUpOrLogIn(View view) {

    if (signUpModeActive == true) {

      ParseUser user = new ParseUser();
      user.setUsername(String.valueOf(usernameField.getText()));
      user.setPassword(String.valueOf(passwordField.getText()));

      user.signUpInBackground(new SignUpCallback() {
        @Override
        public void done(ParseException e) {

          if (e == null) {

            Log.i("AppInfo", "Signup Successful");

            //DO WHAT UPON SIGNUP
            //showUserList();

          } else {

            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

          }

        }
      });

    } else {

      ParseUser.logInInBackground(String.valueOf(usernameField.getText()), String.valueOf(passwordField.getText()), new LogInCallback() {
        @Override
        public void done(ParseUser user, ParseException e) {

          if (user != null) {

            Log.i("AppInfo", "Login Successful");

            //DO WHAT UPON LOGIN
            //showUserList();

          } else {

            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();


          }

        }
      });

    }

  }

//  public void showUserList () {
//
//    Intent i = new Intent(getApplicationContext(), UserList.class);
//    startActivity(i);
//
//  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (ParseUser.getCurrentUser() != null) {

      //DO WHAT IF CURRENT USER IS LOGGED IN
      //showUserList();

    }

    signUpModeActive = true;

    usernameField = (EditText) findViewById(R.id.username);
    passwordField = (EditText) findViewById(R.id.password);
    changeSignUpModeTextView = (TextView) findViewById(R.id.login_signup_txt);
    signUpButton = (Button) findViewById(R.id.login_signup_btn);
    logo = (ImageView) findViewById(R.id.logo);
    relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

    changeSignUpModeTextView.setOnClickListener(this);
    logo.setOnClickListener(this);
    relativeLayout.setOnClickListener(this);
    usernameField.setOnKeyListener(this);
    passwordField.setOnKeyListener(this);

    ParseAnalytics.trackAppOpenedInBackground(getIntent());


  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
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



}
