package com.secray.toshow.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetBehavior.BottomSheetCallback;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.secray.toshow.R;
import com.secray.toshow.Utils.BitmapUtils;
import com.secray.toshow.Utils.Constant;
import com.secray.toshow.Utils.Log;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;
import com.secray.toshow.mvp.contract.ScrawlContract;
import com.secray.toshow.mvp.presenter.ScrawlPresenter;
import com.secray.toshow.widget.QMUITipDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jarlen.photoedit.operate.OperateUtils;
import cn.jarlen.photoedit.scrawl.DrawAttribute;
import cn.jarlen.photoedit.scrawl.DrawingBoardView;
import cn.jarlen.photoedit.scrawl.ScrawlTools;

/**
 * Created by user on 2017/10/11 0011.
 */

public class ScrawlActivity extends BaseActivity implements ScrawlContract.View,
        SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.scrawl_view)
    DrawingBoardView mScrawlView;
    @BindView(R.id.content)
    LinearLayout mContent;
    @BindView(R.id.action_open)
    TextView mBackAction;
    @BindView(R.id.indicator)
    ImageView mIndicator;
    @BindView(R.id.action_edit)
    ImageView mEditAction;
    @BindView(R.id.action_erase)
    ImageView mEraseAction;
    @BindView(R.id.show_color)
    TextView mShowColor;
    @BindView(R.id.action_color)
    ImageView mColorAction;
    @BindView(R.id.seek_size)
    SeekBar mSeekSize;
    @BindView(R.id.radio_style)
    RadioGroup mStyleGroup;
    @BindView(R.id.bottom_sheet_root)
    LinearLayout mSheetRoot;

    @Inject
    ScrawlPresenter mPresenter;

    QMUITipDialog mTipDialog;
    OperateUtils mOperateUtils;
    BottomSheetBehavior mBehavior;
    ScrawlTools mTool;
    DrawAttribute.DrawStatus mStyle;
    boolean mIsChanged;
    int mSelectedColor;
    Bitmap mPaintBitmap;
    Bitmap mLastBitmap;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_scrawl;
    }

    @Override
    protected void initViews() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }
        mBackAction.setText(R.string.back);
        mSeekSize.setOnSeekBarChangeListener(this);
        mBehavior = BottomSheetBehavior.from(mSheetRoot);
        mBehavior.setBottomSheetCallback(new BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0.5f) {
                    mIndicator.setImageResource(R.drawable.ic_expand_less);
                } else {
                    mIndicator.setImageResource(R.drawable.ic_expand_more);
                }
            }
        });
        mStyleGroup.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onWork() {
        mPresenter.bindView(this);
        mOperateUtils = new OperateUtils(this);
        mStyle = DrawAttribute.DrawStatus.PEN_WATER;
        mSelectedColor = getColor(R.color.colorAccent);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_clear);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mIsChanged = mScrawlView.isChanged();
        switch (item.getItemId()) {
            case R.id.menu_done:
                if (mIsChanged) {
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                            .setTipWord(getString(R.string.progress_dialog_saving))
                            .create();
                    mTipDialog.show();
                    mPresenter.savePic(mScrawlView.getDrawBitmap());
                } else {
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord(getString(R.string.pic_not_changed_message))
                            .create();
                    mTipDialog.show();
                    mScrawlView.postDelayed(() -> mTipDialog.dismiss(), 1000);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {
        DaggerActivityComponent
                .builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycle(mPaintBitmap);
        recycle(mLastBitmap);
    }

    private void recycle(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    @Override
    public void onBackPressed() {
        mIsChanged = mScrawlView.isChanged();
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
        mScrawlView.setLayoutParams(layoutParams);
        mTool = new ScrawlTools(this, mScrawlView, bitmap);
        mPaintBitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.crayon);
        mLastBitmap = mPaintBitmap;
        mTool.creatDrawPainter(mStyle, mPaintBitmap, mSelectedColor);
        mScrawlView.setVisibility(View.VISIBLE);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mScrawlView, "alpha", 0f, 1f);
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
        mScrawlView.postDelayed(() -> {
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

    @OnClick({R.id.action_open, R.id.indicator, R.id.action_edit, R.id.action_erase, R.id.action_color})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_open:
                onBackPressed();
                break;
            case R.id.indicator:
                if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mIndicator.setImageResource(R.drawable.ic_expand_less);
                    mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    mIndicator.setImageResource(R.drawable.ic_expand_more);
                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;
            case R.id.action_edit:
                mEditAction.setImageResource(R.drawable.ic_edit_pressed);
                mEraseAction.setImageResource(R.drawable.ic_erase);

                mTool.creatDrawPainter(DrawAttribute.DrawStatus.PEN_WATER,
                        mPaintBitmap, mSelectedColor);
                break;
            case R.id.action_erase:
                mEditAction.setImageResource(R.drawable.ic_edit);
                mEraseAction.setImageResource(R.drawable.ic_erase_pressed);

                mTool.creatDrawPainter(
                        DrawAttribute.DrawStatus.PEN_ERASER, mPaintBitmap,
                        mSelectedColor);
                break;
            case R.id.action_color:
                int currentBackgroundColor = 0xffffffff;
                ColorPickerDialogBuilder
                        .with(this)
                        .setTitle(getString(R.string.choose_color))
                        .initialColor(currentBackgroundColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(selectedColor ->
                                Log.d("onColorSelected: 0x" + Integer.toHexString(selectedColor)))
                        .setPositiveButton(android.R.string.ok,
                                (dialog, selectedColor, allColors) -> {
                                    mSelectedColor = selectedColor;
                                    mShowColor.setBackgroundColor(selectedColor);
                                    mTool.creatDrawPainter(mStyle,
                                            mPaintBitmap, selectedColor);
                                })
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .build()
                        .show();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress() + 1;
        mPaintBitmap = BitmapUtils.scaleBitmap(mLastBitmap, 1.0f * progress / 4);
        mTool.creatDrawPainter(mStyle, mPaintBitmap, mSelectedColor);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rd1:
                mPaintBitmap = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.crayon);
                mStyle = DrawAttribute.DrawStatus.PEN_WATER;
                break;
            case R.id.rd2:
                mPaintBitmap = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.crayon);
                mStyle = DrawAttribute.DrawStatus.PEN_CRAYON;
                break;
            case R.id.rd3:
                mPaintBitmap = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.eraser);
                mStyle = DrawAttribute.DrawStatus.PEN_WATER;
                break;
        }
        mSeekSize.setProgress(3);
        mLastBitmap = mPaintBitmap;
        mTool.creatDrawPainter(mStyle, mPaintBitmap, mSelectedColor);
    }
}
