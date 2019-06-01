package com.semear.tec.palavrizapp.modules.essay.essay_view_professor.essay_correct_room

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Essay
import com.semear.tec.palavrizapp.utils.constants.Constants


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EssayCorrectFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EssayCorrectFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class EssayCorrectFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    private var viewmodel: EssayCorrectViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        arguments?.let {
            viewmodel?.setExtras(it)
        }
    }

    private fun initViewModel() {
        viewmodel = ViewModelProviders.of(this).get(EssayCorrectViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       // registerObservers()
    }

    private fun registerObservers() {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_essay_correct, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(essay: Essay) =
                EssayCorrectFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(Constants.EXTRA_ESSAY, essay)
                    }
                }
    }

}
