package me.vebbo.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import me.vebbo.android.R;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        findViewById(R.id.licenseBack).setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}