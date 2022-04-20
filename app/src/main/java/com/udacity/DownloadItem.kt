package com.udacity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


// Data class parcelable of Downloaded item variable

@Parcelize
data class DownloadItem(
    val id: Long,
    val fileName: String,
    val status: String
) : Parcelable
