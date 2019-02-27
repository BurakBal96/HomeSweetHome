package net.ddns.morigg.homesweethomeapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.activities.TabloEkleme;
import net.ddns.morigg.homesweethomeapp.structures.UserFullInformation;
import net.ddns.morigg.homesweethomeapp.structures.UserInformation;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this,TabloEkleme.class);
                startActivity(intent);
            }
        });
    }

}
