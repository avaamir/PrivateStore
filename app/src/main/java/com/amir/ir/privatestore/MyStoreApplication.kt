package com.amir.ir.privatestore

import android.app.Activity
import android.app.Application
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.multidex.MultiDexApplication
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.repository.persistance.cartdb.CartRepo
import com.amir.ir.privatestore.repository.persistance.userdb.UserRepo
import com.amir.ir.privatestore.repository.sharedprefrence.PrefsRepo
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.FirebaseApp
import com.najva.sdk.NajvaClient
import com.najva.sdk.NajvaConfiguration
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.conscrypt.Conscrypt
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.Security
import javax.net.ssl.SSLContext


class MyStoreApplication : MultiDexApplication() {

    private lateinit var client: NajvaClient

    //Typefaces
    val iransans: Typeface by lazy {
        ResourcesCompat.getFont(this, R.font.iransans)!!
    }
    val iransansMedium: Typeface by lazy {
        ResourcesCompat.getFont(this, R.font.iransans_medium)!!
    }
    val iransansLight: Typeface by lazy {
        ResourcesCompat.getFont(this, R.font.iransans_light)!!
    }
    val belham: Typeface by lazy {
        ResourcesCompat.getFont(this, R.font.belham)!!
    }


    override fun onCreate() {
        super.onCreate()
        initPreLollipop()
        initApiService()

        initLocalRepos()
        initNotificationConfig()

        registerApiInterceptorsCallbacks()
    }

    private fun registerApiInterceptorsCallbacks() {
        registerActivityLifecycleCallbacks(object: Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                if (activity is ApiService.InternetConnectionListener) {
                    ApiService.setInternetConnectionListener(activity)
                }
                if (activity is ApiService.OnUnauthorizedListener) {
                    ApiService.setOnUnauthorizedAction(activity)
                }
            }
            override fun onActivityPaused(activity: Activity) {
                if (activity is ApiService.InternetConnectionListener) {
                    ApiService.removeInternetConnectionListener(activity)
                }
                if (activity is ApiService.OnUnauthorizedListener) {
                    ApiService.removeUnauthorizedAction(activity)
                }
            }
            override fun onActivityStarted(activity: Activity?) {}
            override fun onActivityDestroyed(activity: Activity?) {}
            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
            override fun onActivityStopped(activity: Activity?) {}
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is ApiService.InternetConnectionListener) {
                    ApiService.setInternetConnectionListener(activity)
                }
            }
        })
    }

    private fun initApiService() {
        ApiService.setContext(this)
    }

    private fun initPreLollipop() {
        //TODO aval ba play service moshkel ro hal kon
        //TODO test this line OKHTTP //TODO use this when ever SSLException happend, then you neeeed to reset the okhttp client!!
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            Security.insertProviderAt(Conscrypt.newProvider(), 1)
            //updateAndroidSecurityProvider()
        }
    }

    private fun initNotificationConfig() {
        //FCM init
        FirebaseApp.initializeApp(applicationContext)
        //
        //Najva INIT
        val configuration = NajvaConfiguration()
        configuration.disableLocation()

        configuration.setUserSubscriptionListener { token ->
            //todo get token and save it to server
            println("debug: token from notification: $token")
        }

        //todo in tabe token ra az file mikhanad, bayad an ra dar coroutine ejra konam
        //todo String token = client.getSubscribedToken(); //in age token set nashode bashe null mishe

        configuration.setNajvaJsonDataListener { json ->
            //TODO handle json here
            println("debug: json from notification: $json")
        }
        client = NajvaClient(this, configuration)
        registerActivityLifecycleCallbacks(client)

        println("debug: notification token got before: ${client.subscribedToken}")
        ////////

        //TODO not tested //doc gofte dar mainActivity bezarid
        GlobalScope.launch(IO) {
            client.getCachedJsonData()
        }
    }

    private fun updateAndroidSecurityProvider() {
        //TODO check googleService is Available for android < 22
        try {
            ProviderInstaller.installIfNeeded(applicationContext)
            val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, null)
            sslContext.createSSLEngine()
        } catch (e: GooglePlayServicesRepairableException) {
            println("debug: GooglePlayServicesRepairableException: ${e.message}")
        } catch (e: GooglePlayServicesNotAvailableException) {
            println("debug: GooglePlayServicesNotAvailableException: ${e.message}")
        } catch (e: NoSuchAlgorithmException) {
            println("debug: NoSuchAlgorithmException: ${e.message}")
        } catch (e: KeyManagementException) {
            println("debug: KeyManagementException: ${e.message}")
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        client.onAppTerminated()
    }


    private fun initLocalRepos() {
        UserRepo.setContext(this)
        // MessageRepo.setContext(this)
        // FavoriteRepo.setContext(this)
        CartRepo.setContext(this)
        // AddressRepo.setContext(this)
        PrefsRepo.setContext(this)

        UserConfigs.setContext(this)
    }
}