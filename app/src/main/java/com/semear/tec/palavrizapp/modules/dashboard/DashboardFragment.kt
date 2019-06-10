package com.semear.tec.palavrizapp.modules.dashboard

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.UserType
import com.semear.tec.palavrizapp.modules.MainActivity
import com.semear.tec.palavrizapp.modules.essay.MyEssayActivity
import com.semear.tec.palavrizapp.modules.essay.essay_mark_list.EssayMarkListFragment
import com.semear.tec.palavrizapp.modules.plans.PlansFragment
import com.semear.tec.palavrizapp.modules.themes.ThemesFragment
import com.semear.tec.palavrizapp.utils.Commons
import kotlinx.android.synthetic.main.card_aulas.*
import kotlinx.android.synthetic.main.card_create_themes_essay.*
import kotlinx.android.synthetic.main.card_create_themes_essay.view.*
import kotlinx.android.synthetic.main.card_list_essay.*
import kotlinx.android.synthetic.main.card_send_essay.*
import kotlinx.android.synthetic.main.card_user_planos.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.item_essay_list.*


class DashboardFragment : Fragment() {

    private var dashboardViewModel: DashboardViewModel? = null
    private var listener: DashboardFragment.OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        dashboardViewModel?.init()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        checkUserTypeView()
    }

    private fun initView() {

        val user = dashboardViewModel!!.currentUser
        user_greetings?.text = String.format(getString(R.string.salute_you), user.fullname)

        val mainActivity = activity as MainActivity?

        mainActivity?.setActionBarTitle("Dashboard")

        user_plan?.setOnClickListener { v ->
            mainActivity?.changeFragment(PlansFragment(), "Planos")
            mainActivity?.setActionBarTitle("Planos")
        }
        user_aula?.setOnClickListener { v ->
            mainActivity?.changeFragment(ThemesFragment(), "Aulas")
            mainActivity?.setActionBarTitle("Aulas")
        }

        user_redacoes?.setOnClickListener { v ->
            startMyEssayActivity()
        }
        user_correcoes?.setOnClickListener { v ->
            mainActivity?.changeFragment(EssayMarkListFragment(), "Sala de Correção")
            mainActivity?.setActionBarTitle("Sala de Correção")
        }
        card_layout_create_essay_theme?.setOnClickListener { v ->
            Log.d("teste","clicado")
            Commons.createThemeDialog(activity as Activity
                    ,
                    {
                        val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(i, 231)

                    },{ themeTitle, urlVideo ->

            },{

            })
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        fun OnPlansClicked()
        fun OnThemesClicked()
    }


    private fun startMyEssayActivity() {
        val it = Intent(activity, MyEssayActivity::class.java)
        startActivity(it)
    }

    private fun checkUserTypeView() {
        val user = dashboardViewModel?.currentUser
        if (user?.userType == UserType.CORRETOR || user?.userType == UserType.ADMINISTRADOR) {
            user_correcoes?.visibility = View.VISIBLE
        } else {
            user_correcoes?.visibility = View.GONE
        }
        if (user?.userType == UserType.ADMINISTRADOR){
            card_layout_create_essay_theme.visibility = View.VISIBLE
        } else {
            card_layout_create_essay_theme.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                DashboardFragment()
    }
}
