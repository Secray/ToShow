package com.secray.toshow.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.secray.toshow.R;
import com.secray.toshow.listener.OnImageFilterClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by user on 2017/10/19 0019.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private String[] mFilterNames;
    private List<Bitmap> mIcons;
    private int[] mTypes;
    private int mPosition;
    private OnImageFilterClickListener mListener;

    public void setOnImageFilterClickListener(OnImageFilterClickListener listener) {
        mListener = listener;
    }

    public FilterAdapter(Context context, int[]types, String[] names, List<Bitmap> icons) {
        mContext = context;
        mTypes = types;
        mFilterNames = names;
        mIcons = icons;
        mInflater = LayoutInflater.from(context);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.filter_item, parent, false);
        return new FilterAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mIcon.setImageBitmap(mIcons.get(position));
        holder.mName.setText(mFilterNames[position]);
        holder.mType = mTypes[position];
        if (mPosition == position) {
            holder.mRoot.setBackgroundColor(mContext.getColor(R.color.colorAccent));
        } else {
            holder.mRoot.setBackgroundColor(0);
        }
    }

    @Override
    public int getItemCount() {
        return mFilterNames.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_root)
        void onClick() {
            mListener.onImageFilterChanged(getAdapterPosition(), mType);
        }

        @BindView(R.id.filter_image)
        ImageView mIcon;
        @BindView(R.id.filter_text)
        TextView mName;
        @BindView(R.id.item_root)
        View mRoot;
        int mType;
    }

    public void notifyAdapter(int position) {
        mPosition = position;
        notifyDataSetChanged();
    }
}
