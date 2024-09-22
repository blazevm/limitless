package com.example.limitless

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.limitless.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.text.SimpleDateFormat
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemList: ArrayList<DataClass>
    private lateinit var itemAdapter: AdapterClass
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemList = arrayListOf()

        itemAdapter = AdapterClass(itemList)
//        itemAdapter.onItemClick = {
//            val intent = Intent(this, DetailActivity::class.java)
//            intent.putExtra("android", it)
//            startActivity(intent)
//        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = itemAdapter

        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("items")
            .get()
            .addOnSuccessListener { documents ->
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                for (document in documents) {
                    itemList.add(DataClass(
                        document.getString("photo") ?: "",
                        document.getString("description") ?: "",
                        document.getString("brand") ?: "",
                        dateFormat.format(document.getTimestamp("date")!!.toDate())
                    ))
                }

                itemAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
            }

        binding.logoutButton.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener { // user is now signed out
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                }
        }
    }
}