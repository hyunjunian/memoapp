package com.hyunjunian.www.justamemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MemoDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MemoDetailActivity";

    public static final String EXTRA_Memo_KEY = "memo_key";
    public static final String EXTRA_Memo_BODY = "memo_body";

    private DatabaseReference mMemoReference;
    private ValueEventListener mMemoListener;
    private String mMemoKey;

    private TextView mBodyView;
    private TextView mTopbarView;
    private TextView mEditView;
    private TextView mSizeSmallView;
    private TextView mSizeBigView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);

        // Get memo key from intent
        mMemoKey = getIntent().getStringExtra(EXTRA_Memo_KEY);
        if (mMemoKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_Memo_KEY");
        }

        // Initialize Database
        mMemoReference = FirebaseDatabase.getInstance().getReference()
                .child("memos").child(mMemoKey);

        // Initialize Views
        mBodyView = (TextView) findViewById(R.id.memo_body);
        mTopbarView = (TextView) findViewById(R.id.topbar_memo_detail);
        mEditView = (TextView) findViewById(R.id.bt_edit_memo);
        mSizeSmallView = (TextView) findViewById(R.id.bt_text_size_small);
        mSizeBigView = (TextView) findViewById(R.id.bt_text_size_big);
        mEditView.setOnClickListener(this);
        mSizeSmallView.setOnClickListener(this);
        mSizeBigView.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the memo
        // [START memo_value_event_listener]
        ValueEventListener memoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Memo object and use the values to update the UI
                Memo memo = dataSnapshot.getValue(Memo.class);
                // [START_EXCLUDE]
                mBodyView.setText(memo.body);
                mTopbarView.setText(memo.date);
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Memo failed, log a message
                Log.w(TAG, "loadMemo:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(MemoDetailActivity.this, "Failed to load memo.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mMemoReference.addValueEventListener(memoListener);
        // [END memo_value_event_listener]

        // Keep copy of memo listener so we can remove it when app stops
        mMemoListener = memoListener;
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove memo value event listener
        if (mMemoListener != null) {
            mMemoReference.removeEventListener(mMemoListener);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i==R.id.bt_text_size_small){
            mBodyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mSizeSmallView.setTextColor(Color.BLACK);
            mSizeBigView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        else if(i==R.id.bt_text_size_big){
            mBodyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            mSizeBigView.setTextColor(Color.BLACK);
            mSizeSmallView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        else if(i==R.id.bt_edit_memo){
            Intent intent = new Intent(MemoDetailActivity.this, EditMemoActivity.class);
            intent.putExtra(MemoDetailActivity.EXTRA_Memo_KEY, mMemoKey);
            intent.putExtra(MemoDetailActivity.EXTRA_Memo_BODY, mBodyView.getText().toString());
            startActivity(intent);
        }
    }
}
