package com.doctor.sun.ui.activity.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;

import com.doctor.sun.bean.Constants;
import com.doctor.sun.entity.AppointMent;
import com.doctor.sun.event.SwitchTabEvent;
import com.doctor.sun.module.AuthModule;
import com.doctor.sun.ui.activity.TabActivity;
import com.doctor.sun.ui.fragment.DiagnosisFragment;
import com.doctor.sun.ui.fragment.FillForumFragment;
import com.doctor.sun.ui.fragment.ModifyForumFragment;
import com.doctor.sun.ui.handler.QCategoryHandler;
import com.doctor.sun.ui.model.HeaderViewModel;
import com.doctor.sun.ui.pager.ConsultingDetailPagerAdapter;
import com.squareup.otto.Subscribe;

import io.ganguo.library.Config;
import io.ganguo.library.core.event.extend.OnPageChangeAdapter;


/**
 * Created by rick on 12/16/15.
 */
public class ConsultingDetailActivity extends TabActivity
        implements QCategoryHandler.QCategoryCallback, FillForumFragment.SetHeaderListener {
    public static final int POSITION_ANSWER = 0;
    public static final int POSITION_SUGGESTION = 1;
    public static final int POSITION_SUGGESTION_READONLY = 2;
    private int position = 0;
    private HeaderViewModel header0;
    private HeaderViewModel header1;
    private boolean isReadOnly;

    public static Intent makeIntent(Context context, AppointMent data, int position) {
        Intent i = new Intent(context, ConsultingDetailActivity.class);
        i.putExtra(Constants.DATA, data);
        i.putExtra(Constants.POSITION, position);
        return i;
    }


    private AppointMent getData() {
        return getIntent().getParcelableExtra(Constants.DATA);
    }

    private int getPosition() {
        return getIntent().getIntExtra(Constants.POSITION, POSITION_ANSWER);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getPosition() == POSITION_SUGGESTION_READONLY) {
            isReadOnly = true;
        } else {
            isReadOnly = false;
        }
        super.onCreate(savedInstanceState);
        initListener();
    }

    @Override
    protected PagerAdapter createPagerAdapter() {
        return new ConsultingDetailPagerAdapter(getSupportFragmentManager(), getData(), isReadOnly);
    }


    @Override
    protected HeaderViewModel createHeaderViewModel() {
        header0 = new HeaderViewModel(this);
        header1 = new HeaderViewModel(this);

        if (isUserPatient()) {
            //病人端
            header0.setRightTitle("");
            header1.setLeftTitle(getData().getHandler().getTitle());
        } else {
            //医生端
            if (!isReadOnly) {
                header0.setRightTitle("补充问卷")
                        .setRightFirstTitle("修改用药");
                header1.setRightTitle("保存");
            }
        }

        if (getPosition() == 0) {
            return header0;
        } else {
            return header1;
        }
    }

    @Override
    public void onFirstMenuClicked() {
        switchTab(new SwitchTabEvent(0));
    }

    @Override
    public void onMenuClicked() {
        switch (position) {
            case 0: {
                if (isUserPatient()) {
                    if (binding.getHeader().getRightTitle().equals("保存")) {
                        //保存
                        ModifyForumFragment.getInstance(getData().getAppointMentId()).save();
                    }
                } else {
                    Intent intent = AssignQuestionActivity.makeIntent(this, getData());
                    startActivity(intent);
                }
                break;
            }
            case 1: {
                DiagnosisFragment.getInstance(null).setDiagnosise();
                break;
            }
        }
    }

    @Subscribe
    public void switchTab(SwitchTabEvent event) {
        binding.vp.setCurrentItem(event.getPosition());
    }

    @Override
    public void onCategorySelect(QCategoryHandler data) {
        int questionCategoryId = data.getQuestionCategoryId();
        if (isUserPatient()) {
            //病人端 具体问卷查看时 －－> 保存(有效)
            header0.setRightTitle("保存");
            ModifyForumFragment.getInstance(getData().getId()).loadQuestions(questionCategoryId);
        } else {
            FillForumFragment.getInstance(getData()).loadQuestions(questionCategoryId);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        switchTab(new SwitchTabEvent(isReadOnly ? 1 : getPosition()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.doctor_REQUEST_CODE:
            case Constants.PRESCRITION_REQUEST_CODE:
                DiagnosisFragment.getInstance(getData()).handlerResult(requestCode, resultCode, data);
                break;
            case Constants.PATIENT_PRESCRITION_REQUEST_CODE:
                ModifyForumFragment.getInstance(getData().getAppointMentId()).handleResult(requestCode, resultCode, data);
                break;
            case Constants.UPLOAD_REQUEST_CODE:
            case Constants.UPLOAD_REQUEST_CODE / 2:
                ModifyForumFragment.getInstance(getData().getAppointMentId()).handleImageResult(requestCode, resultCode, data);
                break;
        }
    }

    private void initListener() {
        getBinding().vp.addOnPageChangeListener(new OnPageChangeAdapter() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ConsultingDetailActivity.this.position = position;
                if (position == 0) {
                    getBinding().setHeader(header0);
                } else {
                    getBinding().setHeader(header1);
                }
            }
        });
    }

    /**
     * 病人 - true, 医生 - false
     *
     * @return
     */
    private boolean isUserPatient() {
        return Config.getInt(Constants.USER_TYPE, -1) == AuthModule.PATIENT_TYPE;
    }

    @Override
    public void setHeaderRightTitle(String title) {
        header0.setRightTitle(title);
    }
}