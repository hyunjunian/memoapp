package com.hyunjunian.www.justamemo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by hyunjunian on 06/01/2017.
 */

public class MemoViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView dateView;

    public MemoViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.list_title);
        dateView = (TextView) itemView.findViewById(R.id.list_date);
    }

    public void bindToMemo(Memo memo) {
        titleView.setText(memo.body);
        dateView.setText(memo.date);
    }
}
