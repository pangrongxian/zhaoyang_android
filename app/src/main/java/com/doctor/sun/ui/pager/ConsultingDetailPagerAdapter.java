package com.doctor.sun.ui.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.doctor.sun.bean.Constants;
import com.doctor.sun.entity.AppointMent;
import com.doctor.sun.module.AuthModule;
import com.doctor.sun.ui.fragment.DiagnosisFragment;
import com.doctor.sun.ui.fragment.DiagnosisReadOnlyFragment;
import com.doctor.sun.ui.fragment.FillForumFragment;
import com.doctor.sun.ui.fragment.ModifyForumFragment;

import io.ganguo.library.Config;

/**
 * Created by rick on 12/17/15.
 */
public class ConsultingDetailPagerAdapter extends FragmentPagerAdapter {
    public static final String TAG = ConsultingDetailPagerAdapter.class.getSimpleName();
    private AppointMent AppointMent;
    private boolean isReadOnly = false;

    public ConsultingDetailPagerAdapter(FragmentManager fm, AppointMent AppointMent, boolean isReadOnly) {
        super(fm);
        this.AppointMent = AppointMent;
        this.isReadOnly = isReadOnly;
    }

    @Override
    public Fragment getItem(final int position) {
        int userType = Config.getInt(Constants.USER_TYPE, -1);
        if (userType == AuthModule.PATIENT_TYPE) {
            switch (position) {
                case 0: {
                    //填写问卷 编辑
                    return ModifyForumFragment.getInstance(AppointMent.getId());
                }
                case 1: {
//                AppointMent.setId(325);
                    return DiagnosisReadOnlyFragment.getInstance(AppointMent);
                }
            }
        } else {
            switch (position) {
                case 0: {
                    //填写问卷 只读
                    return FillForumFragment.getInstance(AppointMent);
                }
                case 1: {
//                AppointMent.setId(325);
                    if (!isReadOnly) {
                        return DiagnosisFragment.newInstance(AppointMent);
                    } else {
                        return DiagnosisReadOnlyFragment.getInstance(AppointMent);
                    }
                }
            }
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
