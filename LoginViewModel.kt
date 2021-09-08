package app.ticktasker.viewmodels

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import app.ticktasker.map.FusedLocationClient
import app.ticktasker.networking.model.SignupData
import app.ticktasker.networking.model.SignupResponse
import app.ticktasker.networking.model.request.TaskDetailJsonLocationRequest
import app.ticktasker.networking.model.request.TaskDetailJsonRequest
import app.ticktasker.networking.model.request.TaskDetailJsonTaskRequest
import app.ticktasker.networking.model.request.TaskDetailsJsonResultRequest
import app.ticktasker.networkingbase.NetworkState
import app.ticktasker.utils.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import java.io.File

class LoginViewModel(ctx: Context) : MBaseViewModel(ctx) {


    var isFirstTimeOpened = false
    var isUpdateTaskClicked = false
//    var taskDetailMap: HashMap<String, Any>? = null
    var taskDetailsList: ArrayList<TaskDetailJsonTaskRequest> = ArrayList()









    var fMutableLiveData: MutableLiveData<Location> = MutableLiveData<Location>()
    //   var fLiveData : LiveData<Location> = _fMutableLiveData

    val singUpData = SignupData()
//    val taskDetailsJsonResultRequest = TaskDetailsJsonResultRequest(TaskDetailJsonRequest(
//        TaskDetailJsonLocationRequest(""),
//        TaskDetailJsonTaskRequest()
//    ))

    val sinUpEvent = MutableLiveData<NetworkState<SignupResponse>>()
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    var verificationId: String = ""
    var code: String = ""

    //*** Drop Location string
    var isDropLocationAdded: Boolean = false
    var dropLocation: String? = null
    var dropLat: String? = null
    var dropLang: String? = null
    var list: ArrayList<TaskDetailJsonLocationRequest>? = null

    //    json fields
    var pickUpLocation: String? = null
    var lat: String? = null
    var lang: String? = null
    val category: String? = null
    val task_description: String? = null
    val mobile_number: String? = null
    val image: String? = null
//

    fun checkMobile() = repo.checkMobile(
        hashMapOf(
            "mobile" to singUpData.mobileNumber,
            "country_code" to singUpData.countryCode
        )
    )

    fun signUpUser(
        mobile_number: String,
        country_code: String,
        language: String,
        device_token: String,
        device_type: String
    ) = repo.signUpUser(
        hashMapOf(
            "mobile_number" to mobile_number,
            "country_code" to country_code,
            "language" to language,
            "device_token" to device_token,
            "device_type" to device_type
        )
    )

    fun getPlaces(q: String) = repo.getPlaces(q)

    fun nearByTasker(lat: String, lang: String, filter: String) = repo.nearByTasker(
        hashMapOf(
            "lat" to lat,
            "lang" to lang,
            "filter" to filter
        )
    )

    fun bookTasker(bookTaskerMap: HashMap<String, String>) = repo.bookTasker(bookTaskerMap)

    fun scheduleTask(
        time: String?,
        date: String?,
        taskDetailsJson: ArrayList<TaskDetailJsonRequest>,
        dropLocation: String?,
        dropLat: String?,
        dropLang: String?,
        image: File?,
        userId: String?
    ) = repo.scheduleTask(
        time,
        date,
        taskDetailsJson,
        dropLocation,
        dropLat,
        dropLang,
        image,
        userId
    )

    fun cancelTask(cancelTask: HashMap<String, String>) = repo.cancelTask(cancelTask)

    fun getTaskCategory() = repo.getTaskCategory()

    fun getGuideline() = repo.getGuideline()

    fun getUserTask(user_id: String, type: String) =
        repo.getUserTask(user_id, type).cachedIn(viewModelScope)

    fun getDirections(origin: String, destination: String) = repo.getDirections(origin, destination)


    fun getFusedLocation(ctx: Context) {
        fMutableLiveData = FusedLocationClient(ctx)
    }

}

// var checkMobileEvent = LiveData<NetworkState<BaseResponse>>()
//var countryCode:String = ""
//  var phoneNumber:String = ""

//    fun mobileAlreadyExists(){
//        viewModelScope.launch {
//         val response = checkMobile(hashMapOf("mobile" to singUpData.mobileNumber.substring(3,singUpData.mobileNumber.length) , "country_code" to singUpData.mobileNumber.substring(0,3)))
//       //  checkMobileEvent = response.currentState()
//
//        }
//    }

//    fun signUpUser1() {
//        viewModelScope.launch {
//            val credential = PhoneAuthProvider.getCredential(verificationId, code)
//            val user = sigInWithPhoneAuthCredential(credential)
//            if (user != null) {
//                val token = messagingToken()
//                if (token != null) {
//                    singUpData.deviceToken = token
//                    val signupResponse = loginApi.signUpUser(singUpData)
//                    when (val state = signupResponse.currentState()) {
//                          is NetworkState.Success -> {
//                              sinUpEvent.postValue(NetworkState.Success(state.data))
//                          }
//                          is NetworkState.Failure ->{
//                              sinUpEvent.postValue(NetworkState.Failure(state.errorMessage))
//                        }
//                    }
//                }
//            }
//        }
//    }