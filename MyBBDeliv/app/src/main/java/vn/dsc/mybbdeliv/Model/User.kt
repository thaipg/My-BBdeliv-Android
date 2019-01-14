package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Created by thaiphan on 11/7/17.
 */
//User Model
data class User(val userId: Int,
                val userName: String,
                val userFullName: String,
                val token: String) {

    //User Deserializer
    class Deserializer : ResponseDeserializable<User> {
        override fun deserialize(content: String) = Gson().fromJson(content, User::class.java)
    }

}

data class UserDetail(var id: Int,
                      var concurrencyStamp: String?,
                      var userName: String,
                      var fullName: String,
                      var phoneNumber: String?,
                      var email: String?,
                      var code: String?,
                      var hubId: Int?,
                      var roleId: Int?,
                      var roleIds: List<Int>?,
                      var roles: List<Role>?,
                      var departmentId: Int?,
                      var avatarPath: String?,
                      var isEnabled: Boolean) {

    //User Deserializer
    class Deserializer : ResponseDeserializable<UserDetail> {
        override fun deserialize(content: String) = Gson().fromJson(content, UserDetail::class.java)
    }
}

data class Role(val parrentRoleId: Int?,
                val code: String,
                val name: String,
                val id: String) {

    //User Deserializer
    class Deserializer : ResponseDeserializable<User> {
        override fun deserialize(content: String) = Gson().fromJson(content, User::class.java)
    }

}