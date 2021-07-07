package com.example.nasaphotos.view.photolist

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nasaphotos.R
import com.example.nasaphotos.databinding.CardPhotoDetailBinding
import com.example.nasaphotos.databinding.CustomCameraNameTextBinding
import com.example.nasaphotos.databinding.FilterBottomSheetBinding
import com.example.nasaphotos.databinding.FragmPhotoListBinding
import com.example.nasaphotos.model.AlertDialogModel
import com.example.nasaphotos.model.PopupInfo
import com.example.nasaphotos.model.UIState
import com.example.nasaphotos.util.Constant.ALL
import com.example.nasaphotos.util.Constant.ARG_ROVER_TYPE
import com.example.nasaphotos.util.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoListFragment : Fragment() {
    private lateinit var photoListBinding: FragmPhotoListBinding
    private val viewModel: PhotoListViewModel by viewModels()
    private val photoListAdapter = PhotoListAdapter()
    private var currentTab: String? = null
    private var currentFilter: String = ALL
    private var popUpWindow: PopupWindow? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        photoListBinding = FragmPhotoListBinding.inflate(inflater, container, false)
        return photoListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentTab = arguments?.getString(ARG_ROVER_TYPE)
        initRecyclerView()
        initPhotoListLiveData()
        initErrorStateLiveData()
        initFilterButtonListener()
        setCardViewClickListener()
        initUIStateLiveData()
        getNewPage(false)

    }

    private fun initUIStateLiveData() {
        viewModel.uiState.observe(viewLifecycleOwner, Observer { uiState ->
            when (uiState) {
                UIState.LIVE -> {
                    liveState()
                }
                UIState.LOADING -> {
                    loadingState()
                }
            }
        })
    }

    private fun setCardViewClickListener() {
        photoListAdapter.setOnPhotoClickListener {
            it?.let {
                showPopUp(
                    PopupInfo(
                        roverName = it.rover?.name,
                        photoUrl = it.imgSrc,
                        date = it.earthDate,
                        status = it.rover?.status,
                        cameraName = it.camera?.name,
                        launchAndLandDate = activity?.getString(
                            R.string.launch_and_land_date,
                            it.rover?.launchDate,
                            it.rover?.landingDate
                        )
                    )
                )
            }

        }
    }

    private fun getNewPage(isFiltered: Boolean) {
        viewModel.getPhotos(currentTab, currentFilter, isFiltered)
    }

    private fun loadingState() {
        photoListBinding.progressBar.visibility = View.VISIBLE
    }

    private fun liveState() {
        photoListBinding.progressBar.visibility = View.INVISIBLE
    }

    private fun initRecyclerView() {
        var layoutManager = LinearLayoutManager(context)
        photoListBinding.mainRecycler.layoutManager = layoutManager
        photoListBinding.mainRecycler.setHasFixedSize(true)
        photoListBinding.mainRecycler.adapter = photoListAdapter
        photoListBinding.mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // The code below checks if a new page is needed
                if (layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 1
                    && !viewModel.isListDone()
                    && viewModel.getCurrentUIState() == UIState.LIVE
                ) {
                    getNewPage(false)
                }
            }
        })
    }

    private fun initPhotoListLiveData() {
        viewModel.photoListLiveData.observe(viewLifecycleOwner, Observer {
            liveState()
            photoListAdapter.addToList(it)
        })
    }

    private fun initErrorStateLiveData() {
        viewModel.alertDialogLiveData.observe(viewLifecycleOwner, Observer {
            createAlertDialog(it)
        })
    }


    private fun createAlertDialog(alertDialogModel: AlertDialogModel) {
        AlertDialog.Builder(this@PhotoListFragment.context)
            .setTitle(getString(alertDialogModel.title))
            .setMessage(getString(alertDialogModel.message))
            .setPositiveButton(alertDialogModel.positiveButtonText?.let { getString(it) }, alertDialogModel.positiveButtonAction)
            .setNegativeButton(alertDialogModel.negativeButtonText?.let { getString(it) }, alertDialogModel.negativeButtonAction)
            .show()
            .create()
    }

    private fun showPopUp(popupInfo: PopupInfo) {
        val view = getPopupView(popupInfo)
        setPopupWindow(view)
    }

    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun getPopupView(popupInfo: PopupInfo): View {
        val binding = CardPhotoDetailBinding.inflate(layoutInflater, null, false)
        Glide.with(binding.root.context)
            .load((popupInfo.photoUrl)?.replace("http:", "https:"))
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.imgMain)
        binding.txtCamera.text = popupInfo.cameraName
        binding.txtDate.text = popupInfo.date
        binding.txtRoverName.text = popupInfo.roverName
        binding.txtTakeoffLandDate.text = popupInfo.launchAndLandDate
        binding.txtStatus.text = popupInfo.status?.capitalize()
        binding.btnDismissPopup.setOnClickListener {
            popUpWindow?.dismiss()
        }
        return binding.root
    }

    private fun setPopupWindow(view: View) {
        val width = getScreenWidth() * 0.9
        popUpWindow = PopupWindow(view, width.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        popUpWindow?.isTouchable = true
        popUpWindow?.isOutsideTouchable = true
        popUpWindow?.animationStyle = R.style.CustomPopupAnimation
        popUpWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popUpWindow?.showAtLocation(view, Gravity.CENTER, 0, 0)

    }

    private fun initFilterButtonListener() {
        photoListBinding.btnFilter.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog() {
        val binding = FilterBottomSheetBinding.inflate(layoutInflater, null, false)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(binding.root)
        val cameraList = currentTab?.let { Utils.getRoverCameras(it) }

        cameraList?.forEach { cameraName ->
            val customTextViewBinding =
                CustomCameraNameTextBinding.inflate(layoutInflater, null, false)
            customTextViewBinding.txtCameraName.text = cameraName
            customTextViewBinding.txtCameraName.setOnClickListener {
                currentFilter = cameraName
                clearList()
                getNewPage(true)
                bottomSheetDialog.dismiss()
            }
            binding.linearCameraList.addView(customTextViewBinding.root)
        }

        bottomSheetDialog.show()
    }

    private fun clearList() {
        photoListAdapter.clearList()
    }


}