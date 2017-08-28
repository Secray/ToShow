package com.secray.toshow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.secray.toshow.R;
import com.secray.toshow.listener.OnTextColorItemClickListener;
import com.secray.toshow.widget.ColorCircleView;

import static com.secray.toshow.Utils.Constant.TEXT_COLOR_ARRAY;

/**
 * Created by xiekui on 17-8-28.
 */

public class TextColorAdapter extends RecyclerView.Adapter<TextColorAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
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
    }

    @Override
    public int getItemCount() {
        return TEXT_COLOR_ARRAY.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            mColorCircleView = (ColorCircleView) itemView.findViewById(R.id.text_color);
            itemView.findViewById(R.id.text_color_root).setOnClickListener(v -> {
                mColorCircleView.drawBorder(true);
                if (mListener != null) {
                    mListener.onTextColorChanged(mColorCircleView.getColor());
                }
            });
        }

        ColorCircleView mColorCircleView;
    }
}
