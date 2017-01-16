package com.hyunjunian.www.justamemo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyMemosFragment extends MemoListFragment {

    public MyMemosFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-memos")
                .child(getUid());
    }

}
