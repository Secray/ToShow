package com.secray.toshow.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.secray.toshow.R;
import com.secray.toshow.Utils.Constant;
import com.secray.toshow.Utils.Log;
import com.secray.toshow.adapter.TextColorAdapter;
import com.secray.toshow.adapter.TextFontAdapter;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;
import com.secray.toshow.listener.OnTextColorItemClickListener;
import com.secray.toshow.listener.OnTextFontItemClickListener;
import com.secray.toshow.mvp.contract.AddTextContract;
import com.secray.toshow.mvp.presenter.AddTextPresenter;
import com.secray.toshow.widget.QMUITipDialog;

import javax.inject.Inject;

import butterknife.BindView;
import cn.jarlen.photoedit.operate.OperateUtils;
import cn.jarlen.photoedit.operate.OperateView;
import cn.jarlen.photoedit.operate.TextObject;

/**
 * Created by xiekui on 17-8-28.
 */

public class AddTextActivity extends BaseActivity implements OnTextColorItemClickListener,
        View.OnClickListener, OnTextFontItemClickListener, AddTextContract.View {
    @BindView(R.id.add_text_list)
    RecyclerView mColorList;
    @BindView(R.id.text_font)
    TextView mTextFont;
    @BindView(R.id.text_color)
    TextView mTextColor;
    @BindView(R.id.text_add)
    TextView mTextAdd;
    @BindView(R.id.img_content)
    LinearLayout mImgContent;
    @BindView(R.id.text_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.action_open)
    TextView mBackAction;

    @Inject
    AddTextPresenter mPresenter;

    TextColorAdapter mTextColorAdapter;
    TextFontAdapter mTextFontAdapter;
    OperateUtils mOperateUtils;
    TextObject mTextObject;
    QMUITipDialog mTipDialog;
    OperateView mOperateView;
    private boolean mFontSelected;
    private boolean mColorSelected;
    boolean mIsFirst = true;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_text;
    }

    @Override
    protected void initViews() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }

        initRecyclerView();

        mOperateUtils = new OperateUtils(this);
        mBackAction.setText(R.string.back);

        mTextFont.setOnClickListener(this);
        mTextColor.setOnClickListener(this);
        mTextAdd.setOnClickListener(this);
        mBackAction.setOnClickListener(this);
    }

    @Override
    protected void onWork() {
        mPresenter.bindView(this);
        String path = getIntent().getStringExtra("path");
        mImgContent.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mImgContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mPresenter.generateBitmap(path, mImgContent, mOperateUtils);
                    }
                }

        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_clear);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                if (mColorList.getAlpha() > 0f) {
                    hideRecyclerView();
                }
                mColorSelected = false;
                mFontSelected = false;
                setTextViewTopDrawable(R.drawable.ic_text_18dp, mTextFont);
                setTextViewTopDrawable(R.drawable.ic_color_18dp, mTextColor);
                if (mOperateView.hasImageObject()) {
                    mOperateView.save();
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                            .setTipWord(getString(R.string.progress_dialog_saving))
                            .create();
                    mTipDialog.show();
                    mPresenter.savePic(mOperateView);
                } else {
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord(getString(R.string.pic_not_changed_message))
                            .create();
                    mTipDialog.show();
                    mImgContent.postDelayed(() -> mTipDialog.dismiss(), 1000);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {
        DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build()
                .inject(this);
    }

    @Override
    public void onTextColorChanged(int position, int color) {
        mTextColorAdapter.notifyAdapter(position);
        TextObject object = (TextObject) mOperateView.getSelected();
        if (object == null) {
            return;
        }
        object.setColor(color);
        object.commit();
        mOperateView.invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_color:
                setTextViewTopDrawable(!mColorSelected ?
                        R.drawable.ic_color_seleted_18dp : R.drawable.ic_color_18dp, mTextColor);
                setTextViewTopDrawable(R.drawable.ic_text_18dp, mTextFont);
                handleRecyclerView(mColorSelected);
                mColorList.setAdapter(mTextColorAdapter);
                mColorSelected = !mColorSelected;
                mFontSelected = false;
                break;
            case R.id.text_font:
                setTextViewTopDrawable(R.drawable.ic_color_18dp, mTextColor);
                setTextViewTopDrawable(!mFontSelected ?
                        R.drawable.ic_text_seleted_18dp : R.drawable.ic_text_18dp, mTextFont);

                handleRecyclerView(mFontSelected);
                mColorList.setAdapter(mTextFontAdapter);
                mFontSelected = !mFontSelected;
                mColorSelected = false;
                break;
            case R.id.text_add:
                View view = getLayoutInflater().inflate(R.layout.add_text_dialog_content, null);
                EditText editText = (EditText) view.findViewById(R.id.add_text);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.add_text_bottom_add)
                        .setView(view)
                        .setPositiveButton(android.R.string.ok,
                                (dialog1, which) -> {
                                    Log.d("add text = " + editText.getText().toString());
                                    mTextObject = mOperateUtils.getTextObject(
                                            editText.getText().toString(), mOperateView, 5, 0, 0);
                                    mOperateView.addItem(mTextObject);
                                })
                        .setNegativeButton(android.R.string.cancel, (d, which) -> d.dismiss())
                        .create();
                dialog.show();
                break;
            case R.id.action_open:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mOperateView.hasImageObject()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.exit_message)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> back())
                    .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            back();
        }
    }

    private void back() {
        Intent i = new Intent(this, EditorActivity.class);
        i.putExtra("lastBitmap", mPresenter.getPath());
        setResult(Constant.REQUEST_EDIT_PHOTO_CODE, i);
        finish();
    }

    void setTextViewTopDrawable(@DrawableRes int id, TextView textView) {
        Drawable d = getResources().getDrawable(id, null);
        d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
        textView.setCompoundDrawables(null, d, null, null);
    }

    void handleRecyclerView(boolean checked) {
        if (!checked) {
            Log.d("showRecyclerView");
            showRecyclerView();
        } else {
            Log.d("hideRecyclerView");
            hideRecyclerView();
        }
    }

    void initRecyclerView() {
        mTextColorAdapter = new TextColorAdapter(this);
        mTextFontAdapter = new TextFontAdapter(this);
        mTextColorAdapter.setOnTextColorItemClickListener(this);
        mTextFontAdapter.setOnTextFontItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mColorList.setLayoutManager(linearLayoutManager);
    }

    void showRecyclerView() {
        mColorList.setVisibility(View.VISIBLE);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mColorList, "alpha", 0f, 1f);
        ObjectAnimator translate = ObjectAnimator.ofFloat(mColorList, "translationY", mColorList.getY() + 100, 0);
        set.play(alpha).with(translate);
        set.setDuration(500);
        set.start();
    }

    void hideRecyclerView() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mColorList, "alpha", 1f, 0f);
        ObjectAnimator translate = ObjectAnimator.ofFloat(mColorList, "translationY", 0, mColorList.getY() + 100);
        set.play(alpha).with(translate);
        set.setDuration(500);
        set.start();
    }

    @Override
    public void onTextFontChanged(int position, Typeface typeface) {
        mTextFontAdapter.notifyAdapter(position);
        TextObject object = (TextObject) mOperateView.getSelected();
        if (object == null) {
            return;
        }
        object.setTypeface(typeface);
        object.commit();
        mOperateView.invalidate();
    }

    @Override
    public void showImage(Bitmap bitmap) {
        mOperateView = new OperateView(AddTextActivity.this, bitmap);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                bitmap.getWidth(), bitmap.getHeight());
        mOperateView.setLayoutParams(layoutParams);
        mImgContent.removeAllViews();
        mImgContent.addView(mOperateView);
        mOperateView.setMultiAdd(true);

        if (mIsFirst) {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mImgContent, "alpha", 0f, 1f);
            alpha.start();
            mIsFirst = false;
        }
    }

    @Override
    public void showMessage(boolean success) {
        mTipDialog.dismiss();
        QMUITipDialog dialog = new QMUITipDialog.Builder(this)
                .setIconType(success ? QMUITipDialog.Builder.ICON_TYPE_SUCCESS
                        : QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord(success ? getString(R.string.save_success) : getString(R.string.save_failed))
                .create();
        dialog.show();
        mImgContent.postDelayed(() -> dialog.dismiss(), 1000);
        mPresenter.loadLastBitmap();
    }
}
