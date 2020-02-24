package app

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.androidnetworking.AndroidNetworking
import com.tss.webserver.PbocListener
import java.net.Inet4Address
import java.net.NetworkInterface


class TssServerApplication : MultiDexApplication(), LifeCycleDelegate {

    override fun onCreate() {
        super.onCreate()
        val lifeCycleHandler = AppLifecycleHandler(this)
        registerLifecycleHandler(lifeCycleHandler)
        mPbocListener = PbocListener()
        appContext = applicationContext
        ipAddress = ipAddress()
        AndroidNetworking.initialize(getApplicationContext())
        Log.e("panya", "ipAddress : " + ipAddress)
    }

    override fun onAppBackgrounded() {
        Log.d("panya", "App in background")
    }

    override fun onAppForegrounded() {
        Log.d("panya", "App in foreground")
    }

    private fun registerLifecycleHandler(lifeCycleHandler: AppLifecycleHandler) {
        registerActivityLifecycleCallbacks(lifeCycleHandler)
        registerComponentCallbacks(lifeCycleHandler)
    }

    fun ipAddress(): String {
        var ip = ""
        try {
            val enumerationNetworkInterface = NetworkInterface.getNetworkInterfaces()
            while (enumerationNetworkInterface.hasMoreElements()) {
                val networkInterface = enumerationNetworkInterface.nextElement()
                val enumerationInetAddress = networkInterface.inetAddresses
                while (enumerationInetAddress.hasMoreElements()) {
                    val inetAddress = enumerationInetAddress.nextElement()
                    val ipAddress = inetAddress.hostAddress
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return ipAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val wifiMgr = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo

        return Formatter.formatIpAddress(wifiInfo.ipAddress)
    }

    companion object {
        var ipAddress: String = ""
        var mPbocListener: PbocListener? = null
        var appContext: Context? = null
            private set
    }
}
