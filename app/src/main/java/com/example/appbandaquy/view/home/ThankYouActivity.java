package com.example.appbandaquy.view.home;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appbandaquy.R;

public class ThankYouActivity extends AppCompatActivity {

    private Toolbar mToolbar6;
    private ImageView mImageView3;
    private TextView mTextView27;
    private TextView mTextView30;
    private TextView mTextView32;
    private FloatingActionButton mFloatingActionButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        mToolbar6 = findViewById(R.id.toolbar6);
        mImageView3 = findViewById(R.id.imageView3);
        mTextView27 = findViewById(R.id.textView27);
        mTextView30 = findViewById(R.id.textView30);
        mTextView32 = findViewById(R.id.textView32);
        mFloatingActionButton3 = findViewById(R.id.floatingActionButton3);

        mToolbar6.setTitle("Cảm Ơn");
        setSupportActionBar(mToolbar6);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar6.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar6.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
