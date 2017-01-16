package com.hyunjunian.www.justamemo;

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

public class EditMemoActivity extends BaseActivity {

    private static final String TAG = "EditMemoActivity";
    private static final String REQUIRED = "메모를 작성해주세요!";
    public static final String EXTRA_Memo_KEY = "memo_key";
    public static final String EXTRA_Memo_BODY = "memo_body";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private String mMemoKey;
    private String mMemoBody;

    private TextView mTopbar;
    private EditText mBodyField;
    private Button mSubmitButton;
    private TextView mDeleteMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);

        mMemoKey = getIntent().getStringExtra(EXTRA_Memo_KEY);
        mMemoBody = getIntent().getStringExtra(EXTRA_Memo_BODY);

        if (mMemoKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_Memo_KEY");
        }

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        mTopbar = (TextView) findViewById(R.id.topbar_edit_memo);
        mBodyField = (EditText) findViewById(R.id.field_edit_body);
        mSubmitButton = (Button) findViewById(R.id.submit_edit_memo);
        mDeleteMemo = (TextView) findViewById(R.id.delete_memo);


        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        mTopbar.setText(currentDateTimeString);
        mBodyField.setText(mMemoBody);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitMemo();
            }
        });
        mDeleteMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMemo();
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
                            Toast.makeText(EditMemoActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new memo
                            editMemo(userId, user.username, body, DateFormat.getDateTimeInstance().format(new Date()));
                            Toast.makeText(EditMemoActivity.this, "메모 수정!", Toast.LENGTH_SHORT).show();
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
    private void editMemo(String userId, String username, String body, String date) {
        // Create new memo at /user-memos/$userid/$memoid and at
        // /memos/$memoid simultaneously
        String key = mMemoKey;
        Memo memo = new Memo(userId, username, body, date);
        Map<String, Object> memoValues = memo.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/memos/" + key, memoValues);
        childUpdates.put("/user-memos/" + userId + "/" + key, memoValues);

        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]

    private void deleteMemo() {
        mDatabase.child("memos").child(mMemoKey).removeValue();
        mDatabase.child("user-memos").child(getUid()).child(mMemoKey).removeValue();
        this.finish();
    }
}
