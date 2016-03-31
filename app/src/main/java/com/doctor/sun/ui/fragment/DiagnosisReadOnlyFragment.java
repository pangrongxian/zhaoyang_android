package com.doctor.sun.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doctor.sun.R;
import com.doctor.sun.bean.Constants;
import com.doctor.sun.databinding.FragmentDiagnosisReadonlyBinding;
import com.doctor.sun.databinding.IncludeDiagnosisDetailBinding;
import com.doctor.sun.databinding.IncludeDiagnosisResultBinding;
import com.doctor.sun.databinding.ItemTagBinding;
import com.doctor.sun.dto.ApiDTO;
import com.doctor.sun.entity.AppointMent;
import com.doctor.sun.entity.DiagnosisInfo;
import com.doctor.sun.entity.Prescription;
import com.doctor.sun.http.Api;
import com.doctor.sun.http.callback.ApiCallback;
import com.doctor.sun.module.AuthModule;
import com.doctor.sun.module.DiagnosisModule;
import com.doctor.sun.ui.model.DiagnosisReadOnlyViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import io.ganguo.library.Config;
import io.ganguo.library.util.log.Logger;
import io.ganguo.library.util.log.LoggerFactory;

/**
 * 医生建议 历史纪录 只读 - 医生端
 * <p/>
 * Created by Lynn on 1/14/16.
 */
public class DiagnosisReadOnlyFragment extends Fragment {
    public Logger logger = LoggerFactory.getLogger(DiagnosisFragment.class);
    private static DiagnosisReadOnlyFragment instance;
    private DiagnosisModule api = Api.of(DiagnosisModule.class);
    private DiagnosisReadOnlyViewModel viewModel;
    private FragmentDiagnosisReadonlyBinding binding;
    private IncludeDiagnosisResultBinding resultBinding;

    public static DiagnosisReadOnlyFragment getInstance(AppointMent id) {
        if (instance == null) {
            instance = new DiagnosisReadOnlyFragment();
            Bundle args = new Bundle();
            args.putParcelable(Constants.DATA, id);
            instance.setArguments(args);
        } else {
            instance.getArguments().putParcelable(Constants.DATA, id);
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDiagnosisReadonlyBinding.inflate(LayoutInflater.from(getContext()));
        if (viewModel == null) {
            viewModel = new DiagnosisReadOnlyViewModel();
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel = null;
        instance = null;
    }

    private void initDiagnosisView() {
        if (!getUserType()) {
            //医生端
            IncludeDiagnosisDetailBinding detailBinding =
                    IncludeDiagnosisDetailBinding.inflate(LayoutInflater.from(getContext()));
            detailBinding.setData(viewModel);
            binding.llRoot.addView(detailBinding.getRoot());
        }
        resultBinding = IncludeDiagnosisResultBinding.inflate(LayoutInflater.from(getContext()));
        resultBinding.setData(viewModel);
        binding.llRoot.addView(resultBinding.getRoot());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDiagnosisInfo();
    }

    private void getDiagnosisInfo() {
        AppointMent AppointMent = getArguments().getParcelable(Constants.DATA);
        if (AppointMent != null) {
            api.diagnosisInfo(AppointMent.getId()).enqueue(new ApiCallback<DiagnosisInfo>() {
                @Override
                protected void handleResponse(DiagnosisInfo response) {
                    //data != null
                    initDiagnosisView();

                    logger.d("诊断返回结果: " + response.toString());
                    viewModel.cloneFromDiagnosisInfo(response);
                    //用药设置
                    resultBinding.flMedicine.setVisibility(View.VISIBLE);
                    if (response.getPrescription() != null) {
                        setPrescription(response.getPrescription());
                    } else {
                        //停止用药
                        resultBinding.flMedicine.addView(getSingleText("停止用药"));
                    }

                    //处理支付
//                    setPay(response);

                    //会诊状态
                    setReturn(response);
                    resultBinding.tvTypeDate.setText(getReturnTypeAndInterval(response));
                    Log.e("TAG", "handleResponse: " + response.toString());
                    resultBinding.tvdoctorAdvince.setText(response.getdoctorAdvince().trim());

                    binding.executePendingBindings();
                }

                @Override
                protected void handleApi(ApiDTO<DiagnosisInfo> body) {
                    //data == null
                    setWaitingView();
                    super.handleApi(body);
                }
            });
        }
    }

    private void setPrescription(List<Prescription> prescription) {
        for (int i = 0; i < prescription.size(); i++) {
            ItemTagBinding nameBinding = ItemTagBinding.inflate(LayoutInflater.from(getContext()));
            nameBinding.setLabel("用药 : ");
            nameBinding.setData(prescription.get(i).getMediaclName().trim());

            ItemTagBinding intervalBinding = ItemTagBinding.inflate(LayoutInflater.from(getContext()));
            intervalBinding.setLabel("间隔 : ");
            intervalBinding.setData(prescription.get(i).getInterval().trim());

            ItemTagBinding numBinding = ItemTagBinding.inflate(LayoutInflater.from(getContext()));
            numBinding.setLabel("数量 : ");
            numBinding.setData(prescription.get(i).getNumberLabel().trim());

            ItemTagBinding remarkBinding = ItemTagBinding.inflate(LayoutInflater.from(getContext()));
            remarkBinding.setLabel("备注 : ");
            remarkBinding.setData(prescription.get(i).getRemark().trim());

            resultBinding.flMedicine.addView(nameBinding.getRoot());
            resultBinding.flMedicine.addView(intervalBinding.getRoot());
            resultBinding.flMedicine.addView(numBinding.getRoot());
            resultBinding.flMedicine.addView(remarkBinding.getRoot());

            if (i != prescription.size() - 1) {
                resultBinding.flMedicine.addView(getDivider(0, (int) getContext().getResources().getDimension(R.dimen.dp_4),
                        0, (int) getContext().getResources().getDimension(R.dimen.dp_4)));
            }
        }
    }

    private void setReturn(DiagnosisInfo response) {
        if (response.getReturnTime() == 0) {
            return;
        }

        resultBinding.flReturn.setVisibility(View.VISIBLE);

        ItemTagBinding remarkBinding = ItemTagBinding.inflate(LayoutInflater.from(getContext()));
        remarkBinding.setLabel("备注 : ");
        remarkBinding.setData(response.getComment().trim());

        ItemTagBinding timeBinding = ItemTagBinding.inflate(LayoutInflater.from(getContext()));
        timeBinding.setLabel("预约时间 : ");
        timeBinding.setData(response.getDate() + " " + response.getTime().trim());

        ItemTagBinding moneyBinding = ItemTagBinding.inflate(LayoutInflater.from(getContext()));
        moneyBinding.setLabel("详细就诊诊金 : ");
        moneyBinding.setData("￥" + response.getMoney());

        resultBinding.flReturn.addView(remarkBinding.getRoot());
        resultBinding.flReturn.addView(timeBinding.getRoot());
        resultBinding.flReturn.addView(moneyBinding.getRoot());
    }

//    private void setPay(final DiagnosisInfo response) {
//        //支付界面
//        logger.d("has pay : " + response.getReturnX());
//        if (getUserType() && response.getReturnX() == 1) {
////        if (true) {
//            resultBinding.payLabel.getRoot().setVisibility(View.VISIBLE);
//            resultBinding.payView.getRoot().setVisibility(View.VISIBLE);
//            resultBinding.payView.tvHint.setVisibility(View.VISIBLE);
//
//            resultBinding.payView.tvApply.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AppointmentModule.orderAppointMent(
//                            String.valueOf(response.getdoctorRequire() == 1 ? response.getdoctorInfo().getId() : response.getdoctorId()),
//                            String.valueOf(response.getReturnTime()),
//                            response.getReturnType() + "",
//                            //当前病历id
//                            String.valueOf(AppointMent.getMedicalRecord().getMedicalRecordId())).enqueue(new ApiCallback<AppointMent>() {
//                        @Override
//                        protected void handleResponse(AppointMent response) {
//                            AppointmentHandler handler = new AppointmentHandler(response);
//                            if (resultBinding.payView.rbWechat.isChecked()) {
//                                logger.d("wechat pay");
//                                handler.payWithWeChat(getActivity());
//                            } else {
//                                logger.d("alipay");
//                                handler.payWithAlipay(getActivity());
//                            }
//                        }
//                    });
//                }
//            });
//        }
//    }

    private void setWaitingView() {
        binding.llRoot.addView(getSingleText("等待医生诊断"));
        binding.llRoot.addView(getDivider(0, 0, 0, 0));
    }

    private String getReturnTypeAndInterval(DiagnosisInfo response) {
        if (response.getReturnTime() > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            java.util.Date parse = null;
            try {
                parse = simpleDateFormat.parse(response.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
                return "无需复诊";
            }
            return ((parse.getTime() - System.currentTimeMillis()) / 86400000) + 1 + "天后" +
                    getDiagnosisType(response.getReturnType());
        }
        return "无需复诊";
    }

    private boolean getUserType() {
        //true - 病人端 / false - 医生端
        return Config.getInt(Constants.USER_TYPE, -1) == AuthModule.PATIENT_TYPE;
    }

    private TextView getSingleText(String str) {
        TextView NullText = new TextView(getContext());
        NullText.setText(str);
        NullText.setTextColor(Color.parseColor("#363636"));
        NullText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        NullText.setPadding((int) getResources().getDimension(R.dimen.dp_13), (int) getResources().getDimension(R.dimen.dp_15), 0,
                (int) getResources().getDimension(R.dimen.dp_15));
        return NullText;
    }

    /**
     * 传入参数设置margin
     *
     * @param l
     * @param t
     * @param r
     * @param b
     * @return
     */
    private View getDivider(int l, int t, int r, int b) {
        View divider = new View(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        lp.setMargins(l, t, r, b);
        divider.setLayoutParams(lp);
        divider.setBackgroundResource(R.drawable.shape_divider);
        return divider;
    }

    private String getDiagnosisType(int returnType) {
        String type = "";
        switch (returnType) {
            case 1:
                //详细就诊
                type = "详细就诊";
                break;
            case 2:
                //详细就诊
                type = "详细就诊";
                break;
            case 3:
                //转诊
                type = "转诊";
                break;
        }
        return type;
    }
}
