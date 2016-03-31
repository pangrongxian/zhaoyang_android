package com.doctor.sun.ui.activity.patient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;

import com.doctor.sun.bean.Constants;
import com.doctor.sun.entity.AppointMent;
import com.doctor.sun.ui.activity.TabActivity;
import com.doctor.sun.ui.activity.doctor.ConsultingDetailActivity;
import com.doctor.sun.ui.adapter.HistoryDetailAdapter;
import com.doctor.sun.ui.fragment.FillForumFragment;
import com.doctor.sun.ui.fragment.ListFragment;
import com.doctor.sun.ui.handler.QCategoryHandler;
import com.doctor.sun.ui.model.HeaderViewModel;

/**
 * 病人端 历史纪录
 * <p/>
 * Created by lucas on 1/8/16.
 */
public class HistoryDetailActivity extends TabActivity
        implements ListFragment.SetHeaderListener, QCategoryHandler.QCategoryCallback {

    public static Intent makeIntent(Context context, AppointMent AppointMent) {
        Intent i = new Intent(context, HistoryDetailActivity.class);
        i.putExtra(Constants.DATA, AppointMent);
        return i;
    }

    public static Intent makeIntent(Context context, AppointMent AppointMent, int position) {
        Intent i = new Intent(context, HistoryDetailActivity.class);
        i.putExtra(Constants.DATA, AppointMent);
        i.putExtra(Constants.POSITION, position);
        return i;
    }

    private AppointMent getData() {
        return getIntent().getParcelableExtra(Constants.DATA);
    }

    private int getPosition() {
        return getIntent().getIntExtra(Constants.POSITION, ConsultingDetailActivity.POSITION_ANSWER);
    }

    @Override
    protected PagerAdapter createPagerAdapter() {
        return new HistoryDetailAdapter(getSupportFragmentManager(), getData());
    }

    @Override
    protected HeaderViewModel createHeaderViewModel() {
        return new HeaderViewModel(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getPosition() == ConsultingDetailActivity.POSITION_SUGGESTION_READONLY) {
            binding.vp.setCurrentItem(1);
        }
    }

    @Override
    public void setHeaderRightTitle(String title) {
        binding.getHeader().setRightTitle(title);
    }

    @Override
    public void onCategorySelect(QCategoryHandler data) {
        FillForumFragment.getInstance(getData()).loadQuestions(data.getQuestionCategoryId());
    }
}
