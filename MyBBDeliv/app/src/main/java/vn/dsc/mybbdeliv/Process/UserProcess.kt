package dsc.vn.mybbdeliv.Process

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import dsc.vn.mybbdeliv.Model.User
import dsc.vn.mybbdeliv.Model.UserDetail
import dsc.vn.mybbdeliv.SQLite.MyDatabaseOpenHelper
import dsc.vn.mybbdeliv.Service.JsonWebservice
import dsc.vn.mybbdeliv.Service.LocationTrackingService.Companion.lastLocation
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.json.JSONObject

/**
 * Created by thaiphan on 11/7/17.
 */
class UserProcess {
    companion object {
        var token: User? = null
    }

    fun signIn(applicationContext: Context, username: String, password: String, callback: (String) -> Unit) {
        val json = JSONObject()
        json.put("userName", username)
        json.put("passWord", password)

        val url = "/account/SignIn"

        val urlEndpoint = JsonWebservice().core_api_endPoint + url

        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }


    //update location service
    fun updateLocation() {
        val json = JSONObject()
        json.put("currentLat", lastLocation.latitude)
        json.put("currentLng", lastLocation.longitude)
        val url = "/account/UpdateLocation"

        val urlEndpoint = JsonWebservice().core_api_endPoint + url

        JsonWebservice().postJson(
                urlEndpoint, json
        ) {
            Log.i("Location", "Update complete : " + lastLocation.latitude.toString() + "&" + lastLocation.longitude.toString())
        }
    }

    //SQLLite Data
    fun insert(
            database: MyDatabaseOpenHelper,
            user: User
    ) {
        var db = database.writableDatabase
        database.use {
            val values = ContentValues()
            values.put("userId", user.userId)
            values.put("userName", user.userName)
            values.put("userFullName", user.userFullName)
            values.put("token", user.token)
            db.insert("User", null, values)
        }
    }

    fun getUser(database: MyDatabaseOpenHelper): User? {
        val db = database.readableDatabase
        return database.use {
            val rowParser = classParser<User>()
            if (db.select("User").parseList(rowParser).count() > 0) {
                token = db.select("User").parseList(rowParser)[0]
                return@use token
            } else {
                return@use null
            }
        }
    }

    fun removeAll(database: MyDatabaseOpenHelper) {
        val db = database.readableDatabase
        return database.use {
            val rowParser = classParser<User>()
            if (db.select("User").parseList(rowParser).count() > 0) {
                db.delete("User", "", null)
            }
        }
    }

    fun removeToken(applicationContext :Context) {
        val database = MyDatabaseOpenHelper.getInstance(applicationContext)
        val db = database.readableDatabase
        return database.use {
            val rowParser = classParser<User>()
            if (db.select("User").parseList(rowParser).count() > 0) {
                db.delete("User", "", null)
            }
        }
    }

    fun getUserAccount(applicationContext: Context, callback: (String) -> Unit) {
        val url = "/account/get?id=" + (token!!.userId)

        val urlEndpoint = JsonWebservice().core_api_endPoint + url

        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    //update token firebase
    fun updateInstanceIDToken(token: String) {
        val requestParam = JSONObject()
        requestParam.put("InstanceIDToken", token)
        val url = "/account/updateFireBaseToken"

        val urlEndpoint = JsonWebservice().core_api_endPoint + url

        JsonWebservice().postJson(
                urlEndpoint, requestParam
        ) {
            Log.v("Update Token",it)
        }
    }

    fun uploadAvatar(applicationContext: Context, uploadData: JSONObject, callback: (String) -> Unit) {
        val url = "/upload/UploadAvatarAccount"
        val urlEndpoint = JsonWebservice().core_api_endPoint + url

        JsonWebservice().postJson(
                applicationContext, urlEndpoint, uploadData
        ) {
            callback.invoke(it)
        }
    }

    fun downloadImage(applicationContext: Context, imagePath: String, callback: (String) -> Unit) {
        val url = "/upload/getImageByPath?imagePath=$imagePath"
        val urlEndpoint = JsonWebservice().core_api_endPoint + url

        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    fun update(applicationContext: Context, userDetail: UserDetail, callback: (String) -> Unit) {

        val jsonString = Gson().toJson(userDetail)
        val json = JSONObject(jsonString)

        val url = "/account/Update"
        val urlEndpoint = JsonWebservice().core_api_endPoint + url

        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    fun changePassword(applicationContext: Context, requestParam: JSONObject, callback: (String) -> Unit) {
        val url = "/account/ChangePassWord"
        val urlEndpoint = JsonWebservice().core_api_endPoint + url

        JsonWebservice().postJson(
                applicationContext, urlEndpoint, requestParam
        ) {
            callback.invoke(it)
        }
    }
}