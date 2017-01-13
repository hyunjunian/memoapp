package com.hyunjunian.www.justamemo;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewMemoActivity extends BaseActivity {

    private static final String TAG = "NewMemoActivity";
    private static final String REQUIRED = "메모를 작성해주세요!";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private TextView mTopbar;
    private EditText mBodyField;
    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_memo);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        mTopbar = (TextView) findViewById(R.id.topbar_new_memo);
        mBodyField = (EditText) findViewById(R.id.field_body);
        mSubmitButton = (Button) findViewById(R.id.submit_memo);


        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        mTopbar.setText(currentDateTimeString);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitMemo();
            }
        });
    }

    private void submitMemo() {
        final String body = mBodyField.getText().toString();

        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-memos
        setEditingEnabled(false);
        Toast.makeText(this, "메모 중...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewMemoActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new memo
                            writeNewMemo(userId, user.username, body, DateFormat.getDateTimeInstance().format(new Date()));
                            Toast.makeText(NewMemoActivity.this, "메모 완료!", Toast.LENGTH_SHORT).show();
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        // [END single_value_read]
    }

    private void setEditingEnabled(boolean enabled) {
        mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewMemo(String userId, String username, String body, String date) {
        // Create new memo at /user-memos/$userid/$memoid and at
        // /memos/$memoid simultaneously
        String key = mDatabase.child("memos").push().getKey();
        Memo memo = new Memo(userId, username, body, date);
        Map<String, Object> memoValues = memo.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/memos/" + key, memoValues);
        childUpdates.put("/user-memos/" + userId + "/" + key, memoValues);

        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]
}
