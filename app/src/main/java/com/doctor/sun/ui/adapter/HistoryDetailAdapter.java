package com.doctor.sun.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.doctor.sun.entity.AppointMent;
import com.doctor.sun.ui.fragment.DiagnosisReadOnlyFragment;
import com.doctor.sun.ui.fragment.FillForumFragment;

/**
 * Created by lucas on 1/8/16.
 */
public class HistoryDetailAdapter extends FragmentPagerAdapter {
    private AppointMent AppointMent;

    public HistoryDetailAdapter(FragmentManager fm, AppointMent AppointMent) {
        super(fm);
        this.AppointMent = AppointMent;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FillForumFragment.getInstance(AppointMent);
            case 1:
                return DiagnosisReadOnlyFragment.getInstance(AppointMent);
        }
        return null;
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "填写问卷";
        } else {
            return "医生建议";
        }
    }
}
