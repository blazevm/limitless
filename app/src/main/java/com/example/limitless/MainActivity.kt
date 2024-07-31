package com.example.limitless

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.limitless.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>
    lateinit var imageList: Array<Int>
    lateinit var titleList: Array<String>
    lateinit var descList: Array<String>
    lateinit var detailImageList: Array<Int>
    private lateinit var myAdapter: AdapterClass
    private lateinit var searchList: ArrayList<DataClass>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageList = arrayOf(
            R.drawable.ic_list,
            R.drawable.ic_checkbox,
            R.drawable.ic_image,
            R.drawable.ic_toggle,
            R.drawable.ic_date,
            R.drawable.ic_rating,
            R.drawable.ic_time,
            R.drawable.ic_text,
            R.drawable.ic_edit,
            R.drawable.ic_camera
        )
        titleList = arrayOf(
            "ListView",
            "CheckBox",
            "ImageView",
            "Toggle Switch",
            "Date Picker",
            "Rating Bar",
            "Time Picker",
            "TextView",
            "EditText",
            "Camera"
        )
        descList = arrayOf(
            getString(R.string.listview),
            getString(R.string.checkbox),
            getString(R.string.imageview),
            getString(R.string.toggle),
            getString(R.string.date),
            getString(R.string.rating),
            getString(R.string.time),
            getString(R.string.textview),
            getString(R.string.edit),
            getString(R.string.camera)
        )
        detailImageList = arrayOf(
            R.drawable.list_detail,
            R.drawable.check_detail,
            R.drawable.image_detail,
            R.drawable.toggle_detail,
            R.drawable.date_detail,
            R.drawable.rating_detail,
            R.drawable.time_detail,
            R.drawable.text_detail,
            R.drawable.edit_detail,
            R.drawable.camera_detail
        )
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        dataList = arrayListOf<DataClass>()
        searchList = arrayListOf<DataClass>()
        getData()

        myAdapter = AdapterClass(searchList)
        recyclerView.adapter = myAdapter
        myAdapter.onItemClick = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("android", it)
            startActivity(intent)
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

    private fun getData() {
        for (i in imageList.indices) {
            val dataClass = DataClass(imageList[i], titleList[i], descList[i], detailImageList[i])
            dataList.add(dataClass)
        }
        searchList.addAll(dataList)
        recyclerView.adapter = AdapterClass(searchList)
    }


    val db = Firebase.firestore
}