package com.secray.toshow.activity;

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

import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Util;
import com.secray.toshow.App;
import com.secray.toshow.R;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;

import butterknife.BindView;

/**
 * Created by android on 17-8-26.
 */

public class EditorActivity extends BaseActivity {
    @BindView(R.id.main_img)
    ImageView mEditorPic;
    @BindView(R.id.editor_bmb)
    BoomMenuButton mBmb;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_editor;
    }

    @Override
    protected void initViews() {
        for (int i = 0; i < mBmb.getPiecePlaceEnum().pieceNumber(); i++) {
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                    .normalImageRes(R.drawable.ic_mosaic_24dp)
                    .imageRect(new Rect(Util.dp2px(24), Util.dp2px(20), Util.dp2px(56), Util.dp2px(52)))
                    .buttonRadius(Util.dp2px(40))
                    .rippleEffect(true)
                    .normalColor(Color.WHITE)
                    .normalText("Text")
                    .maxLines(2)
                    .textGravity(Gravity.CENTER)
                    .ellipsize(TextUtils.TruncateAt.MIDDLE)
                    .textSize(14)
                    .typeface(Typeface.SANS_SERIF)
                    .normalTextColor(Color.GRAY)
                    .pieceColorRes(R.color.colorAccent)
                    .shadowEffect(true)
                    .shadowColor(Color.parseColor("#ee000000"));
            mBmb.addBuilder(builder);
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
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        mEditorPic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
    }

    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {
        DaggerActivityComponent.builder().activityModule(new ActivityModule(this)).
                applicationComponent(App.get(this).getApplicationComponent())
                .build()
                .inject(this);
    }
}
