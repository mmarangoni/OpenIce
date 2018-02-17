package com.example.matthew.openice;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutLabel = (TextView)findViewById(R.id.aboutLabel);
        TextView about = (TextView)findViewById(R.id.about);
        TextView author = (TextView)findViewById(R.id.author);

        aboutLabel.setText(R.string.aboutLabel);
        about.setText(R.string.about);
        author.setText(R.string.author);
    }
}
