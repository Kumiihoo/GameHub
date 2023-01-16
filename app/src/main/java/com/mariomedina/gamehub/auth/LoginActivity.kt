package com.mariomedina.gamehub.auth

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mariomedina.gamehub.MainActivity
import com.mariomedina.gamehub.R
import com.mariomedina.gamehub.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding

    val auth = FirebaseAuth.getInstance()
    private var verificationId: String? = null

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = AlertDialog.Builder(this).setView(R.layout.loading_layout)
            .setCancelable(false)
            .create()

        binding.sendOTP.setOnClickListener{
            if (binding.userNumber.text!!.isEmpty()) {
                binding.userNumber.error = "Por favor introduce un número"
            } else {
                sendOTP(binding.userNumber.text.toString())
            }
        }

        binding.verifyOTP.setOnClickListener{
            if (binding.userOTP.text!!.isEmpty()) {
                binding.userOTP.error = "Por favor introduce el Código de Seguridad"
            } else {
                verifyOTP(binding.userOTP.text.toString())
            }
        }
    }

    private fun verifyOTP(otp: String) {

        //binding.sendOTP.showLoadingButton()

        try {
            dialog.show()
        } catch (e: Exception) {
        }

        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)

        signInWithPhoneAuthCredential(credential)

    }

    private fun sendOTP(number: String) {

        //binding.sendOTP.showLoadingButton()

        dialog.show()

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                //binding.sendOTP.showNormalButton()

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                this@LoginActivity.verificationId = verificationId

                dialog.dismiss()
                //binding.sendOTP.showNormalButton()

                binding.numberLayout.visibility = GONE
                binding.OTPLayout.visibility = VISIBLE
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+34$number")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                //binding.sendOTP.showNormalButton()
                if (task.isSuccessful) {

                    checkUserExist(binding.userNumber.text.toString())

//                    startActivity(Intent(this, MainActivity::class.java))
//                    finish()
                } else {

                    dialog.dismiss()
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserExist(number: String) {

        FirebaseDatabase.getInstance().getReference("users").child("+34$number")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    dialog.dismiss()
                    Toast.makeText(this@LoginActivity, p0.message, Toast.LENGTH_SHORT).show()

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()) {
                        try {
                            dialog.dismiss()
                        } catch (e: Exception) {
                        }
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                        finish()
                    }
                }

            })

    }

}