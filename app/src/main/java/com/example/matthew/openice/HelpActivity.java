package com.example.matthew.openice;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView usageLabel = (TextView)findViewById(R.id.usageLabel);
        TextView usageDesc = (TextView)findViewById(R.id.usageDesc);
        TextView mapIconsLabel = (TextView)findViewById(R.id.mapIconsLabel);
        TextView skateDesc = (TextView)findViewById(R.id.skateDesc);
        TextView amenityIconsLabel = (TextView)findViewById(R.id.amenityIconsLabel);
        TextView washroomDesc = (TextView)findViewById(R.id.washroomDesc);
        TextView changeroomDesc = (TextView)findViewById(R.id.changeroomDesc);
        TextView skateTrailDesc = (TextView)findViewById(R.id.skateTrailDesc);
        TextView skateRentalDesc = (TextView)findViewById(R.id.skateRentalDesc);

        usageLabel.setText(R.string.usageLabel);
        usageDesc.setText(R.string.usageDesc);
        mapIconsLabel.setText(R.string.mapIconsLabel);
        skateDesc.setText(R.string.skateDesc);
        amenityIconsLabel.setText(R.string.amenityIconsLabel);
        washroomDesc.setText(R.string.washroomDesc);
        changeroomDesc.setText(R.string.changeroomDesc);
        skateTrailDesc.setText(R.string.skateTrailDesc);
        skateRentalDesc.setText(R.string.skateRentalDesc);

        ImageView skate = (ImageView)findViewById(R.id.skate);
        ImageView washroom = (ImageView)findViewById(R.id.washroom);
        ImageView changeroom = (ImageView)findViewById(R.id.changeroom);
        ImageView skateTrail = (ImageView)findViewById(R.id.skateTrail);
        ImageView skateRental = (ImageView)findViewById(R.id.skateRental);

        skate.setImageResource(R.drawable.skate_med);
        washroom.setImageResource(R.drawable.washroom);
        changeroom.setImageResource(R.drawable.skatechangeroom);
        skateTrail.setImageResource(R.drawable.skatingtrail);
        skateRental.setImageResource(R.drawable.skaterental);
    }
}
