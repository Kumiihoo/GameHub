package com.mariomedina.gamehub.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mariomedina.gamehub.adapter.MessageAdapter
import com.mariomedina.gamehub.databinding.ActivityMessageBinding
import com.mariomedina.gamehub.model.MessageModel
import java.text.SimpleDateFormat
import java.util.*

class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        getData(intent.getStringExtra("chat_id"))

        verifyChatId()

        binding.imageView4.setOnClickListener {
            if (binding.yourMessage.text!!.isEmpty()) {
                Toast.makeText(this, "Por favor escribe un mensaje", Toast.LENGTH_SHORT).show()
            } else {
                storeData(binding.yourMessage.text.toString())
            }
        }
    }

    private var senderId : String? = null
    private var chatId : String? = null
    private fun verifyChatId() {

        val receiverId = intent.getStringExtra("userId")
        senderId = FirebaseAuth.getInstance().currentUser!!.phoneNumber

        chatId = senderId + receiverId
        val reverseChatId = receiverId + senderId

        val reference = FirebaseDatabase.getInstance().getReference("chats")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.hasChild(chatId!!)) {
                    getData(chatId)
                } else if (snapshot.hasChild(reverseChatId)){
                    chatId = reverseChatId
                    getData(chatId)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessageActivity, "Algo ha salido mal", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun getData(chatId: String?) {

        FirebaseDatabase.getInstance().getReference("chats")
            .child(chatId!!).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val list = arrayListOf<MessageModel>()

                    for (show in snapshot.children) {
                        list.add(show.getValue(MessageModel::class.java)!!)
                    }

                    binding.recyclerView2.adapter = MessageAdapter(this@MessageActivity, list)

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MessageActivity, error.message, Toast.LENGTH_SHORT).show()
                }

            })

    }

    private fun storeData(msg: String) {

        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime: String = SimpleDateFormat("HH:mm: a", Locale.getDefault()).format(Date())

        val map = hashMapOf<String, String>()
        map["message"] = msg
        map["senderId"] = senderId!!
        map["currentTime"] = currentTime
        map["currentDate"] = currentDate


        val reference = FirebaseDatabase.getInstance().getReference("chats").child(chatId!!)


        reference.child(reference.push().key!!).setValue(map).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.yourMessage.text = null
                Toast.makeText(this, "Mensaje Enviado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Algo ha ido mal", Toast.LENGTH_SHORT).show()
            }
        }
    }
}