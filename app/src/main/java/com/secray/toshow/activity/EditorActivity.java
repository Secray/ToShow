package com.secray.toshow.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Util;
import com.secray.toshow.App;
import com.secray.toshow.R;
import com.secray.toshow.Utils.BmbBuilderManager;
import com.secray.toshow.Utils.Constant;
import com.secray.toshow.Utils.Log;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;
import com.secray.toshow.mvp.contract.EditorContract;
import com.secray.toshow.mvp.presenter.EditorPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import cn.jarlen.photoedit.operate.OperateUtils;

import static com.secray.toshow.Utils.ViewUtils.OPERATION_ACTIVITYS;

/**
 * Created by android on 17-8-26.
 */

public class EditorActivity extends BaseActivity implements OnBMClickListener, EditorContract.View {
    @BindView(R.id.main_img)
    ImageView mEditorPic;
    @BindView(R.id.editor_bmb)
    BoomMenuButton mBmb;

    OperateUtils mOperateUtils;

    @Inject
    EditorPresenter mPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_editor;
    }

    @Override
    protected void initViews() {
        for (int i = 0; i < mBmb.getPiecePlaceEnum().pieceNumber(); i++) {
            mBmb.addBuilder(BmbBuilderManager.getBuilder(i, this));
        }
    }

    @Override
    protected void onWork() {
        mPresenter.bindView(this);
        mOperateUtils = new OperateUtils(this);
        Uri selectedImage = getIntent().getData();
        mPresenter.generateBitmap(selectedImage, mEditorPic, mOperateUtils);
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
    public void onBoomButtonClick(int index) {
        Intent i = new Intent(this, OPERATION_ACTIVITYS[index]);
        i.putExtra("path", mPresenter.getPath());
        startActivityForResult(i, Constant.REQUEST_EDIT_PHOTO_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = data.getStringExtra("lastBitmap");
        if (!path.equals(mPresenter.getPath())) {
            if (data != null && resultCode == Constant.REQUEST_EDIT_PHOTO_CODE) {
                mPresenter.loadLastBitmap(path, mEditorPic, mOperateUtils);
            }
        }
    }

    @Override
    public void showImage(Bitmap bitmap) {
        mEditorPic.setImageBitmap(bitmap);
        mEditorPic.setVisibility(View.VISIBLE);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mEditorPic, "alpha", 0f, 1f);
        alpha.start();
    }
}
