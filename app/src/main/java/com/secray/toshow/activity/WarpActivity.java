package com.secray.toshow.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.secray.toshow.R;
import com.secray.toshow.Utils.Constant;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;
import com.secray.toshow.mvp.contract.WarpContract;
import com.secray.toshow.mvp.presenter.WarpPresenter;
import com.secray.toshow.widget.QMUITipDialog;

import javax.inject.Inject;

import butterknife.BindView;
import cn.jarlen.photoedit.operate.OperateUtils;
import cn.jarlen.photoedit.warp.Picwarp;
import cn.jarlen.photoedit.warp.WarpView;

/**
 * Created by user on 2017/10/11 0011.
 */

public class WarpActivity extends BaseActivity implements WarpContract.View {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.warp_view)
    WarpView mWarpView;
    @BindView(R.id.action_open)
    TextView mBackAction;
    @BindView(R.id.content)
    View mContent;

    @Inject
    WarpPresenter mPresenter;

    QMUITipDialog mTipDialog;
    OperateUtils mOperateUtils;
    private Picwarp mWarp;
    boolean mIsChanged;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_wrap;
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
        mWarp = new Picwarp();
        mWarp.initArray();
        mOperateUtils = new OperateUtils(this);
        String path = getIntent().getStringExtra("path");
        mContent.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mPresenter.generateBitmap(path, mContent, mOperateUtils);
                    }
                });
    }

    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {
        DaggerActivityComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(this))
                .build()
                .inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.op_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_clear);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mIsChanged = mWarpView.isChanged();
        switch (item.getItemId()) {
            case R.id.menu_done:
                if (mIsChanged) {
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                            .setTipWord(getString(R.string.progress_dialog_saving))
                            .create();
                    mTipDialog.show();
                    mPresenter.savePic(mWarpView.getWrapBitmap());
                } else {
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord(getString(R.string.pic_not_changed_message))
                            .create();
                    mTipDialog.show();
                    mWarpView.postDelayed(() -> mTipDialog.dismiss(), 1000);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mIsChanged = mWarpView.isChanged();
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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                bitmap.getWidth(), bitmap.getHeight());
        mWarpView.setLayoutParams(layoutParams);
        mWarpView.setWarpBitmap(bitmap);
        mWarpView.setVisibility(View.VISIBLE);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mWarpView, "alpha", 0f, 1f);
        alpha.start();
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
        mWarpView.postDelayed(() -> {
            dialog.dismiss();
            back();
        }, 1000);
    }

    private void back() {
        Intent i = new Intent(this, EditorActivity.class);
        i.putExtra("lastBitmap", mPresenter.getPath());
        setResult(Constant.REQUEST_EDIT_PHOTO_CODE, i);
        finish();
    }
}
