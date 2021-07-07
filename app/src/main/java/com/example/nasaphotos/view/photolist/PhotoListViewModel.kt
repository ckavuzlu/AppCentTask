package com.example.nasaphotos.view.photolist

import android.content.DialogInterface
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaphotos.R
import com.example.nasaphotos.model.*
import com.example.nasaphotos.network.ApiInterface
import com.example.nasaphotos.util.Constant.ALL
import com.example.nasaphotos.util.Constant.NASA_API_KEY
import com.example.nasaphotos.util.Constant.SOL_NUMBER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PhotoListViewModel @Inject constructor(
    private val apiInterface: ApiInterface
) : ViewModel() {

    private val TAG = "PhotosViewModel"
    private var nextPage = 0
    private var isListDone: Boolean = false

    private val _photoListLiveData: MutableLiveData<List<PhotosItem?>> = MutableLiveData()
    val photoListLiveData: LiveData<List<PhotosItem?>> = _photoListLiveData

    private val _alertDialogLiveData: MutableLiveData<AlertDialogModel> = MutableLiveData()
    val alertDialogLiveData: LiveData<AlertDialogModel> = _alertDialogLiveData

    private val _uiState: MutableLiveData<UIState> = MutableLiveData()
    val uiState: LiveData<UIState> = _uiState


    private fun getCameraFilter(cameraName: String): String? {
        return if (cameraName == ALL) {
            null
        } else {
            cameraName
        }
    }

    fun getPhotos(roverType: String?, cameraName: String? = null, isFiltered: Boolean = false) {
        changeUIState(UIState.LOADING)

        if (roverType.isNullOrEmpty()) {
            this.resolveError(ErrorType.BAD_REQUEST)
            Log.e(TAG, "roverType is null or empty")
            return
        }

        val cameraFilter = cameraName?.let { getCameraFilter(it) }
        updateLastPage(isFiltered)

        viewModelScope.launch(Dispatchers.IO) {
            apiInterface.getRoverPhotos(roverType, SOL_NUMBER, nextPage, NASA_API_KEY, cameraFilter)
                .enqueue(object : Callback<NasaResponse> {
                    override fun onResponse(call: Call<NasaResponse>, response: Response<NasaResponse>) {
                        changeUIState(UIState.LIVE)
                        if (response.isSuccessful) {
                            if (response.body()?.photos?.isNotEmpty() == true) {
                                _photoListLiveData.postValue(response.body()?.photos)
                            } else {
                                this@PhotoListViewModel.resolveError(ErrorType.END_OF_LIST)
                            }
                        } else {
                            this@PhotoListViewModel.resolveError(ErrorType.REQUEST_ERROR)
                            Log.e(TAG, "Request is not successful.")
                            return
                        }
                    }

                    override fun onFailure(call: Call<NasaResponse>, t: Throwable) {
                        changeUIState(UIState.LIVE)
                        this@PhotoListViewModel.resolveError(ErrorType.REQUEST_ERROR)
                        Log.e(TAG, "Request is failure.")
                        return
                    }

                })
        }
    }

    private fun updateLastPage(isClear: Boolean = false) {
        if (isClear) {
            nextPage = 1
            isListDone = false
            return
        }
        nextPage++
    }

    private fun changeUIState(newState: UIState) {
        _uiState.postValue(newState)
    }

    private fun resolveError(errorType: ErrorType) {
        when (errorType) {
            ErrorType.END_OF_LIST -> {
                changeUIState(UIState.LIVE)
                isListDone = true
            }
            ErrorType.REQUEST_ERROR, ErrorType.BAD_REQUEST -> {
                changeUIState(UIState.LIVE)
                _alertDialogLiveData.postValue(
                    AlertDialogModel(
                        title = R.string.error,
                        message = R.string.something_went_wrong,
                        positiveButtonText = R.string.ok,
                        positiveButtonAction = DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()
                        })
                )

            }
        }
    }

    fun isListDone(): Boolean {
        return isListDone
    }

    fun getCurrentUIState(): UIState? {
        return uiState.value
    }


}