package dsc.vn.mybbdeliv.Service

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import dsc.vn.mybbdeliv.Process.UserProcess

/**
 * Created by thaiphan on 1/23/18.
 */
class FirebaseInstanceIDService : FirebaseInstanceIdService()  {
    private var TAG = "FirebaseInstanceIDService"

    override fun onTokenRefresh() {
        //Get updated token
        var refreshedToken = FirebaseInstanceId.getInstance().token
        Log.v(TAG,"New Token : "+refreshedToken)
        UserProcess().updateInstanceIDToken(refreshedToken!!)
    }
}