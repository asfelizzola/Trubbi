package com.example.trubbi.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import com.example.trubbi.activities.MainActivity
import com.example.trubbi.R
import com.example.trubbi.data.LoginTouristResponse
import com.example.trubbi.interfaces.APILoginService
import com.example.trubbi.services.LoginServiceBuilder
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Response
import java.lang.IllegalArgumentException


class LoginFragment : Fragment() {
    private lateinit var v: View
    private lateinit var txtRegister: TextView
    private lateinit var btnLogin: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var btnGoogle: ImageButton
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var btnForgotPass: MaterialButton
    private val key = "JWT"

    companion object {
        const val GOOGLE_SIGN_IN = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        v = inflater.inflate(R.layout.fragment_login, container, false)
        txtRegister = v.findViewById(R.id.txtBackLogin)
        btnLogin = v.findViewById(R.id.btnLogin)
        btnGoogle = v.findViewById(R.id.btnGoogle)
        firebaseAuth = Firebase.auth
        btnForgotPass = v.findViewById(R.id.btnForgotPass)

        return v
    }

    override fun onStart() {
        super.onStart()
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
        val editor = prefs.edit()
        editor.putString(key,"")
        editor.apply()
        txtRegister.setOnClickListener {

            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            v.findNavController().navigate(action)
        }

        btnLogin.setOnClickListener {
            try {
                val email: TextInputLayout = v.findViewById(R.id.emailLogin)
                val password: TextInputLayout = v.findViewById(R.id.passLogin)
                val user = LoginTouristResponse(email.editText?.text.toString(), password.editText?.text.toString(),"")
                val prefs =
                logIn(user, prefs)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(
                    context, "Debe completar todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnForgotPass.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            v.findNavController().navigate(action)
        }

    }

    private fun logIn(user: LoginTouristResponse, preferences: SharedPreferences){
        val apiService = LoginServiceBuilder.buildService(APILoginService::class.java)
        apiService.login(user).enqueue(
                object : retrofit2.Callback<LoginTouristResponse> {
                    override fun onFailure(call: Call<LoginTouristResponse>, t: Throwable) {
                        Toast.makeText(
                            context, "Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    override fun onResponse(call: Call<LoginTouristResponse>, response: Response<LoginTouristResponse>) {
                        val loggedUser = response.body()
                        if(response.errorBody() == null){
                            val editor = preferences.edit()
                            editor.putString(key,loggedUser?.accessToken)
                            editor.apply()
                            val intent = Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        } else {
                            Toast.makeText(context, "No se ha ingresado correctamente", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            )
        }

    private fun logUser(email: String, password: String) {

        activity?.let {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            updateUI(user)
                        }
                    } else {
                        Toast.makeText(
                            context, task.exception!!.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
        }
    }

    private fun updateUI(account: FirebaseUser?) {
        if (account != null) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()

        } else {
            Toast.makeText(context, "No ha ingresado", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        firebaseAuth = Firebase.auth
        googleSignInClient = GoogleSignIn.getClient(requireContext(), getGSO())
        googleSignInClient.signOut()
        btnGoogle.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.d(TAG, "signInWithEmail: error ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        updateUI(user)
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun getGSO(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.CLIENT_ID))
            .requestEmail()
            .build()
    }

 /*   private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            } else {
                val token = task.result
                println("Este es el token:$token")
            }
        })
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("evento1")
        println("Te suscribiste al topico")
    }*/

}