package ru.netology.nmedia.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject


@InstallIn(SingletonComponent::class)
@Module
object FirebaseMessaging {
    @Provides
    fun provideFirebaseMessaging(): FirebaseMessaging =
        FirebaseMessaging.getInstance()
}

@InstallIn(SingletonComponent::class)
@Module
object GoogleApiAvailabilityModule {
    @Provides
    fun provideGoogleApiAvailability(): GoogleApiAvailability =
        GoogleApiAvailability.getInstance()
}

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class AppActivity : AppCompatActivity(R.layout.activity_app) {

    @Inject
    lateinit var auth: AppAuth

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    private val viewModelAuth: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = this
        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )
        }

        viewModelAuth.data.observe(this) {
            invalidateOptionsMenu()
        }

        firebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }


        checkGoogleApiAvailability()

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater:
            MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)

                menu.let {
                    it.setGroupVisible(R.id.unauthenticated,
                        !viewModelAuth.authenticated)
                    it.setGroupVisible(R.id.authenticated,
                        viewModelAuth.authenticated)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem):
                    Boolean =
                when (menuItem.itemId) {
                    R.id.posts -> {
                        findNavController(R.id.nav_host_fragment)
                            .navigate(
                                R.id.feedFragment
                            )
                        true
                    }
                    R.id.signin -> {
                        findNavController(R.id.nav_host_fragment)
                            .navigate(
                                R.id.loginFragment
                            )
                        true
                    }
                    R.id.signup -> {
                        findNavController(R.id.nav_host_fragment)
                            .navigate(
                                R.id.registerFragment,
                            )
                        true
                    }
                    R.id.signout -> {

                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("Do you want to logout ?")
                        builder.setTitle("Logout")
                        builder.setCancelable(false)
                        builder.setPositiveButton("Yes") {
                                _, _ -> auth.removeAuth()
                        }
                        builder.setNegativeButton("No") {
                                dialog, _ -> dialog.cancel()
                        }
                        val alertDialog = builder.create()
                        alertDialog.show()

                        true
                    }
                    else -> false
                }

        })
    }

    private fun checkGoogleApiAvailability() {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity,
                R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

    }

}
