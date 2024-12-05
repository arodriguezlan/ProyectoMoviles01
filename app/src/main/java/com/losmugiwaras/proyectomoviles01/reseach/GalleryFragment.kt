package com.losmugiwaras.proyectomoviles01.reseach

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.losmugiwaras.proyectomoviles01.R

class GalleryFragment : Fragment() {
    private lateinit var photoList: MutableList<String> // Lista para almacenar URLs de fotos
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)

        photoList = mutableListOf()
        recyclerView = view.findViewById(R.id.recyclerView) // Encuentra el RecyclerView en el diseño
        recyclerView.layoutManager = GridLayoutManager(context, 3) // Configura un GridLayoutManager
        photoAdapter = PhotoAdapter(photoList) // Inicializa el adaptador
        recyclerView.adapter = photoAdapter // Asocia el adaptador al RecyclerView

        // Cargar fotos desde Firebase Storage
        fetchPhotos()

        return view
    }

    private fun fetchPhotos() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("eventPhotos")

        // Obtiene la lista de fotos almacenadas en el directorio "eventPhotos"
        storageRef.listAll().addOnSuccessListener { result ->
            photoList.clear()
            for (item in result.items) {
                // Obtiene la URL de descarga de cada foto
                item.downloadUrl.addOnSuccessListener { uri ->
                    photoList.add(uri.toString()) // Agrega la URL a la lista
                    photoAdapter.notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
                }
            }
        }.addOnFailureListener { exception ->
            // Maneja cualquier error al listar las fotos
            exception.printStackTrace()
        }
    }

    // Adapter para el RecyclerView que manejará las fotos
    inner class PhotoAdapter(private val photos: List<String>) :
        RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_photo, parent, false) // Asegúrate de tener este layout
            return PhotoViewHolder(view)
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            val photoUrl = photos[position]
            // Convertir la URL a un URI
            val uri = Uri.parse(photoUrl)
            holder.imageView.setImageURI(uri) // Cargar la imagen directamente en el ImageView
        }

        override fun getItemCount(): Int = photos.size

        // ViewHolder para cada item de la galería
        inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.imageView) // Asegúrate de tener un ImageView en el layout
        }
    }
}
