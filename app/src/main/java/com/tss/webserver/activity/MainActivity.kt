package com.tss.webserver.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import app.TssServerApplication
import com.tss.webserver.R
import com.tss.webserver.configulations.TinyWebServer
import com.tss.webserver.entity.TransectionModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), TinyWebServer.CallListener {
    val KEY_TRANSACTION = "transaction"
    val KEY_SETUP = "setup"
    var canResponse: Boolean = false
    var response = ""
    override fun onMethodCall(methodName: String?, param: TransectionModel?): String {
        Log.e("panya", "onMethodCall : " + methodName)
        if (methodName.equals(KEY_SETUP)) {
            canResponse = true
        } else if (methodName.equals(KEY_TRANSACTION)) {
            val intent = Intent(this@MainActivity, SwipeCardActivity::class.java)
            intent.putExtra("amount", param?.amount)
            intent.putExtra("id", param?.id)

//            intent.putExtra("currency", param?.currency)
//            intent.putExtra("payment_option", param?.payment_option)
//            intent.putExtra("payment_type", param?.payment_type)
//            intent.putExtra("cardholder_name", param?.cardholder_name)
//            intent.putExtra("cardholder_email", param?.cardholder_email)
//            intent.putExtra("callback_url", param?.callback_url)
//            intent.putExtra("notify_url", param?.notify_url)
//            intent.putExtra("transaction_id", param?.transaction_id)
//            intent.putExtra("kiosk_id", param?.kiosk_id)
//            intent.putExtra("to_tokenize", param?.to_tokenize)
//            intent.putExtra("token_id", param?.token_id)
//            intent.putExtra("payer_id", param?.payer_id)
//            intent.putExtra("additional_data", param?.additional_data)
//            intent.putExtra("sign", param?.sign)

            startActivityForResult(intent,101)
        }
        while (!canResponse) {
            Thread.sleep(1000)
        }
        canResponse = false
        return response
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        response = data?.getStringExtra("response").toString()
        canResponse = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txt_ip.text = TssServerApplication.ipAddress
        TinyWebServer.startServer(TssServerApplication.ipAddress, 9000, "/web/public_html", this)
    }

    override fun onDestroy() {
        super.onDestroy()
        TinyWebServer.stopServer()
    }
}
