package com.lugares_j

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lugares_j.databinding.ActivityMainBinding
import com.lugares_j.databinding.ActivityMainBinding.inflate


class MainActivity : AppCompatActivity() {

    // Definicion del objeto para hacer la autenticacion
    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityMainBinding // Sirve como enlace al archivo activity_main.xml

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater) // Debe colocarse siempre que creo un proyecto nuevo
        setContentView(binding.root)                            // Debe colocarse siempre que creo un proyecto nuevo
        FirebaseApp.initializeApp(this)      // Debe colocarse siempre que creo un proyecto nuevo
        auth = Firebase.auth                         // Debe colocarse siempre que creo un proyecto nuevo

        binding.btRegister.setOnClickListener{ haceRegistro ()} // Le asigna al boton Register la funcion de registro
        binding.btLogin.setOnClickListener{ haceLogin ()} // Le asigna al boton Login la funcion de login

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)  // Autenticacion con google
            .requestIdToken(getString(R.string.default_web_client_idr))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        binding.btGoogle.setOnClickListener{ googleSignIn()}

    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent,5000)
    }

    override fun onActivityResult(requestCode: Int,resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 5000){
            val tarea = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = tarea.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(cuenta.idToken)
            }catch (e: ApiException){

            }


        }

    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credenciales = GoogleAuthProvider.getCredential(idToken,null)

        auth.signInWithCredential(credenciales)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful){ // Si pudo crear el usuario
                    Log.d("Autenticando usuario","usuario autenticado")
                    val user = auth.currentUser // Recupero informacion del usuario creado
                    actualiza(user) //
                }else{
                    Log.d("Autenticando usuario","Error autenticando usuario")
                }
                actualiza(null) // Asigno la variable a la funcion actualiza para validar que el usuario pudo auntenticarse

            }
    }

    private fun haceRegistro(){
        // Obtenemos informacion que ingresa usuario
        val email = binding.etEmail.text.toString()  // Obtiene el texto del campo email
        val clave = binding.etClave.text.toString()  // Obtiene el texto del campo clave

        //Se llama a la funcion para crear un usuario en Firebase (correo/contrasena)
        auth.createUserWithEmailAndPassword(email,clave)
            .addOnCompleteListener(this){ task ->
                val user : FirebaseUser?=null
                if(task.isSuccessful){ // Si pudo crear el usuario
                    Log.d("Autenticando usuario","usuario creado")
                    val user = auth.currentUser // Recupero informacion del usuario creado
                }else{
                    Log.d("Autenticando usuario","Error creando usuario")
                }
                actualiza(user) // Asigno la variable a la funcion actualiza para validar que el usuario pudo auntenticarse

            }



    }

    private fun haceLogin(){
        // Obtenemos informacion que ingresa usuario
        val email = binding.etEmail.text.toString()  // Obtiene el texto del campo email
        val clave = binding.etClave.text.toString()  // Obtiene el texto del campo clave

        //Se llama a la funcion para iniciarsesion con un usuario en Firebase (correo/contrasena)
        auth.signInWithEmailAndPassword(email,clave)
            .addOnCompleteListener(this){ task ->
                val user : FirebaseUser?=null
                if(task.isSuccessful){ // Si pudo autenticar el usuario
                    Log.d("Autenticando usuario","usuario autenticado")
                    val user = auth.currentUser // Recupero informacion del usuario creado
                    actualiza(user) // Asigno la variable a la funcion actualiza para validar que el usuario pudo auntenticarse
                }else{
                    Log.d("Autenticando usuario","Error autenticando usuario")
                    actualiza(null) // Asigno la variable a la funcion actualiza para validar que el usuario pudo auntenticarse
                }


            }


    }

    private fun actualiza(user: FirebaseUser?) {
        // Si hay un usuario definido, se redirije a pantalla principal
        if (user !=null){
            // se redirije a pantalla principal
            val intent = Intent(this,Principal::class.java)
            startActivity(intent)  // Redirije a pantalla principal
        }
    }
    // Se ejecuta cuando el app inicia y valida sesion del usuario.
    public override fun onStart() {
        super.onStart()
        val usuario = auth.currentUser
        actualiza(usuario)
    }

}