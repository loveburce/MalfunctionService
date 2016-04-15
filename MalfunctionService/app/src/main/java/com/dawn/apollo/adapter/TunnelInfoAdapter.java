package com.dawn.apollo.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dawn.apollo.R;
import com.dawn.apollo.model.TunnelInfo;

import java.util.List;

/**
 * Created by dawn-pc on 2016/4/12.
 */
public class TunnelInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<TunnelInfo> mData;
    private final LayoutInflater mLayoutInflater;
    private Context mContext;

    public TunnelInfoAdapter(Context context, List<TunnelInfo> data) {
        this.mContext = context;
        this.mData = data;
        this.mLayoutInflater = LayoutInflater.from(mContext);

        Log.d("ffffffffffffff", "response -> " + data.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(mLayoutInflater.inflate(R.layout.adapter_tunnel_info, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final TunnelInfo tunnelInfo = mData.get(position);
        ItemHolder itemHolder = (ItemHolder)holder;
        itemHolder.mTextView.setText(tunnelInfo.getName());

        Log.d("ffffffffffffff", "response -> " + itemHolder.toString());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ItemHolder(View itemView) {
            super(itemView);
            mTextView = (TextView)itemView.findViewById(R.id.item);
        }
    }
}
