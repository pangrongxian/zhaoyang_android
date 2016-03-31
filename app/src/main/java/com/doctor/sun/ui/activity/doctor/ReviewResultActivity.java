package com.doctor.sun.ui.activity.doctor;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.doctor.sun.R;
import com.doctor.sun.bean.Constants;
import com.doctor.sun.databinding.ActivityReviewResultBinding;
import com.doctor.sun.entity.doctor;
import com.doctor.sun.ui.activity.BaseActivity2;
import com.doctor.sun.ui.widget.PassDialog;


/**
 * Created by rick on 11/19/15.
 */
public class ReviewResultActivity extends BaseActivity2 implements View.OnClickListener {

    private ActivityReviewResultBinding binding;

    public static Intent makeIntent(Context context, doctor data) {
        Intent i = new Intent(context, ReviewResultActivity.class);
        i.putExtra(Constants.DATA, data);
        return i;
    }


    private doctor getData() {
        return getIntent().getParcelableExtra(Constants.DATA);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review_result);
        doctor data = getData();
        switch (data.getStatus()) {
            case doctor.STATUS_PENDING: {
                binding.tvStatus.setText("审核中");
                binding.tvInstruction.setText("您的信息正在审核中，请耐心等待...");
                break;
            }

            case doctor.STATUS_REJECT: {
                binding.tvStatus.setText("审核不通过");
                binding.tvInstruction.setText("资料有误，请重新填写");
                break;
            }
        }

        binding.actionBack.setOnClickListener(this);
        binding.actionEdit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_back: {
                Intent intent = MainActivity.makeIntent(this);
                startActivity(intent);
                break;
            }
            case R.id.action_edit: {
                Intent intent = EditDoctorInfoActivity.makeIntent(this,null);
                startActivity(intent);
                break;
            }
        }
    }
}
