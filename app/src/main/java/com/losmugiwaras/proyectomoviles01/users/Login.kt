package com.losmugiwaras.proyectomoviles01.users

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.losmugiwaras.proyectomoviles01.R
import java.util.Date

class LoginActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var btnAutenticar: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtContra: EditText
    private lateinit var txtRegister: TextView
    private lateinit var txtGoogle: TextView // TextView para Google Sign-In

    companion object {
        private const val RC_SIGN_IN = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        btnAutenticar = findViewById(R.id.btnAutenticar)
        txtEmail = findViewById(R.id.txtEmail)
        txtContra = findViewById(R.id.txtContra)
        txtRegister = findViewById(R.id.txtRegister)
        txtGoogle = findViewById(R.id.txtGoogle) // Asumiendo que existe en el layout

        configureGoogleSignIn()

        txtRegister.setOnClickListener {
            goToSignup()
        }

        btnAutenticar.setOnClickListener {
            loginWithEmail()
        }

        txtGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Coloca tu ID de cliente
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun loginWithEmail() {
        if (txtEmail.text.isNotEmpty() && txtContra.text.isNotEmpty()) {
            auth.signInWithEmailAndPassword(txtEmail.text.toString(), txtContra.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        saveLoginInfoToFirestore()
                        saveLoginInfoToLocal()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        showAlert("Error", "Al autenticar el usuario")
                    }
                }
        } else {
            showAlert("Error", "El correo electrónico y contraseña no pueden estar vacíos")
        }
    }

    private fun saveLoginInfoToFirestore() {
        val dt = Date()
        val user = hashMapOf("ultAcceso" to dt.toString())
        auth.currentUser?.uid?.let { uid ->
            db.collection("datosUsuarios")
                .whereEqualTo("idemp", uid).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        db.collection("datosUsuarios").document(document.id)
                            .update(user as Map<String, Any>)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Error al actualizar los datos del usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun saveLoginInfoToLocal() {
        val prefe = getSharedPreferences("appData", Context.MODE_PRIVATE)
        val editor = prefe.edit()
        editor.putString("email", txtEmail.text.toString())
        editor.putString("contra", txtContra.text.toString())
        editor.apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                showAlert("Error", "Error al iniciar sesión con Google")
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveLoginInfoToFirestore()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    showAlert("Error", "Error al autenticar con Google")
                }
            }
    }

    private fun goToSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    private fun showAlert(title: String, message: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .create()
        dialog.show()
    }
}
