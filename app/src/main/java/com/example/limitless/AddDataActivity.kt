package com.example.limitless

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddDataActivity : AppCompatActivity() {

    private lateinit var descriptionEditText: EditText
    private lateinit var brandEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var imageUri: Uri

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    private val calendar = Calendar.getInstance()

    companion object {
        const val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        descriptionEditText = findViewById(R.id.editTextDescription)
        brandEditText = findViewById(R.id.editTextBrand)
        dateEditText = findViewById(R.id.editTextDate)
        imagePreview = findViewById(R.id.imagePreview)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        // Set up image selection from gallery
        findViewById<Button>(R.id.buttonUploadImage).setOnClickListener {
            selectImageFromGallery()
        }

        // Show DatePickerDialog when the date field is clicked
        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Handle the Submit button to upload data to Firestore
        findViewById<Button>(R.id.buttonSubmit).setOnClickListener {
            uploadDataToFirestore()
        }
    }

    // Step 2: Function to show a DatePickerDialog
    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                updateDateEditText() // Update the EditText with the selected date
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Function to format the selected date and display it in the EditText
    private fun updateDateEditText() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateEditText.setText(dateFormat.format(calendar.time))
    }

    // Function to select image from gallery
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    // Handle result from the image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data!!
            imagePreview.setImageURI(imageUri)
        }
    }

    // Function to upload image and store data in Firestore
    private fun uploadDataToFirestore() {
        val userId = firebaseAuth.currentUser?.uid
        val description = descriptionEditText.text.toString().trim()
        val brand = brandEditText.text.toString().trim()

        // Convert the selected date from the EditText to Firestore Timestamp
        val selectedDate = calendar.time // Get the selected date from the DatePicker
        val date = FieldValue.serverTimestamp() // Use server timestamp or create logic for selected date timestamp

        if (imageUri != null && userId != null) {
            val imageRef = storageReference.child("images/$userId/${UUID.randomUUID()}.jpg")
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                        // Data to be added to Firestore
                        val data = hashMapOf(
                            "description" to description,
                            "brand" to brand,
                            "date" to date, // Use Firestore timestamp or your selected date
                            "imageUrl" to imageUrl.toString(),
                            "userId" to userId
                        )

                        // Store the data inside the 'items' collection of the user document
                        firestore.collection("users").document(userId)
                            .collection("items")
                            .add(data)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Data added successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to add data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }
}
