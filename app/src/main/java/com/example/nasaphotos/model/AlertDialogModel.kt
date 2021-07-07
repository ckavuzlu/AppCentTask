package com.example.nasaphotos.model

import android.content.DialogInterface

data class AlertDialogModel(
    var title : Int,
    var message : Int,
    var positiveButtonText : Int? = null,
    var negativeButtonText : Int? = null,
    var positiveButtonAction: DialogInterface.OnClickListener? = null,
    var negativeButtonAction: DialogInterface.OnClickListener? = null,
)
