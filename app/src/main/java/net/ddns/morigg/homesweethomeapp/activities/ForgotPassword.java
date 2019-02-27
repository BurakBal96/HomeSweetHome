package net.ddns.morigg.homesweethomeapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.ddns.morigg.homesweethomeapp.R;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle(getResources().getText(R.string.ForgotPasswordTitle));
    }
}
