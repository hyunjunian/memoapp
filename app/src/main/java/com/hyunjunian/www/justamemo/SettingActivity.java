package com.hyunjunian.www.justamemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView mSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mSignOut = (TextView) findViewById(R.id.sign_out);

        mSignOut.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_out) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }
    }
}
