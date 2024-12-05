package com.losmugiwaras.proyectomoviles01

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class AboutUsFragment : Fragment() {

    private lateinit var textViewAboutUs: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about_us, container, false)

        // Inicializa el TextView usando findViewById
        textViewAboutUs = view.findViewById(R.id.textViewAboutUs)

        // Aquí puedes configurar el contenido estático para mostrar sobre los desarrolladores
        textViewAboutUs.text = "Desarrollado por: \n Juan Pérez, María López, etc."

        return view
    }
}
