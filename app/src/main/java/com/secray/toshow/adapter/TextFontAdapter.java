package com.secray.toshow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.secray.toshow.App;
import com.secray.toshow.R;
import com.secray.toshow.listener.OnTextColorItemClickListener;
import com.secray.toshow.listener.OnTextFontItemClickListener;
import com.secray.toshow.widget.ColorCircleView;

import static com.secray.toshow.Utils.Constant.TEXT_COLOR_ARRAY;

/**
 * Created by xiekui on 17-8-28.
 */

public class TextFontAdapter extends RecyclerView.Adapter<TextFontAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private int mPosition = -1;
    private OnTextFontItemClickListener mListener;

    public void setOnTextFontItemClickListener(OnTextFontItemClickListener listener) {
        this.mListener = listener;
    }

    public TextFontAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.text_fonts_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setTypeface(App.get(mContext).getTypefaces().get(position));
        if (mPosition == position) {
            holder.mTextView.setTextColor(mContext.getColor(R.color.colorAccent));
        } else {
            holder.mTextView.setTextColor(mContext.getColor(R.color.main_text_color));
        }
    }

    @Override
    public int getItemCount() {
        return App.get(mContext).getTypefaces().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.text_font);

            itemView.findViewById(R.id.text_font_root).setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onTextFontChanged(getAdapterPosition(), mTextView.getTypeface());
                }
            });
        }

        TextView mTextView;
    }

    public void notifyAdapter(int position) {
        mPosition = position;
        notifyDataSetChanged();
    }
}
