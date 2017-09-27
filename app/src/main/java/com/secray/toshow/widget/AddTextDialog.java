package com.secray.toshow.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.TextView;

import com.secray.toshow.R;

/**
 * Created by user on 2017/9/21 0021.
 */

public class AddTextDialog extends AlertDialog {
    EditText mEditText;
    public AddTextDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.add_text_bottom_add);
        setContentView(R.layout.add_text_dialog_content);
        mEditText = (EditText) findViewById(R.id.add_text);
    }

    public String getAddText() {
        return mEditText.getText().toString();
    }
}
