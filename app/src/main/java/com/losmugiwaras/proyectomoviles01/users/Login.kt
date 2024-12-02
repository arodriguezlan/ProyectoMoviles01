package com.losmugiwaras.proyectomoviles01.users

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.tasks.Task
import com.losmugiwaras.proyectomoviles01.R
import java.util.Date

const val valorIntentSignup = 1

class LoginActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()

    private lateinit var btnAutenticar: Button
    private lateinit var txtGoogleSignIn: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtContra: EditText
    private lateinit var txtRegister: TextView

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        btnAutenticar = findViewById(R.id.btnAutenticar)
        txtGoogleSignIn = findViewById(R.id.txtGoogle) // Assuming you've added a Google Sign-In button in your XML
        txtEmail = findViewById(R.id.txtEmail)
        txtContra = findViewById(R.id.txtContra)
        txtRegister = findViewById(R.id.txtRegister)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))  // Use your web client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set up the Google Sign-In button
        txtGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        txtRegister.setOnClickListener {
            goToSignup()
        }

        btnAutenticar.setOnClickListener {
            if (txtEmail.text.isNotEmpty() && txtContra.text.isNotEmpty()) {
                auth.signInWithEmailAndPassword(txtEmail.text.toString(), txtContra.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            updateLastAccessTime(it.result?.user?.uid)
                        } else {
                            showAlert("Error", "Al autenticar el usuario")
                        }
                    }
            } else {
                showAlert("Error", "El correo electrónico y contraseña no pueden estar vacíos")
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        if (account != null) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        updateLastAccessTime(auth.currentUser?.uid)
                    } else {
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun updateLastAccessTime(userId: String?) {
        if (userId != null) {
            val dt: Date = Date()
            val user = hashMapOf("ultAcceso" to dt.toString())

            db.collection("datosUsuarios")
                .whereEqualTo("idemp", userId).get()
                .addOnSuccessListener { documentReference ->
                    documentReference.forEach { document ->
                        db.collection("datosUsuarios").document(document.id)
                            .update(user as Map<String, Any>)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar los datos del usuario", Toast.LENGTH_SHORT).show()
                }

            // Store user credentials locally
            val prefe = this.getSharedPreferences("appData", MODE_PRIVATE)
            val editor = prefe.edit()
            editor.putString("email", auth.currentUser?.email)
            editor.commit()

            // Success - Return to the previous activity
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun goToSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivityForResult(intent, valorIntentSignup)
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
