package com.palavrizar.tec.palavrizapp.modules.dashboard

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.UserType
import com.palavrizar.tec.palavrizapp.modules.MainActivity
import com.palavrizar.tec.palavrizapp.modules.admin.AdminActivity
import com.palavrizar.tec.palavrizapp.modules.essay.MyEssayActivity
import com.palavrizar.tec.palavrizapp.modules.essay.essay_mark_list.EssayMarkListFragment
import com.palavrizar.tec.palavrizapp.modules.plans.MyPlansActivity
import com.palavrizar.tec.palavrizapp.modules.store.StoreFragment
import com.palavrizar.tec.palavrizapp.modules.videocatalog.VideoCatalogFragment
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import kotlinx.android.synthetic.main.card_admin_area.*
import kotlinx.android.synthetic.main.card_aulas.*
import kotlinx.android.synthetic.main.card_list_essay.*
import kotlinx.android.synthetic.main.card_send_essay.*
import kotlinx.android.synthetic.main.card_store.*
import kotlinx.android.synthetic.main.card_user_planos.*
import kotlinx.android.synthetic.main.fragment_dashboard.*




class DashboardFragment : Fragment() {

    private var dashboardViewModel: DashboardViewModel? = null
    private var listener: DashboardFragment.OnFragmentInteractionListener? = null

   val REQ_CODE_MY_PLANS = 300

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        checkUserTypeView()
        registerObservers()

        dashboardViewModel?.setFirstTimeFalse()
    }

    fun registerObservers(){
        dashboardViewModel?.purchasedPlan?.observe(this, Observer {
            if (it != null){
                //TEM PLANO
                dashboardViewModel?.updateUserPlan(it.sku, it.isAutoRenewing)
            }else{
                //NAO TEM PLANO
                dashboardViewModel?.updateUserPlan(Constants.PLAN_FREE_ID)
            }
        })
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        dashboardViewModel?.executeRequest(activity?.applicationContext ?: return){
            queryPurchases()
        }
    }

    fun queryPurchases(){
        dashboardViewModel?.queryPurchases()
    }


    private fun initView() {

        val user = dashboardViewModel?.currentUser
        user_greetings?.text = String.format(getString(R.string.salute_you), user?.fullname)

        val mainActivity = activity as MainActivity?

        mainActivity?.setActionBarTitle("Dashboard")

        user_plan?.setOnClickListener { v ->
            startMyPlansActivity()
        }
        user_aula?.setOnClickListener { v ->
            mainActivity?.changeFragment(VideoCatalogFragment(), "Aulas")
            mainActivity?.setActionBarTitle("Aulas")
        }

        user_redacoes?.setOnClickListener { v ->
            startMyEssayActivity()
        }
        user_correcoes?.setOnClickListener { v ->
            mainActivity?.changeFragment(EssayMarkListFragment(), "Sala de Correção")
            mainActivity?.setActionBarTitle("Sala de Correção")
        }
        card_layout_admin_area?.setOnClickListener {
            startMyAdminActivity()
        }
        card_store?.setOnClickListener {
            mainActivity?.changeFragment(StoreFragment(), "Loja")
            mainActivity?.setActionBarTitle("Loja")
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

    private fun startMyAdminActivity() {
        val it = Intent(activity, AdminActivity::class.java)
        startActivity(it)
    }

    private fun startMyEssayActivity() {
        val it = Intent(activity, MyEssayActivity::class.java)
        startActivity(it)
    }

    private fun startMyPlansActivity(){
        val it = Intent(activity, MyPlansActivity::class.java)
        startActivityForResult(it, REQ_CODE_MY_PLANS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE_MY_PLANS){
            val redirectToStore = data?.getBooleanExtra("redirect", false)
            if (redirectToStore == true){
                val mainActivity = activity as MainActivity?
                mainActivity?.changeFragment(StoreFragment(), "Loja")
                mainActivity?.setActionBarTitle("Loja")
            }
            val a = ""
        }
    }

    private fun checkUserTypeView() {
        val user = dashboardViewModel?.currentUser
        if (user?.userType == UserType.CORRETOR || user?.userType == UserType.ADMINISTRADOR) {
            user_correcoes?.visibility = View.VISIBLE
        } else {
            user_correcoes?.visibility = View.GONE
        }
        if (user?.userType == UserType.ADMINISTRADOR){
            card_layout_admin_area?.visibility = View.VISIBLE
        }else{
            card_layout_admin_area?.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                DashboardFragment()
    }
}
