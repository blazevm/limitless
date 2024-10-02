package com.example.limitless

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.limitless.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

        // Initialize the RecyclerView Adapter with the empty list
        itemAdapter = AdapterClass(itemList)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = itemAdapter

        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return  // Ensure user is logged in

        // Fetch data from Firestore
        db.collection("users").document(userId).collection("items")
            .get()
            .addOnSuccessListener { documents ->
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                for (document in documents) {
                    // Extract data from Firestore document, including the image URL
                    val imageUrl = document.getString("imageUrl") ?: ""  // Fetch image URL from Firestore
                    val description = document.getString("description") ?: ""
                    val brand = document.getString("brand") ?: ""
                    val timestamp = document.getTimestamp("date")?.toDate()
                    val formattedDate = timestamp?.let { dateFormat.format(it) } ?: ""

                    // Add the fetched data to itemList
                    itemList.add(DataClass(imageUrl, description, brand, formattedDate))
                }

                // Notify the adapter that the data set has changed
                itemAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
                Toast.makeText(this, "Failed to load data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // Button to navigate to AddDataActivity
        val buttonToAddData = findViewById<FloatingActionButton>(R.id.buttonToAddData)
        buttonToAddData.setOnClickListener {
            val intent = Intent(this, AddDataActivity::class.java)
            startActivity(intent)  // Navigate to AddDataActivity
        }

        // Logout button functionality
        binding.logoutButton.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                }
        }
    }
}
