package com.palavrizar.tec.palavrizapp.modules.essay.essay_mark_list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Essay
import com.palavrizar.tec.palavrizapp.modules.essay.essay_view_professor.EssayViewActivity
import com.palavrizar.tec.palavrizapp.utils.adapters.EssayListAdapter
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.EXTRA_ESSAY
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnEssayClicked
import kotlinx.android.synthetic.main.fragment_essay_mark.*

class EssayMarkListFragment : Fragment(), OnEssayClicked {


    private var essayMarkViewModel: EssayMarkListViewModel? = null
    private val adapter = EssayListAdapter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       return inflater.inflate(R.layout.fragment_essay_mark, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onResume() {
        setupView()
        initViewModel()
        registerObservers()
        getEssayList()
        super.onResume()
    }

    private fun getEssayList() {
        essayMarkViewModel?.getEssayList()
    }

    fun initViewModel(){
        essayMarkViewModel = ViewModelProviders.of(this).get(EssayMarkListViewModel::class.java)
    }

    private fun setupView(){
        recyclerEssayList.layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerEssayList.adapter = adapter
    }

    private fun registerObservers(){
        essayMarkViewModel?.essayList?.observe(this, Observer {
            //var listOfEssays = it.values.
            val hashEssayList = it

            if (hashEssayList != null && hashEssayList.isNotEmpty()) {

                adapter.essayList = hashEssayList
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onEssayClicked(essay: Essay) {
        val it = Intent(activity, EssayViewActivity::class.java)
        it.putExtra(EXTRA_ESSAY, essay)
        activity?.startActivity(it)
    }
}
