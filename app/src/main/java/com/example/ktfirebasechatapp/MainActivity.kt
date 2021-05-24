package com.example.ktfirebasechatapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var messageListView: ListView
    private lateinit var adapter: MessageAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var sendImageButton: ImageButton
    private lateinit var sendMessageButton: Button
    private lateinit var messageEditText: EditText
    private lateinit var username: String
    var database: FirebaseDatabase? = null
    var messagesDatabaseReference: DatabaseReference? = null
    var messagesChildEventListener: ChildEventListener? = null
    private val mAuth: FirebaseAuth? = null
    var userDatabaseReference: DatabaseReference? = null
    //private var i: Intent? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progessBar)
        sendImageButton = findViewById(R.id.sendPhotoButton)
        sendMessageButton = findViewById(R.id.sendMessageButton)
        messageEditText = findViewById(R.id.messageEditText)
        progressBar.setVisibility(View.VISIBLE)
        val i = getIntent()

        val uid = i.getStringExtra("userId")
        userDatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        userDatabaseReference!!.child(uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userClass: User? = snapshot.getValue<User>(User::class.java)
                if (userClass != null) {
                    username = userClass.fullName.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })


        //realtime database
        database = FirebaseDatabase.getInstance()
        messagesDatabaseReference = database!!.reference.child("messages")
        //
        val messageModels: List<MessageModel?> = ArrayList()
        messageListView = findViewById(R.id.messageListView)
        adapter = MessageAdapter(this, R.layout.message_item, messageModels)
        messageListView.setAdapter(adapter)


        // добавляем для того чтобы реализовать у edittextview аттрибут правильно (т.е чтобы кнопка была активна только при наличии текста)
        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //когда текст внутри view поменялся
                if (s.toString().trim { it <= ' ' }.length > 0) {
                    sendMessageButton.setEnabled(true)
                } else {
                    sendMessageButton.setEnabled(true)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })


        //ограничение символов в одном сообщении
        messageEditText.setFilters(arrayOf<InputFilter>(LengthFilter(500)))


        //giving listeners for btns
        sendMessageButton.setOnClickListener(View.OnClickListener {
            val message = MessageModel(messageEditText.getText().toString(), username, null)
            messagesDatabaseReference!!.push().setValue(message)
            messageEditText.setText("")
        })
        sendImageButton.setOnClickListener(View.OnClickListener { })


        //листенер потомков для отображения сообщений в приложении
        messagesChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //когда добавляется потомок
                val message: MessageModel? = snapshot.getValue<MessageModel>(MessageModel::class.java) // в атрибуте узказываем в каком классе можно расспознать
                adapter!!.add(message)
                progressBar.setVisibility(View.GONE)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //когда изменяется потомок
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                //когда произошла ошибка в базе данных
            }
        }
        messagesDatabaseReference!!.addChildEventListener(messagesChildEventListener as ChildEventListener)
    }
}


