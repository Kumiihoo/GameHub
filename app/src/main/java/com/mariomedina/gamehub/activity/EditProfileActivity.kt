package com.mariomedina.gamehub.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mariomedina.gamehub.R
import com.mariomedina.gamehub.databinding.ActivityEditProfileBinding
import com.mariomedina.gamehub.model.UserModel
import com.mariomedina.gamehub.ui.ProfileFragment
import com.mariomedina.gamehub.utils.Config


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var imageUri: Uri? = null

    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {

        imageUri = it

        binding.profileImage.setImageURI(imageUri)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!).get()
            .addOnSuccessListener {

                if (it.exists()) {
                    val dataProfile = it.getValue(UserModel::class.java)

                    binding.profileName.setText(dataProfile!!.name.toString())
                    binding.profileCity.setText(dataProfile.city.toString())
                    binding.profileEmail.setText(dataProfile.email.toString())
                    binding.profileNumber.setText(dataProfile.number.toString())

                    Config.hideDialog()

                }
            }


        binding.profileImage.setOnClickListener {
            selectImage.launch("image/*")
        }

        binding.saveProfile.setOnClickListener {
            validateData()
        }

    }

    private fun validateData() {

        if (binding.profileName.text.toString().isEmpty()
            || binding.profileEmail.text.toString().isEmpty()
            || binding.profileCity.text.toString().isEmpty()
            || imageUri == null
        ) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            uploadImage()
        }
    }

    private fun uploadImage() {
        Config.showDialog(this)

        val storageRef = FirebaseStorage.getInstance().getReference("profile")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("profile.jpg")


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
        val data = UserModel(
            name = binding.profileName.text.toString(),
            image = imageUrl.toString(),
            email = binding.profileEmail.text.toString(),
            city = binding.profileCity.text.toString(),
            number = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        )

        FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
            .setValue(data).addOnCompleteListener {

                Config.hideDialog()
                if (it.isSuccessful) {
                    startActivity(Intent(this, ProfileFragment::class.java))
                    finish()
                    //Toast.makeText(this, "Datos Actualizados", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()

                }
            }
    }

}