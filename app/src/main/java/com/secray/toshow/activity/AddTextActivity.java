package com.secray.toshow.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.secray.toshow.App;
import com.secray.toshow.R;
import com.secray.toshow.adapter.TextColorAdapter;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;
import com.secray.toshow.listener.OnTextColorItemClickListener;

import butterknife.BindView;

/**
 * Created by xiekui on 17-8-28.
 */

public class AddTextActivity extends BaseActivity implements OnTextColorItemClickListener {
    @BindView(R.id.text_color_list)
    RecyclerView mColorList;
    @BindView(R.id.test)
    TextView mTextView;

    TextColorAdapter mTextColorAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_text;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void onWork() {
        mTextColorAdapter = new TextColorAdapter(this);
        mTextColorAdapter.setOnTextColorItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mColorList.setLayoutManager(linearLayoutManager);
        mColorList.setAdapter(mTextColorAdapter);
    }

    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {
        DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(App.get(this)
                        .getApplicationComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onTextColorChanged(int position, int color) {
        mTextColorAdapter.notifyAdapter(position);
        mTextView.setTextColor(color);
    }
}
