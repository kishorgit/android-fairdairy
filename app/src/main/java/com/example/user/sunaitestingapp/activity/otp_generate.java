package com.example.user.sunaitestingapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.common.AppUtills;

public class otp_generate extends AppCompatActivity {
    TextView resend;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_generate);
     /*   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);*/
        findById();
        resend.setClickable(true);
        //   new_customer.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='test' >Resend</a>";
        resend.setLinkTextColor(Color.rgb(47, 79, 79));
        resend.setText(Html.fromHtml(text));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtills.isNetworkAvailable(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), registration_info.class);
               startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void findById() {
        resend = (TextView) findViewById(R.id.resend);
        submit  = (Button) findViewById(R.id.submit);
    }

}
