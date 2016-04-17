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
public class TunnelInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements View.OnClickListener{
    private List<TunnelInfo> mData;
    private final LayoutInflater mLayoutInflater;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private Context mContext;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            Log.d("tunnelId","tunnelId (String)v.getTag() : "+(String)v.getTag());

            mOnItemClickListener.onItemClick(v,(String)v.getTag());
        }
    }

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public TunnelInfoAdapter(Context context, List<TunnelInfo> data) {
        this.mContext = context;
        this.mData = data;
        this.mLayoutInflater = LayoutInflater.from(mContext);

        Log.d("ffffffffffffff", "response -> " + data.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.adapter_tunnel_info, parent, false);
        ItemHolder itemHolder = new ItemHolder(view);
        //将创建的View注册点击事件
        view.setOnClickListener(this);

        return itemHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final TunnelInfo tunnelInfo = mData.get(position);
        ItemHolder itemHolder = (ItemHolder)holder;
        itemHolder.mTextView.setText(tunnelInfo.getName());
        //将数据保存在itemView的Tag中，以便点击时进行获取
        itemHolder.itemView.setTag(tunnelInfo.getId());
//        Log.d("ffffffffffffff", "response -> " + itemHolder.toString());
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
