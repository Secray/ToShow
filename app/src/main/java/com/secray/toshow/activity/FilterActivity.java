package com.secray.toshow.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.secray.toshow.R;
import com.secray.toshow.Utils.Constant;
import com.secray.toshow.adapter.FilterAdapter;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;
import com.secray.toshow.listener.OnImageFilterClickListener;
import com.secray.toshow.mvp.contract.FilterContract;
import com.secray.toshow.mvp.presenter.FilterPresenter;
import com.secray.toshow.widget.QMUITipDialog;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jarlen.photoedit.operate.OperateUtils;

import static com.secray.toshow.Utils.ImageFilterUtil.FILTER_NORMAL;

/**
 * Created by user on 2017/10/11 0011.
 */

public class FilterActivity extends BaseActivity implements FilterContract.View,
        OnImageFilterClickListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.action_open)
    TextView mBackAction;
    @BindView(R.id.photo)
    ImageView mContent;
    @BindView(R.id.filter_list)
    RecyclerView mFilterList;
    @BindView(R.id.root)
    View mRoot;

    @Inject
    FilterPresenter mPresenter;


    QMUITipDialog mTipDialog;
    OperateUtils mOperateUtils;
    boolean mIsChanged;
    boolean mIsFirst = true;
    FilterAdapter mAdapter;
    Bitmap mBitmap;
    Bitmap mLastBitmap;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_filter;
    }

    @Override
    protected void initViews() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }

        mBackAction.setText(R.string.back);
    }

    @Override
    protected void onWork() {
        mPresenter.bindView(this);
        mOperateUtils = new OperateUtils(this);
        String path = getIntent().getStringExtra("path");
        mRoot.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mPresenter.generateBitmap(path, mRoot, mOperateUtils);
                    }
                });
        mPresenter.generateIcon(path);
    }

    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {
        DaggerActivityComponent
                .builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build()
                .inject(this);
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
                if (mIsChanged) {
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                            .setTipWord(getString(R.string.progress_dialog_saving))
                            .create();
                    mTipDialog.show();
                    mPresenter.savePic(mLastBitmap);
                } else {
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord(getString(R.string.pic_not_changed_message))
                            .create();
                    mTipDialog.show();
                    mContent.postDelayed(() -> mTipDialog.dismiss(), 1000);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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
        mRoot.postDelayed(() -> {
            dialog.dismiss();
            back();
        }, 1000);
    }

    @Override
    public void showIcons(List<Bitmap> icons) {
        String []names = getResources().getStringArray(R.array.filter_name);
        int []types = getResources().getIntArray(R.array.filter_type);
        mAdapter = new FilterAdapter(this, types, names, icons);
        mAdapter.setOnImageFilterClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterList.setLayoutManager(linearLayoutManager);
        mFilterList.setAdapter(mAdapter);

        mFilterList.setVisibility(View.VISIBLE);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mFilterList, "alpha", 0f, 1f);
        ObjectAnimator translate = ObjectAnimator.ofFloat(mFilterList, "translationY", mFilterList.getY() + 100, 0);
        set.play(alpha).with(translate);
        set.setDuration(500);
        set.start();
    }

    private void back() {
        Intent i = new Intent(this, EditorActivity.class);
        i.putExtra("lastBitmap", mPresenter.getPath());
        setResult(Constant.REQUEST_EDIT_PHOTO_CODE, i);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mIsChanged) {
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

    @Override
    public void showImage(Bitmap bitmap) {
        if (mIsFirst) {
            mBitmap = bitmap;
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    bitmap.getWidth(), bitmap.getHeight());
            layoutParams.gravity = Gravity.CENTER;
            mContent.setLayoutParams(layoutParams);
        }
        mLastBitmap = bitmap;
        mContent.setImageBitmap(bitmap);
        mIsFirst = false;
    }

    @Override
    public void onImageFilterChanged(int position, int type) {
        mAdapter.notifyAdapter(position);
        mPresenter.filterBitmap(mBitmap, type, 1);
        if (type != FILTER_NORMAL) {
            mIsChanged = true;
        } else {
            mIsChanged = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycle(mBitmap);
        recycle(mLastBitmap);
    }

    @OnClick(R.id.action_open)
    void onClick() {
        onBackPressed();
    }

    private void recycle(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}
