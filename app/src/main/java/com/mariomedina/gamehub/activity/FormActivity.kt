package com.mariomedina.gamehub.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mariomedina.gamehub.databinding.ActivityFormBinding
import com.mariomedina.gamehub.model.FormModel
import com.mariomedina.gamehub.utils.Config

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private var imageUri: Uri? = null

    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {

        imageUri = it

        binding.formImage.setImageURI(imageUri)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormBinding.inflate(layoutInflater)

        setContentView(binding.root)

        FirebaseDatabase.getInstance().getReference("forms")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!).get()
            .addOnSuccessListener {

                if (it.exists()) {
                    val dataForm = it.getValue(FormModel::class.java)

                    binding.formName.setText(dataForm!!.formName.toString())
                    binding.formEmail.setText(dataForm.formEmail.toString())
                    binding.formNumber.setText(dataForm.formSenderId.toString())
                }
            }


        binding.formImage.setOnClickListener {
            selectImage.launch("image/*")
        }

        binding.formSend.setOnClickListener {
            validateData()
        }

    }

    private fun validateData() {

        if (binding.formName.text.toString().isEmpty()
            || binding.formEmail.text.toString().isEmpty()
            || binding.formTitle.text.toString().isEmpty()
            || binding.formMessage.text.toString().isEmpty()
            || binding.formNumber.text.toString().isEmpty()
            || imageUri == null
        ) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            uploadImage()
        }
    }

    private fun uploadImage() {
        Config.showDialog(this)

        val storageRef = FirebaseStorage.getInstance().getReference("form")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("formImage.jpg")


        storageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    storeData(it)
                }.addOnFailureListener {
                    Config.hideDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData(imageUrl: Uri?) {
        val data = FormModel(
            formName = binding.formName.text.toString(),
            message = binding.formMessage.text.toString(),
            formEmail = binding.formEmail.text.toString(),
            title = binding.formTitle.text.toString(),
            snapshot = imageUrl.toString(),
            formSenderId = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        )

        FirebaseDatabase.getInstance().getReference("forms")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
            .setValue(data).addOnCompleteListener {

                Config.hideDialog()
                if (it.isSuccessful) {
                    finish()
                    Toast.makeText(this, "Correo Enviado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()

                }
            }
    }

}
