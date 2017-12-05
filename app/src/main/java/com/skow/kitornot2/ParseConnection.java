/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.skow.kitornot2;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;


public class ParseConnection extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    Parse.enableLocalDatastore(this);

    Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
            .applicationId("b688b965e7d11efeb92422b7c00e0d27f0bd1b38")
            .clientKey("ff1fbfb5e435d0fc6f12196c972e55abe558786a")
            .server("http://ec2-52-91-81-222.compute-1.amazonaws.com:80/parse/")
    .build()
    );

      ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
