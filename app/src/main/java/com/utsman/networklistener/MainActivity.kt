package com.utsman.networklistener

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainViewModel = ViewModelProviders.of(this)[MainViewModel::class.java]

        /*mainViewModel.getListenerNetwork().observe(this, Observer { isConnected ->
            toast("connected -> $isConnected")
        })

        mainViewModel.getTypeName().observe(this, Observer { typeName ->
            toast("")
        })*/

        mainViewModel.getType().observe(this, Observer { typeModel ->
            toast("connect --> ${typeModel.connect} -- ${typeModel.connectType}")
        })
    }
}

data class TypeModel(val connect: Boolean,
                     val connectType: String)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    private val isConnected: MutableLiveData<Boolean> = MutableLiveData()
    private val connectivityTypeName: MutableLiveData<String> = MutableLiveData()

    private val typeModel: MutableLiveData<TypeModel> = MutableLiveData()

    init {
        val rxNetwork = ReactiveNetwork.observeNetworkConnectivity(application)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { connect ->
                isConnected.postValue(connect.available())
                connectivityTypeName.postValue(connect.typeName())

                val type = TypeModel(connect.available(), connect.typeName())
                typeModel.postValue(type)
            }

        disposable.add(rxNetwork)
    }


    fun getListenerNetwork() = isConnected
    fun getTypeName() = connectivityTypeName
    fun getType() = typeModel
}


fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()