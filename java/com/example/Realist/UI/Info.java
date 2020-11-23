package com.example.dttapp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dttapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Info extends AppCompatActivity {

    TextView hyperlink;
    BottomNavigationView btmNav;
    TextView aboutText;
    String text;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        text = "This app provides users a platform to conveniently access the housing market. Forget " +
                "about having to gather information by contacting the agents which is time-consuming " +
                "and, sometimes, unnecessary. With this app, the overview of the market, and everything " +
                "you need to know about each individual house are in the palm of your" +
                "hand. With user-friendly experience and quick navigation, " + "this app is the tool " +
                "for every house hunter." +"\n"+ "The developer of this app is a " +
                "recent Economics graduate - Linh Nguyen. Originally from Vietnam, Linh came to the " +
                "Netherlands 3 years ago to pursue her Bachelor degree at Radboud University, Nijmegen." +
                "She grew her interest in programming as she sees its potential in revolutionize " +
                "businesses. Linh is passionate about software development and is striving " +
                "everyday to become a better developer.";
        aboutText = (TextView) findViewById(R.id.about_text);
        aboutText.setText(text);

        btmNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        //set selected item so that the color of the icon can change by default
        btmNav.setSelectedItemId(R.id.info);

        btmNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.home:
                        //go back to home without restart the activity
                        finish();
                        break;

                    case R.id.info:
                        // Respond to navigation item 2 click
                        intent = new Intent(Info.this, Info.class);
                        Info.this.startActivity(intent);
                        break;
                }
                return Info.super.onOptionsItemSelected(item);
            }
        });
    }
}
