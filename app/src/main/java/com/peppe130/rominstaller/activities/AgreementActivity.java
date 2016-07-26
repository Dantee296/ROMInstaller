package com.peppe130.rominstaller.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.peppe130.rominstaller.ControlCenter;
import com.peppe130.rominstaller.R;
import com.peppe130.rominstaller.core.Utils;
import org.sufficientlysecure.htmltextview.HtmlRemoteImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;
import cn.pedant.SweetAlert.SweetAlertDialog;


@SuppressWarnings("ConstantConditions")
@SuppressLint("CommitPrefEdits")
public class AgreementActivity extends AppCompatActivity {

    Button AGREE, CLOSE;
    SharedPreferences SP;
    SharedPreferences.Editor mEditor;
    Boolean mFirstTime, mDoubleBackToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_layout);

        Utils.ACTIVITY = this;

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mEditor = SP.edit();

        AGREE = (Button) findViewById(R.id.agree);
        CLOSE = (Button) findViewById(R.id.close);
        AGREE.setTextColor(Utils.FetchAccentColor());
        CLOSE.setTextColor(Utils.FetchAccentColor());

        mFirstTime = SP.getBoolean("first_time", true);

        if (!ControlCenter.TRIAL_MODE && !mFirstTime) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(new Intent(AgreementActivity.this, MainActivity.class));
        }

        AGREE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ControlCenter.TRIAL_MODE) {
                    String[] mString = {"Buttons UI", "Swipe UI"};
                    new MaterialDialog.Builder(AgreementActivity.this)
                            .items(mString)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    switch (which) {
                                        case 0:
                                            ControlCenter.BUTTON_UI = true;
                                            break;
                                        case 1:
                                            ControlCenter.BUTTON_UI = false;
                                            break;
                                    }
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    startActivity(new Intent(AgreementActivity.this, MainActivity.class));
                                }
                            })
                            .show();
                } else {
                    mEditor.putBoolean("first_time", false).apply();
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    startActivity(new Intent(AgreementActivity.this, MainActivity.class));
                }
            }
        });

        CLOSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(AgreementActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.agreement_dialog_title))
                        .setContentText(getString(R.string.agreement_dialog_message))
                        .showCancelButton(true)
                        .setCancelText(getString(R.string.close_button))
                        .setConfirmText(getString(R.string.ok_button))
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                finishAffinity();
                            }
                        });
                sweetAlertDialog.setCancelable(true);
                sweetAlertDialog.setCanceledOnTouchOutside(true);
                sweetAlertDialog.show();
            }
        });

        HtmlTextView mHtmlTextView = (HtmlTextView) findViewById(R.id.agreement_html_text);
        assert mHtmlTextView != null;
        mHtmlTextView.setHtml(R.raw.agreement, new HtmlRemoteImageGetter(mHtmlTextView));
    }

    @Override
    public void onBackPressed() {
        if (mDoubleBackToExit) {
            super.onBackPressed();
            return;
        }

        this.mDoubleBackToExit = true;
        Utils.ToastShort(AgreementActivity.this, getString(R.string.double_back_to_exit));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDoubleBackToExit = false;
            }
        }, 2000);
    }

}