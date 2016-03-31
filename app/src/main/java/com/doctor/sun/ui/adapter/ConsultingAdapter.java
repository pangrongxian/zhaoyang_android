package com.doctor.sun.ui.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.util.Log;
import android.view.View;

import com.doctor.sun.R;
import com.doctor.sun.bean.Constants;
import com.doctor.sun.databinding.IncludeMessageCountBinding;
import com.doctor.sun.databinding.ItemConsultingBinding;
import com.doctor.sun.databinding.PItemConsultingBinding;
import com.doctor.sun.databinding.PItemMedicineHelperBinding;
import com.doctor.sun.databinding.PItemSystemTipBinding;
import com.doctor.sun.dto.PageDTO;
import com.doctor.sun.entity.AppointMent;
import com.doctor.sun.entity.SystemTip;
import com.doctor.sun.entity.im.TextMsg;
import com.doctor.sun.http.Api;
import com.doctor.sun.http.callback.ApiCallback;
import com.doctor.sun.module.PushModule;
import com.doctor.sun.ui.activity.patient.MedicineHelperActivity;
import com.doctor.sun.ui.activity.patient.SystemTipActivity;
import com.doctor.sun.ui.adapter.ViewHolder.BaseViewHolder;
import com.doctor.sun.ui.adapter.ViewHolder.LayoutId;

import io.ganguo.library.Config;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by rick on 12/15/15.
 */
public class ConsultingAdapter extends SimpleAdapter<LayoutId, ViewDataBinding> {
    private Realm realm;
    private PushModule api = Api.of(PushModule.class);

    public ConsultingAdapter(Context context, Realm realm) {
        super(context);
        this.realm = realm;
    }

    @Override
    public void onBindViewBinding(BaseViewHolder<ViewDataBinding> vh, int position) {
        if (vh.getItemViewType() == R.layout.item_consulting) {
            AppointMent AppointMent = (AppointMent) get(position);
            RealmQuery<TextMsg> q = realm.where(TextMsg.class)
                    .equalTo("sessionId", AppointMent.getVoipAccount())
                    .equalTo("userData", AppointMent.getHandler().getUserData());
            RealmResults<TextMsg> results = q.findAll();
            long count = q.equalTo("haveRead", false).count();
            ItemConsultingBinding rootBinding = (ItemConsultingBinding) vh.getBinding();
            bindCount(results, count, rootBinding.llyMessage, rootBinding.divider);
        } else if (vh.getItemViewType() == R.layout.p_item_consulting) {
            AppointMent AppointMent = (AppointMent) get(position);
            Log.e(TAG, "data: " + AppointMent);
            RealmQuery<TextMsg> q = realm.where(TextMsg.class)
                    .equalTo("sessionId", AppointMent.getdoctor().getVoipAccount())
                    .equalTo("userData", AppointMent.getHandler().getUserData());
            RealmResults<TextMsg> results = q.findAll();
            long count = q.equalTo("haveRead", false).count();
            PItemConsultingBinding rootBinding = (PItemConsultingBinding) vh.getBinding();
            bindCount(results, count, rootBinding.llyMessage, rootBinding.divider);
        } else if (vh.getItemViewType() == R.layout.p_item_medicine_helper) {
            RealmQuery<TextMsg> q = realm.where(TextMsg.class)
                    .contains("userData", MedicineHelperActivity.ADMIN_DRUG);
            long count = q.equalTo("haveRead", false).count();
            PItemMedicineHelperBinding binding = (PItemMedicineHelperBinding) vh.getBinding();

            if (count > 0) {
                binding.tvCount.setText(String.valueOf(count));
                binding.tvCount.setVisibility(View.VISIBLE);
            } else {
                binding.tvCount.setVisibility(View.GONE);
            }
        }

        if (vh.getItemViewType() == R.layout.p_item_system_tip) {
            final PItemSystemTipBinding binding = (PItemSystemTipBinding) vh.getBinding();
            api.systemTip(String.valueOf(1)).enqueue(new ApiCallback<PageDTO<SystemTip>>() {
                @Override
                protected void handleResponse(PageDTO<SystemTip> response) {
                    if (response.getData().size() > 0) {
                        binding.tvDate.setText(response.getData().get(0).getCreatedAt());
                        binding.tvTip.setText(response.getData().get(0).getTitle());
                    }
                    long lastVisitTime = Config.getLong(SystemTipActivity.LAST_VISIT_TIME + Config.getString(Constants.VOIP_ACCOUNT), -1);
//                    Log.e(TAG, "lastVisitTime: " + lastVisitTime);
                    int count = 0;
                    for (SystemTip systemTip : response.getData()) {
                        boolean haveRead = systemTip.getHandler().haveRead(lastVisitTime);
                        if (!haveRead) {
                            count += 1;
                        }
                    }
                    if (count > 0) {
                        binding.tvCount.setText(String.valueOf(count));
                        binding.tvCount.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvCount.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void bindCount(RealmResults<TextMsg> results, long count, IncludeMessageCountBinding binding, View divider) {
        if (!results.isEmpty()) {
            TextMsg lastMsg = results.last();
            binding.getRoot().setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            binding.tvMessage.setText(lastMsg.getBody());
            if (count != 0l) {
                binding.tvMessageCount.setVisibility(View.VISIBLE);
                binding.tvMessageCount.setText(String.valueOf(count));
            } else {
                binding.tvMessageCount.setVisibility(View.GONE);
            }
        } else {
            binding.getRoot().setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            binding.tvMessageCount.setVisibility(View.GONE);
        }
    }
}
