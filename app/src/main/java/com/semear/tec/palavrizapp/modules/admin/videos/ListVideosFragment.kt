package com.semear.tec.palavrizapp.modules.admin.videos

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.modules.upload.UploadActivity
import com.semear.tec.palavrizapp.utils.Commons
import com.semear.tec.palavrizapp.utils.adapters.ThemesAdapter
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.interfaces.OnVideoClicked
import kotlinx.android.synthetic.main.list_videos_fragment.*


class ListVideosFragment : Fragment(), OnVideoClicked {


    private lateinit var adapter: ThemesAdapter

    private val SELECT_VIDEO = 300
    private val REQUEST_READ_STORAGE = 400
    private val REQUEST_WRITE_STORAGE = 401

    companion object {
        fun newInstance() = ListVideosFragment()
    }

    private lateinit var viewModel: ListVideosViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_videos_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListVideosViewModel::class.java)
        adapter = ThemesAdapter(this)
        setupRecyclerVideos()
        registerObservers()
        viewModel.fetchVideos()
        setupFab()
    }


    override fun onVideoClicked(v: Video) {
        val it = Intent(activity as Activity, UploadActivity::class.java)
        it.putExtra(Constants.EXTRA_VIDEO_PATH, v.path)
        it.putExtra(Constants.EXTRA_IS_EDIT, true)
        it.putExtra(Constants.EXTRA_VIDEO, v)
        startActivity(it)
    }

    private fun setupFab() {
        fab_add_video?.setOnClickListener {
            openPickVideoGllery()
        }
    }

    private fun registerObservers() {
        viewModel.videoListLiveData.observe(this, Observer {
            if (it != null) {
                progress_loading_videos?.visibility = View.GONE
                adapter.addAllVideo(it)
            }
        })
    }

    private fun setupRecyclerVideos() {
        rv_videos?.layoutManager = LinearLayoutManager(context)
        rv_videos?.adapter = adapter
    }


    fun openPickVideoGllery() {
        requestStoragePermission()
    }

    fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(activity as Activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity as Activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_STORAGE)

        } else {
            requestWriteStoragePermission()
        }
    }

    fun requestWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(activity as Activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity as Activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_STORAGE)

        } else {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, SELECT_VIDEO)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                requestWriteStoragePermission()

            }
        } else if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                val i = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(i, SELECT_VIDEO)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                val selectedVideoPath = Commons.getRealPathFromURI(activity as Activity, data!!.data!!)
                try {
                    if (selectedVideoPath == null) {
                        activity?.finish()
                    } else {
                        startUploadActivity(selectedVideoPath)
                        //mainViewModel.uploadVideo(this, selectedVideoPath, "nome.mp4");
                    }
                } catch (e: Exception) {

                    Log.d("videao", "error")
                    e.printStackTrace()
                }

            }
        }

    }

    private fun startUploadActivity(videoPath: String) {
        val activity = activity as Activity
        val it = Intent(activity, UploadActivity::class.java)
        it.putExtra(Constants.EXTRA_VIDEO_PATH, videoPath)
        startActivity(it)
    }



}
