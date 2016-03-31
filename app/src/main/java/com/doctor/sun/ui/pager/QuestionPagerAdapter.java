package com.doctor.sun.ui.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.doctor.sun.http.Api;
import com.doctor.sun.module.QuestionModule;
import com.doctor.sun.ui.fragment.ListFragment;
import com.doctor.sun.ui.fragment.QuestionCategoryFragment;
import com.doctor.sun.ui.fragment.QuestionExtendFragment;
import com.doctor.sun.ui.fragment.TemplateExtendFragment;

/**
 * Created by rick on 12/17/15.
 */
public class QuestionPagerAdapter extends FragmentPagerAdapter {
    private String AppointMentId;
    private QuestionModule api = Api.of(QuestionModule.class);

    public String getAppointMentId() {
        return AppointMentId;
    }

    public QuestionPagerAdapter(FragmentManager fm, String AppointMentId) {
        super(fm);
        this.AppointMentId = AppointMentId;

    }

    @Override
    public Fragment getItem(final int position) {
        ListFragment fragment = null;
        switch (position) {
            case 0: {
                return TemplateExtendFragment.getInstance();
            }
            case 1: {
                return QuestionCategoryFragment.getInstance();
            }
            case 2: {
                return QuestionExtendFragment.getInstance();
            }
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "我的模板";
            case 1:
                return "量表库";
            case 2:
                return "问题库";
            default:
                return "";
        }
    }
}
