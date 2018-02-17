package com.example.matthew.openice;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.style.StyleSpan;
import android.widget.Toast;

import static android.graphics.Typeface.BOLD;


public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        // Get a reference to the TextViews
        TextView rinkName = (TextView) findViewById(R.id.rinkName);
        TextView address = (TextView) findViewById(R.id.address);
        TextView pads = (TextView) findViewById(R.id.pads);
        TextView rinkSize = (TextView) findViewById(R.id.rinkSize);
        TextView litArea = (TextView) findViewById(R.id.litArea);
        TextView amenities = (TextView) findViewById(R.id.Amenities);

        // Get a reference to the ImageViews
        ImageView washroom = (ImageView) findViewById(R.id.washroomStatus);
        ImageView changeroom = (ImageView) findViewById(R.id.changeroomStatus);
        ImageView skateTrail = (ImageView) findViewById(R.id.skateTrailStatus);
        ImageView skateRental = (ImageView) findViewById(R.id.skateRentalStatus);

        // Get the data passed from the intent
        String rinkNameText = intent.getStringExtra("rinkName");

        // SpannableStringBuilder required to bold only certain parts of string
        SpannableStringBuilder addressText = new SpannableStringBuilder("Address: " + intent.getStringExtra("address"));
        addressText.setSpan(new StyleSpan(BOLD), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String padsAmt = intent.getStringExtra("numOfPads");
        SpannableStringBuilder padsText;
        if (padsAmt.isEmpty()) {
            padsText = new SpannableStringBuilder("Ice Pads: 0");
        } else {
            padsText = new SpannableStringBuilder("Ice Pads: " + intent.getStringExtra("numOfPads"));
        }
        padsText.setSpan(new StyleSpan(BOLD), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String rinkSizeAmt = intent.getStringExtra("rinkSize");
        SpannableStringBuilder rinkSizeText;
        if (rinkSizeAmt.isEmpty()) {
            rinkSizeText = new SpannableStringBuilder("Rink Size: 0 square metres");
        } else {
            rinkSizeText = new SpannableStringBuilder("Rink Size: " + intent.getStringExtra("rinkSize") + " square meters");
        }
        rinkSizeText.setSpan(new StyleSpan(BOLD), 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String litAreaVal = intent.getStringExtra("litArea");
        SpannableStringBuilder litAreaText;
        if (litAreaVal.isEmpty()) {
            litAreaText = new SpannableStringBuilder("Lit Area: No");
        } else {
            litAreaText = new SpannableStringBuilder("Lit Area: " + litAreaVal);
        }
        litAreaText.setSpan(new StyleSpan(BOLD), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String washroomVal = intent.getStringExtra("washroom");
        String changeroomVal = intent.getStringExtra("changeroom");
        String skateTrailVal = intent.getStringExtra("skateTrail");
        String skateRentalVal = intent.getStringExtra("skateRental");
        SpannableStringBuilder amenitiesText = new SpannableStringBuilder("Amenities:");
        amenitiesText.setSpan(new StyleSpan(BOLD), 0, amenitiesText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (washroomVal.isEmpty()) {
           washroom.setImageResource(R.drawable.washroom_unavailable);
        }
        if (changeroomVal.isEmpty()) {
            changeroom.setImageResource(R.drawable.changeroom_unavailable);
        }
        if (skateTrailVal.isEmpty()) {
            skateTrail.setImageResource(R.drawable.skatingtrail_unavailable);
        }
        if (skateRentalVal.isEmpty()) {
            skateRental.setImageResource(R.drawable.skaterental_unavailable);
        }

        // Set the TextView text
        rinkName.setText(rinkNameText);
        address.setText(addressText);
        pads.setText(padsText);
        rinkSize.setText(rinkSizeText);
        litArea.setText(litAreaText);
        amenities.setText(amenitiesText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:
                showHelp();
                return true;
            case R.id.about:
                showAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void showAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
