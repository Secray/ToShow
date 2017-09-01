package com.secray.toshow.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;

import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Util;
import com.secray.toshow.App;
import com.secray.toshow.R;
import com.secray.toshow.Utils.BmbBuilderManager;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;

import butterknife.BindView;

/**
 * Created by android on 17-8-26.
 */

public class EditorActivity extends BaseActivity implements OnBMClickListener {
    @BindView(R.id.main_img)
    ImageView mEditorPic;
    @BindView(R.id.editor_bmb)
    BoomMenuButton mBmb;

    String mPicturePath;

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
        Uri selectedImage = getIntent().getData();
        if (selectedImage == null || "".equals(selectedImage.toString())) {
            return;
        }
        String[] filePathColumn = { MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        if (cursor == null) return;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        mPicturePath = cursor.getString(columnIndex);
        cursor.close();
        mEditorPic.setImageBitmap(BitmapFactory.decodeFile(mPicturePath));
    }

    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {
        DaggerActivityComponent.builder().activityModule(new ActivityModule(this)).
                applicationComponent(App.get(this).getApplicationComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onBoomButtonClick(int index) {
        Intent i = new Intent(this, AddTextActivity.class);
        i.putExtra("path", mPicturePath);
        startActivity(i);
    }
}
