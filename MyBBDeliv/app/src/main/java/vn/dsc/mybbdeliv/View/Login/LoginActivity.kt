package dsc.vn.mybbdeliv.View.Login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import dsc.vn.mybbdeliv.Model.User
import dsc.vn.mybbdeliv.Process.UserProcess
import dsc.vn.mybbdeliv.R
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_login.*
import dsc.vn.mybbdeliv.MainActivity
import dsc.vn.mybbdeliv.SQLite.MyDatabaseOpenHelper

/**
 * Created by thaiphan on 11/7/17.
 */
class LoginActivity : AppCompatActivity() {

    private val Context.database: MyDatabaseOpenHelper
        get() = MyDatabaseOpenHelper.getInstance(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.v("Database",database.databaseName)

        prepareUI()

    }

    private fun prepareUI()
    {
        setupUI(findViewById<View>(clLayout.id))

        btnLogin.setOnClickListener({
            UserProcess().signIn(
                    this,
                    editUsername.text.toString(),
                    editPassword.text.toString(),
                    {
                        var user = User.Deserializer().deserialize(it)
                        UserProcess().insert(database, user)

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
            )

        })
    }


    private fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@LoginActivity)
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            (0 until view.childCount)
                    .map { view.getChildAt(it) }
                    .forEach(this::setupUI)
        }
    }

    private fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus?.windowToken, 0)
    }
}

