package com.example.locationinbackgroundwithforegroundservice

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.locationinbackgroundwithforegroundservice.location.LocationService
import com.example.locationinbackgroundwithforegroundservice.workmanager.MainWorker
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private val SMS_CONSENT_REQUEST = 1266
    lateinit var edittext_otp: TextInputEditText

    private val smsVerificationReceiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {

            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action){
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when(smsRetrieverStatus.statusCode){
                    CommonStatusCodes.SUCCESS->{

                        //get consent intent
                        val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)

                        try {
                            //Start activity to request permission from user via dialog, you 5 minute or else time out intent
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST)
                        }catch (e: Exception){
                            Toast.makeText(this@MainActivity, "Error ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    CommonStatusCodes.TIMEOUT->{
                        Toast.makeText(this@MainActivity, "Time Out", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }


    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WorkManager.getInstance(this).beginUniqueWork("update_data", ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(MainWorker::class.java)
        ).enqueue()






        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 0)

        val startTrackingBtn = findViewById<Button>(R.id.button1)
        val stopTrackingBtn = findViewById<Button>(R.id.button2)
        val helloText = findViewById<TextView>(R.id.hello)
        edittext_otp = findViewById<TextInputEditText>(R.id.otp_input)

       // helloText.text = getString(R.string.text)
        helloText.text = resources.getText(R.string.coloured_text)


        startTrackingBtn.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                startService(this)
            }
        }

        stopTrackingBtn.setOnClickListener {
             Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }

        }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter, SmsRetriever.SEND_PERMISSION, null)

        listenForAutoFill()

    }



    private fun listenForAutoFill() {
        SmsRetriever.getClient(this)
            .startSmsUserConsent(null)
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    Toast.makeText(this@MainActivity, "SMS task listener successful", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@MainActivity, "SMS task listener failed", Toast.LENGTH_SHORT).show()
                }
            }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            SMS_CONSENT_REQUEST->
                if (resultCode == Activity.RESULT_OK && data != null){
                    //there's an sms content gotten
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)

                    //from sms get only Numbers
                    val otpCode = message?.filter { it.isDigit() }.orEmpty()

                    //populate edittext with otp value
                    edittext_otp.setText(otpCode)

                    //set edittext cursor to the end of the numbers
                    edittext_otp.setSelection(otpCode.length)
                }else{
                    Toast.makeText(this@MainActivity, "Consent Denied, You can manully type your OTP", Toast.LENGTH_SHORT).show()
                }

        }
    }
}