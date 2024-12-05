package com.losmugiwaras.proyectomoviles01.users

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.losmugiwaras.proyectomoviles01.R
import java.util.Date

class SignupActivity : AppCompatActivity() {
    var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()

    private lateinit var txtRNombre: EditText
    private lateinit var txtREmail: EditText
    private lateinit var txtRContra: EditText
    private lateinit var txtRreContra: EditText
    private lateinit var btnRegistrarU: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signup)
        txtRNombre = findViewById(R.id.txtRNombre)
        txtREmail = findViewById(R.id.txtREmail)
        txtRContra = findViewById(R.id.txtRContra)
        txtRreContra = findViewById(R.id.txtRreContra)
        btnRegistrarU = findViewById(R.id.btnRegistrarU)

        btnRegistrarU.setOnClickListener {
            registrarUsuario()
        }

    }

    private fun registrarUsuario() {
        val nombre = txtRNombre.text.toString()
        val email = txtREmail.text.toString()
        val contra = txtRContra.text.toString()
        val reContra = txtRreContra.text.toString()

        if (nombre.isEmpty() || email.isEmpty() || contra.isEmpty() || reContra.isEmpty()) {
            Toast.makeText(this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            if (contra == reContra) {
                auth.createUserWithEmailAndPassword(email, contra)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val dt: Date = Date()
                            val user = hashMapOf(
                                "idemp" to task.result?.user?.uid,
                                "usuario" to nombre,
                                "email" to email,
                                "ultAcceso" to dt.toString(),
                            )
                            db.collection("datosUsuarios")
                                .add(user)
                                .addOnSuccessListener { documentReference ->

                                    //Register the data into the local storage
                                    val prefe = this.getSharedPreferences("appData", Context.MODE_PRIVATE)

                                    //Create editor object for write app data
                                    val editor = prefe.edit()

                                    //Set editor fields with the new values
                                    editor.putString("email", email.toString())
                                    editor.putString("contra", contra.toString())

                                    //Write app data
                                    editor.commit()

                                    Toast.makeText(this,"Usuario registrado correctamente",Toast.LENGTH_SHORT).show()

                                    Intent().let {
                                        setResult(Activity.RESULT_OK)
                                        finish()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this,"Error al registrar usuario",Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this,"Error al registrar usuario",Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
