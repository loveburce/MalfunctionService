package com.dawn.apollo.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dawn.apollo.malfunctionservice.R;

/**
 * Created by Administrator on 2015/10/9.
 */
public class ProgressViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;
    public TextView textView;
    public ProgressViewHolder(View itemView) {
        super(itemView);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        textView = (TextView) itemView.findViewById(R.id.textView5);
    }
}