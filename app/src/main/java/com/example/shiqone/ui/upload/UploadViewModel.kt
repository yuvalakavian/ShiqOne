package com.example.shiqone.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UploadViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Upload your Shiq Post Now :)"
    }
    val text: LiveData<String> = _text
}