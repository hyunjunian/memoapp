package com.hyunjunian.www.justamemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

public abstract class MemoListFragment extends Fragment {

    private static final String TAG = "MemoListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Memo, MemoViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public MemoListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_memo_list, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query memosQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Memo, MemoViewHolder>(Memo.class, R.layout.item_memo,
                MemoViewHolder.class, memosQuery) {
            @Override
            protected void populateViewHolder(final MemoViewHolder viewHolder, final Memo model, final int position) {
                final DatabaseReference memoRef = getRef(position);

                // Set click listener for the whole memo view
                final String memoKey = memoRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch memoDetailActivity
                        Intent intent = new Intent(getActivity(), MemoDetailActivity.class);
                        intent.putExtra(MemoDetailActivity.EXTRA_Memo_KEY, memoKey);
                        startActivity(intent);
                    }
                });

                // Bind Memo to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToMemo(model);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);
}
