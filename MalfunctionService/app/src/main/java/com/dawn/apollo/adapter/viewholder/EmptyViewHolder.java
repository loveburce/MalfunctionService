package com.dawn.apollo.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dawn.apollo.R;


/**
 * Created by Administrator on 2016/1/26.
 */
public class EmptyViewHolder extends RecyclerView.ViewHolder {
    public TextView mTextView;
    public EmptyViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView)itemView.findViewById(R.id.textView4);
    }
}
