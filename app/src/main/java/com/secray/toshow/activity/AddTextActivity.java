package com.secray.toshow.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.secray.toshow.App;
import com.secray.toshow.R;
import com.secray.toshow.adapter.TextColorAdapter;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;
import com.secray.toshow.listener.OnTextColorItemClickListener;

import butterknife.BindView;
import cn.jarlen.photoedit.operate.OperateUtils;
import cn.jarlen.photoedit.operate.OperateView;
import cn.jarlen.photoedit.operate.TextObject;

/**
 * Created by xiekui on 17-8-28.
 */

public class AddTextActivity extends BaseActivity implements OnTextColorItemClickListener,
        View.OnClickListener {
    @BindView(R.id.add_text_list)
    RecyclerView mColorList;
    @BindView(R.id.text_font)
    TextView mTextFont;
    @BindView(R.id.text_color)
    TextView mTextColor;
    @BindView(R.id.img_content)
    LinearLayout mImgContent;

    TextColorAdapter mTextColorAdapter;
    OperateUtils mOperateUtils;
    TextObject mTextObject;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_text;
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }

        mOperateUtils = new OperateUtils(this);
        String path = getIntent().getStringExtra("path");
        Bitmap resizeBmp = mOperateUtils.compressionFiller(path, mImgContent);
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(resizeBmp);
        //OperateView operateView = new OperateView(AddTextActivity.this, resizeBmp);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                resizeBmp.getWidth(), resizeBmp.getHeight());
        //operateView.setLayoutParams(layoutParams);
        imageView.setLayoutParams(layoutParams);
        mImgContent.addView(imageView);
        //operateView.setMultiAdd(true);

        initRecyclerView();

        mTextFont.setOnClickListener(this);
        mTextColor.setOnClickListener(this);

        //mTextObject = mOperateUtils.getTextObject("Test", operateView, 5, 0, 0);
        //operateView.addItem(mTextObject);
    }

    @Override
    protected void onWork() {

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_color:
                Drawable topDrawable = getResources().getDrawable(R.drawable.ic_color_seleted_18dp);
                topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
                mTextColor.setCompoundDrawables(null, topDrawable, null, null);

                Drawable fontDrawable = getResources().getDrawable(R.drawable.ic_text_18dp);
                fontDrawable.setBounds(0, 0, fontDrawable.getMinimumWidth(), fontDrawable.getMinimumHeight());
                mTextFont.setCompoundDrawables(null, fontDrawable, null, null);
                showRecyclerView();
                break;
            case R.id.text_font:
                topDrawable = getResources().getDrawable(R.drawable.ic_color_18dp);
                topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
                mTextColor.setCompoundDrawables(null, topDrawable, null, null);

                fontDrawable = getResources().getDrawable(R.drawable.ic_text_seleted_18dp);
                fontDrawable.setBounds(0, 0, fontDrawable.getMinimumWidth(), fontDrawable.getMinimumHeight());
                mTextFont.setCompoundDrawables(null, fontDrawable, null, null);
                showRecyclerView();
                break;
        }
    }

    void initRecyclerView() {
        mTextColorAdapter = new TextColorAdapter(this);
        mTextColorAdapter.setOnTextColorItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mColorList.setLayoutManager(linearLayoutManager);
        mColorList.setAdapter(mTextColorAdapter);
    }

    void showRecyclerView() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mColorList, "alpha", 0f, 1f);
        ObjectAnimator translate = ObjectAnimator.ofFloat(mColorList, "Y", 0f, mColorList.getY());
        set.play(alpha).with(translate);
        set.start();
    }
}
