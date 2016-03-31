package com.doctor.sun.ui.activity.doctor;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.doctor.sun.R;
import com.doctor.sun.bean.Constants;
import com.doctor.sun.databinding.ActivityMainBinding;
import com.doctor.sun.entity.DoctorIndex;
import com.doctor.sun.entity.im.TextMsg;
import com.doctor.sun.http.Api;
import com.doctor.sun.http.callback.ApiCallback;
import com.doctor.sun.module.AuthModule;
import com.doctor.sun.module.ProfileModule;
import com.doctor.sun.ui.handler.MainActivityHandler;
import com.doctor.sun.ui.model.FooterViewModel;
import com.doctor.sun.ui.widget.PassDialog;

import io.ganguo.library.Config;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


/**
 * Created by rick on 10/23/15.
 */
public class MainActivity extends BaseDoctorActivity {

    public static final int NOTFIRSTTIME = 2;
    public static final int ISFIRSTTIME = 1;
    private ProfileModule api = Api.of(ProfileModule.class);
    private ActivityMainBinding binding;

    public static Intent makeIntent(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setFooter(FooterViewModel.getInstance(this, realm, R.id.tab_one));
        binding.setHandler(new MainActivityHandler(this));
        if (Config.getInt(Constants.USER_TYPE, -1) == AuthModule.doctor_PASSED && Config.getInt(Constants.PASSFIRSTTIME, -1) == ISFIRSTTIME) {
            new PassDialog(this).show();
            Config.putInt(Constants.PASSFIRSTTIME, NOTFIRSTTIME);
        }
        RealmChangeListener listener = new RealmChangeListener() {
            @Override
            public void onChange() {
                RealmResults<DoctorIndex> DoctorIndexes = realm.allObjects(DoctorIndex.class);
                if (!DoctorIndexes.isEmpty()) {
                    DoctorIndex first = DoctorIndexes.first();
                    binding.setData(first);
                    binding.executePendingBindings();
                    long unReadCount = realm.where(TextMsg.class).equalTo("haveRead", false).count();
                    binding.setCount((int) unReadCount);
                }
            }
        };
        realm.addChangeListener(listener);
        listener.onChange();
    }

    @Override
    protected void onStart() {
        super.onStart();
        api.DoctorIndex().enqueue(new ApiCallback<DoctorIndex>() {
            @Override
            protected void handleResponse(DoctorIndex response) {
                realm.beginTransaction();
                realm.clear(DoctorIndex.class);
                realm.copyToRealmOrUpdate(response);
                realm.commitTransaction();
            }
        });
    }
}