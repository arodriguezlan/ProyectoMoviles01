package com.losmugiwaras.proyectomoviles01.research

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.losmugiwaras.proyectomoviles01.R

class UploadPhotoFragment : Fragment() {
    private var selectedImageUri: Uri? = null

    private lateinit var btnUploadPhoto: Button
    private lateinit var btnSave: Button
    private lateinit var editTextDescription: EditText
    private lateinit var imageViewSelectedPhoto: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload_photo, container, false)

        // Inicializa las vistas con findViewById
        btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto)
        btnSave = view.findViewById(R.id.btnSave)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        imageViewSelectedPhoto = view.findViewById(R.id.imageViewSelectedPhoto)

        // Configuración de eventos para botones
        btnUploadPhoto.setOnClickListener { openGallery() }
        btnSave.setOnClickListener { savePhotoToFirebase(editTextDescription.text.toString()) }

        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                imageViewSelectedPhoto.setImageURI(selectedImageUri)
            } else {
                showMessage("No se pudo obtener la imagen seleccionada")
            }
        }
    }

    private fun savePhotoToFirebase(description: String) {
        if (selectedImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val photoRef = storageRef.child("photos/${System.currentTimeMillis()}.jpg")

            photoRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    photoRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val firestore = FirebaseFirestore.getInstance()
                        val photoData = hashMapOf(
                            "description" to description,
                            "imageUrl" to downloadUrl.toString()
                        )

                        firestore.collection("photos").add(photoData)
                            .addOnSuccessListener {
                                showMessage("Foto subida correctamente")
                            }
                            .addOnFailureListener {
                                showMessage("Error al guardar los datos: ${it.message}")
                            }
                    }
                }
                .addOnFailureListener {
                    showMessage("Error al subir la foto: ${it.message}")
                }
        } else {
            showMessage("Por favor, selecciona una foto primero")
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_CODE_GALLERY = 1001
    }
}
