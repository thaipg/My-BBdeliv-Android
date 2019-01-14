package dsc.vn.mybbdeliv.Service

import android.content.Context
import android.util.Log
import com.github.kittinunf.fuel.android.core.Json
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.DataPart
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpUpload
import com.github.kittinunf.result.Result
import com.kaopiz.kprogresshud.KProgressHUD
import dsc.vn.mybbdeliv.BuildConfig
import dsc.vn.mybbdeliv.Process.UserProcess
import dsc.vn.mybbdeliv.Utils.ToastUtils
import org.json.JSONObject

/**
 * Created by thaiphan on 11/7/17.
 */

class JsonWebservice {
    var core_api_endPoint: String
    var post_api_endPoint: String
    var crm_api_endPoint: String
    var vietstar_api_endPoint: String

    init {
        if (BuildConfig.DEBUG) {
            core_api_endPoint = "http://coreapi.bbdeliv.vn/api"
            post_api_endPoint = "http://postapi.bbdeliv.vn/api"
            crm_api_endPoint = "http://crmapi.bbdeliv.vn/api"
            vietstar_api_endPoint = "http://api.bbdeliv.vn:2511/api"
        } else {
            core_api_endPoint = "http://coreapi.bbdeliv.vn/api"
            post_api_endPoint = "http://postapi.bbdeliv.vn/api"
            crm_api_endPoint = "http://crmapi.bbdeliv.vn/api"
            vietstar_api_endPoint = "http://api.bbdeliv.vn:2511/api"
        }
    }

    fun postJson(url: String, requestParam: JSONObject, callback: (String) -> Unit) {

        var userToken = ""
        if (UserProcess.token != null) {
            userToken = UserProcess.token!!.token
        }

        url.httpPost()
                .header(mapOf("Content-Type" to "application/json"))
                .header(mapOf("Authorization" to "Bearer $userToken"))
                .body(requestParam.toString())
                .responseJson { _, response, result ->
                    if (response.statusCode == 200) {
                        val (_, error) = result

                        when (result) {
                            is Result.Failure -> {
                                Log.v("Error", error!!.toString())
                            }
                            is Result.Success -> {
                                val json = Json(String(response.data)).obj()
                                if (json["isSuccess"] == 1) {
                                    callback.invoke(json["data"].toString())
                                } else {
                                    if (json["message"] != null)
                                        Log.v("Error", json["message"].toString())
                                    else
                                        Log.v("Error", json["data"].toString())
                                }
                            }
                        }
                    } else {
                        Log.v("Error", response.toString())
                    }
                }
    }

    fun postJson(applicationContext: Context, url: String, requestParam: JSONObject, callback: (String) -> Unit) {
        val hud = KProgressHUD.create(applicationContext)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Vui lòng chờ")
                .setDetailsLabel("Đang kết nối hệ thống post")
                .setMaxProgress(100)
                .show()

        var userToken = ""
        if (UserProcess.token != null) {
            userToken = UserProcess.token!!.token
        }

        url.httpPost()
                .header(mapOf("Content-Type" to "application/json"))
                .header(mapOf("Authorization" to "Bearer $userToken"))
                .body(requestParam.toString())
                .responseJson { _, response, result ->
                    hud.setProgress(80)
                    if (response.statusCode == 200) {
                        val (_, error) = result

                        when (result) {
                            is Result.Failure -> {
                                hud.dismiss()
                                ToastUtils.error(applicationContext, error!!.toString())
                                if (error.toString() == "Request failed: unauthorized (401)") {
                                    UserProcess().removeToken(applicationContext)
                                    ToastUtils.error(applicationContext, "Khu vực này chưa có mức giá dịch vụ !")
                                } else {
                                    ToastUtils.error(applicationContext, error.toString())
                                }
                                Log.v("Error", error.toString())
                            }
                            is Result.Success -> {
                                hud.dismiss()
                                val json = Json(String(response.data)).obj()
                                if (json["isSuccess"] == 1) {
                                    callback.invoke(json["data"].toString())
                                } else {
                                    if (json["message"] != null) {
                                        if (json["message"].toString() != "null") {
                                            ToastUtils.error(applicationContext, json["message"].toString())
                                            Log.v("Error", json.toString())
                                        } else {
                                            ToastUtils.error(applicationContext, json["data"].toString())
                                            Log.v("Error", json["data"].toString())
                                        }
                                    } else {
                                        ToastUtils.error(applicationContext, json["data"].toString())
                                        Log.v("Error", json["data"].toString())
                                    }
                                }
                            }
                        }
                    } else {
                        hud.dismiss()
                        ToastUtils.error(applicationContext, response.toString())
                        Log.v("Error", response.toString())
                    }
                }
    }

    fun postJsonVietstar(applicationContext: Context, urlString: String, requestParam: List<Pair<String, Any>>, callback: (String) -> Unit) {
        val hud = KProgressHUD.create(applicationContext)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Vui lòng chờ")
                .setDetailsLabel("Đang kết nối đến hệ thống Vietstar")
                .setMaxProgress(100)
                .show()
        Log.v("Request", requestParam.toString())
        urlString.httpUpload(Method.POST, requestParam)
                //Upload normally requires a file, but we can give it an empty list of `DataPart`
                .dataParts { request, url -> listOf<DataPart>() }
                .responseJson { _, response, result ->
                    if (response.statusCode == 200) {
                        val (_, error) = result

                        when (result) {
                            is Result.Failure -> {
                                hud.dismiss()
                                ToastUtils.error(applicationContext, error!!.toString())
                                if (error.toString() == "Request failed: unauthorized (401)") {
                                    UserProcess().removeToken(applicationContext)
                                    ToastUtils.error(applicationContext, "Khu vực này chưa có mức giá dịch vụ !")
                                } else {
                                    ToastUtils.error(applicationContext, error.toString())
                                }
                                Log.v("Error", error.toString())
                            }
                            is Result.Success -> {
                                hud.dismiss()
                                val json = Json(String(response.data)).obj()
                                if (json["isError"].toString() == "false") {
                                    callback.invoke(json["returnData"].toString())
                                } else {
                                    if (json["errorMessage"] != null) {
                                        ToastUtils.error(applicationContext, json["errorMessage"].toString())
                                        Log.v("Error", json.toString())
                                    } else {
                                        ToastUtils.error(applicationContext, json["errorMessage"].toString())
                                        Log.v("Error", json["returnData"].toString())
                                    }

                                }
                            }
                        }
                    } else {
                        hud.dismiss()
                        ToastUtils.error(applicationContext, response.toString())
                        Log.v("Error", response.toString())
                    }
                }
    }


    fun getJson(applicationContext: Context, url: String, callback: (String) -> Unit) {
        val hud = KProgressHUD.create(applicationContext)
                .setStyle(KProgressHUD.Style.BAR_DETERMINATE)
                .setLabel("Vui lòng chờ")
                .setDetailsLabel("Đang kết nối hệ thống post")
                .setMaxProgress(100)
                .show()

        var userToken = ""
        if (UserProcess.token != null) {
            userToken = UserProcess.token!!.token
        }

        url.httpGet()
                .header(mapOf("Authorization" to "Bearer $userToken"))
                .responseJson { _, response, result ->
                    hud.setProgress(80)
                    if (response.statusCode == 200) {
                        val (_, error) = result

                        when (result) {
                            is Result.Failure -> {
                                hud.dismiss()
                                ToastUtils.error(applicationContext, error!!.toString())
                                if (error.toString() == "Request failed: unauthorized (401)") {
                                    UserProcess().removeToken(applicationContext)
                                    ToastUtils.error(applicationContext, "Khu vực này chưa có mức giá dịch vụ !")
                                } else {
                                    ToastUtils.error(applicationContext, error.toString())
                                }
                                Log.v("Error", error.toString())
                            }
                            is Result.Success -> {
                                hud.dismiss()
                                val json = Json(String(response.data)).obj()
                                if (json["isSuccess"] == 1) {
                                    callback.invoke(json["data"].toString())
                                } else {
                                    if (json["message"] != null) {
                                        if (json["message"].toString() != "null") {
                                            ToastUtils.error(applicationContext, json["message"].toString())
                                            Log.v("Error", json.toString())
                                        } else {
                                            ToastUtils.error(applicationContext, json["data"].toString())
                                            Log.v("Error", json["data"].toString())
                                        }
                                    } else {
                                        ToastUtils.error(applicationContext, json["data"].toString())
                                        Log.v("Error", json["data"].toString())
                                    }
                                }
                            }
                        }
                    } else {
                        hud.dismiss()
                        ToastUtils.error(applicationContext, response.toString())
                        Log.v("Error", response.toString())
                    }
                }
    }

    fun getJson(url: String, callback: (String) -> Unit) {

        var userToken = ""
        if (UserProcess.token != null) {
            userToken = UserProcess.token!!.token
        }

        url.httpGet()
                .header(mapOf("Authorization" to "Bearer $userToken"))
                .responseJson { _, response, result ->
                    if (response.statusCode == 200) {
                        val (_, error) = result

                        when (result) {
                            is Result.Failure -> {
                                Log.v("Error", error.toString())
                            }
                            is Result.Success -> {
                                val json = Json(String(response.data)).obj()
                                if (json["isSuccess"] == 1) {
                                    Log.v("DATA", json["data"].toString())
                                    callback.invoke(json["data"].toString())
                                } else {
                                    if (json["message"] != null) {
                                        Log.v("Error", json.toString())
                                    } else {
                                        Log.v("Error", json["data"].toString())
                                    }
                                }
                            }
                        }
                    } else {
                        Log.v("Error", response.toString())
                    }
                }
    }
}