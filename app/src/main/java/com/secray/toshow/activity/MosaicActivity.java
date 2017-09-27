package com.secray.toshow.activity;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.secray.toshow.R;
import com.secray.toshow.di.component.ApplicationComponent;

import butterknife.BindView;
import dmax.dialog.SpotsDialog;

/**
 * Created by user on 2017/9/22 0022.
 */

public class MosaicActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_mosaic;
    }

    @Override
    protected void initViews() {
        setSupportActionBar(mToolbar);
    }

    @Override
    protected void onWork() {

    }

    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_done:
                SpotsDialog dialog = new SpotsDialog(this, R.style.ProgressDialog);
                dialog.show();
                dialog.setMessage(getString(R.string.progress_dialog_saving));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
