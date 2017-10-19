package com.secray.toshow.Utils;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;

import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.Util;
import com.secray.toshow.R;

/**
 * Created by xiekui on 17-8-28.
 */

public class BmbBuilderManager {
    private static final int[] sIcons = new int[] {
            R.drawable.ic_brightness_24dp, R.drawable.ic_crop_24dp,
            R.drawable.ic_frames_24dp,
            R.drawable.ic_mosaic_24dp, R.drawable.ic_palette_24dp,
            R.drawable.ic_photo_filter_24dp, R.drawable.ic_rotate_24dp,
            R.drawable.ic_warp_24dp, R.drawable.ic_text_fields_24dp};
    private static final int[] sTexts = new int[] {
            R.string.photo_edit_tune_image, R.string.photo_edit_crop,
            R.string.photo_edit_frame, R.string.photo_edit_mosaic,
            R.string.photo_edit_scrawl, R.string.photo_edit_filter,
            R.string.photo_edit_rotate, R.string.photo_edit_water_mark,
            R.string.photo_edit_text};

    public static TextInsideCircleButton.Builder getBuilder(int index, OnBMClickListener listener) {
        return new TextInsideCircleButton.Builder()
                .normalImageRes(sIcons[index])
                .imageRect(new Rect(Util.dp2px(24), Util.dp2px(20), Util.dp2px(56), Util.dp2px(52)))
                .buttonRadius(Util.dp2px(40))
                .rippleEffect(true)
                .normalColor(Color.WHITE)
                .normalTextRes(sTexts[index])
                .maxLines(2)
                .textGravity(Gravity.CENTER)
                .ellipsize(TextUtils.TruncateAt.MIDDLE)
                .textSize(14)
                .typeface(Typeface.SANS_SERIF)
                .normalTextColor(Color.GRAY)
                .pieceColorRes(R.color.colorAccent)
                .shadowEffect(true)
                .shadowColor(Color.parseColor("#ee000000"))
                .listener(listener);
    }
}
