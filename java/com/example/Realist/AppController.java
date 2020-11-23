package com.example.dttapp;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {


        protected static AppController sInstance;
        private RequestQueue mRequestQueue;

        @Override
        public void onCreate() {
            super.onCreate();

            mRequestQueue = Volley.newRequestQueue(this);
            sInstance = this;
        }

        public synchronized static AppController getInstance() {
            return sInstance;
        }

        public RequestQueue getRequestQueue() {
            return mRequestQueue;
        }

}
