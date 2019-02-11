package com.semear.tec.palavrizapp.modules.dashboard;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.modules.MainActivity;
import com.semear.tec.palavrizapp.modules.plans.PlansFragment;
import com.semear.tec.palavrizapp.modules.themes.ThemesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DashboardFragment extends Fragment {

    @BindView(R.id.user_greetings)
    TextView tvHelloUser;

    @BindView(R.id.btn_see_more_plans)
    TextView btnSeeMorePlans;

    @BindView(R.id.user_plan)
    TextView tvUserPlan;

    @BindView(R.id.btn_see_more_aulas)
    TextView btnSeeMoreaulas;

    @BindView(R.id.layout_card_aulas)
    RelativeLayout cardAulas;

    @BindView(R.id.layout_card_plans)
    RelativeLayout cardPlans;





    private DashboardViewModel dashboardViewModel;

    public DashboardFragment() {}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        dashboardViewModel.init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this,v);

        initView();

        //código pra ativar o onboard
       /** if ( getActivity() != null ) {
            new Handler().postDelayed(
                    () -> new MaterialTapTargetPrompt.Builder(getActivity())
                            .setTarget(cardAulas)
                            .setPrimaryText(getString(R.string.first_class_onboard_title))
                            .setSecondaryText(getString(R.string.first_class_onboard_text))
                            .setPromptFocal(new RectanglePromptFocal())
                            .show(), 1000
            );
        }**/


        return v;
    }

    public void initView(){
        User user = dashboardViewModel.getCurrentUser();
        tvHelloUser.setText(String.format(getString(R.string.salute_you),user.getFullname()));
        tvUserPlan.setText(user.getPlan().getPlanTitle(getContext()));

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            btnSeeMorePlans.setOnClickListener(v -> mainActivity.changeFragment(new PlansFragment()));
            cardPlans.setOnClickListener(v -> mainActivity.changeFragment(new PlansFragment()));
            btnSeeMoreaulas.setOnClickListener(v -> mainActivity.changeFragment(new ThemesFragment()));
            cardAulas.setOnClickListener(v -> mainActivity.changeFragment(new ThemesFragment()));
        }
    }






}
