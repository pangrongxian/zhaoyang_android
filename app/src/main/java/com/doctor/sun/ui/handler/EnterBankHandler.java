package com.doctor.sun.ui.handler;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.doctor.sun.R;
import com.doctor.sun.ui.activity.doctor.QuestionBankActivity;
import com.doctor.sun.ui.adapter.AssignQuestionAdapter;
import com.doctor.sun.ui.adapter.ViewHolder.LayoutId;

/**
 * Created by lucas on 1/19/16.
 */
public class EnterBankHandler extends BaseHandler implements LayoutId{
    public EnterBankHandler(Activity context) {
        super(context);
    }


    @Override
    public int getItemLayoutId() {
        return R.layout.item_enter_bank;
    }

    public void enterQuestionBank(View view){
        AssignQuestionAdapter.GetAppointMentId getAppointMentId = (AssignQuestionAdapter.GetAppointMentId)view.getContext();
        String AppointMentId = getAppointMentId.getAppointMentId();
        Intent intent = QuestionBankActivity.makeIntent(view.getContext(),AppointMentId);
        view.getContext().startActivity(intent);
    }
}
