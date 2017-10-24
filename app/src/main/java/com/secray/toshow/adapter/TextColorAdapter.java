package com.secray.toshow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.secray.toshow.R;
import com.secray.toshow.Utils.Log;
import com.secray.toshow.listener.OnTextColorItemClickListener;
import com.secray.toshow.widget.ColorCircleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.secray.toshow.Utils.Constant.TEXT_COLOR_ARRAY;

/**
 * Created by xiekui on 17-8-28.
 */

public class TextColorAdapter extends RecyclerView.Adapter<TextColorAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private int mPosition = -1;
    private OnTextColorItemClickListener mListener;

    public void setOnTextColorItemClickListener(OnTextColorItemClickListener listener) {
        this.mListener = listener;
    }

    public TextColorAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.text_colors_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mColorCircleView.setFillColor(mContext.getColor(TEXT_COLOR_ARRAY[position]));
        if (mPosition == position) {
            if (!holder.mColorCircleView.isDrawBorder()) {
                holder.mColorCircleView.drawBorder(true);
            }
        } else {
            holder.mColorCircleView.drawBorder(false);
        }
    }

    @Override
    public int getItemCount() {
        return TEXT_COLOR_ARRAY.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.text_color_root)
        void onClick() {
            if (mListener != null) {
                mListener.onTextColorChanged(getAdapterPosition(), mColorCircleView.getColor());
            }
        }

        @BindView(R.id.text_color)
        ColorCircleView mColorCircleView;
    }

    public void notifyAdapter(int position) {
        mPosition = position;
        notifyDataSetChanged();
    }
}
