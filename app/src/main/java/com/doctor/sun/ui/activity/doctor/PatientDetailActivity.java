package com.doctor.sun.ui.activity.doctor;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseIntArray;
import android.view.View;

import com.doctor.sun.R;
import com.doctor.sun.bean.Constants;
import com.doctor.sun.databinding.ActivityPatientDetailBinding;
import com.doctor.sun.entity.Answer;
import com.doctor.sun.entity.AppointMent;
import com.doctor.sun.http.Api;
import com.doctor.sun.http.callback.AnswerCallback;
import com.doctor.sun.http.callback.ListCallback;
import com.doctor.sun.module.AnswerModule;
import com.doctor.sun.ui.activity.BaseActivity2;
import com.doctor.sun.ui.adapter.AnswerAdapter;
import com.doctor.sun.ui.adapter.core.LoadMoreListener;
import com.doctor.sun.ui.handler.AppointmentHandler;
import com.doctor.sun.ui.handler.QCategoryHandler;
import com.doctor.sun.ui.model.HeaderViewModel;


/**
 * Created by rick on 11/24/15.
 */
public class PatientDetailActivity extends BaseActivity2 implements QCategoryHandler.QCategoryCallback {

    private static final SparseIntArray idMap = new SparseIntArray();

    static {
        idMap.put(0, R.layout.include_patient_detail);
        idMap.put(1, R.layout.include_patient_detail2);
        idMap.put(2, R.layout.include_patient_detail3);
    }

    private AnswerModule api = Api.of(AnswerModule.class);
    private ActivityPatientDetailBinding binding;
    private AnswerAdapter mAdapter;
    private AppointMent data;
    private LoadMoreListener templateListLoader;

    public static Intent makeIntent(Context context, AppointMent data, int layout) {
        Intent i = new Intent(context, PatientDetailActivity.class);
        i.putExtra(Constants.DATA, data);
        i.putExtra(Constants.LAYOUT_ID, layout);
        return i;
    }

    private AppointMent getData() {
        return getIntent().getParcelableExtra(Constants.DATA);
    }

    private int getPosition() {
        int intExtra = getIntent().getIntExtra(Constants.LAYOUT_ID, -1);
        switch (intExtra) {
            case R.layout.item_AppointMent:
                return 0;
            case R.layout.item_consultation:
                return 1;
            case R.layout.item_urgent_call:
                return 2;
            default:
                return 0;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int position = getPosition();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_detail);
        HeaderViewModel header = new HeaderViewModel(this);
        header.setRightTitle("补充问卷");
        binding.setHeader(header);

        mAdapter = new AnswerAdapter(this, getData());
        mAdapter.mapLayout(R.layout.item_AppointMent, idMap.get(position));
        binding.recyclerView.setAdapter(mAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        data = getData();

        data.setHandler(new AppointmentHandler(data));
        binding.setData(data);
        binding.setPosition(position);
        mAdapter.add(data);
        templateListLoader = new LoadMoreListener() {
            @Override
            protected void onLoadMore() {
                mAdapter.clear();
                mAdapter.add(data);
                QCategoryHandler object = new QCategoryHandler();
                object.setCategoryName("基础问卷");
                object.setQuestionCategoryId(-1);
                mAdapter.add(object);
                api.category(data.getId()).enqueue(new ListCallback<QCategoryHandler>(mAdapter));
            }
        };
        mAdapter.setLoadMoreListener(templateListLoader);

        binding.tvBackCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvBackCircle.setVisibility(View.GONE);
                mAdapter.setLoadMoreListener(templateListLoader);
                mAdapter.clear();
                mAdapter.onFinishLoadMore(false);
                mAdapter.add(data);
                mAdapter.notifyDataSetChanged();
                mAdapter.loadMore();
            }
        });

    }

    @Override
    public void onMenuClicked() {
        Intent intent = AssignQuestionActivity.makeIntent(this, getData());
        startActivity(intent);
    }

    @Override
    public void onCategorySelect(final QCategoryHandler qCategory) {
        binding.tvBackCircle.setVisibility(View.VISIBLE);
        mAdapter.onFinishLoadMore(false);
        mAdapter.setPositionMargin(0);
        final double id = qCategory.getQuestionCategoryId();
        if (id == -1) {
            mAdapter.setLoadMoreListener(new LoadMoreListener() {
                @Override
                protected void onLoadMore() {
                    api.answers(data.getId()).enqueue(new ListCallback<Answer>(mAdapter));
                }
            });
        } else {
            mAdapter.setLoadMoreListener(new LoadMoreListener() {
                @Override
                protected void onLoadMore() {
                    api.categoryDetail(data.getId(), String.valueOf(id))
                            .enqueue(new AnswerCallback(qCategory, mAdapter));
                }
            });
        }
        mAdapter.clear();
        mAdapter.add(data);
        mAdapter.notifyDataSetChanged();
    }
}
