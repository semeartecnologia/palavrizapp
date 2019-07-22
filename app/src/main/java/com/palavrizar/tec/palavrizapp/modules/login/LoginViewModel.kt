package com.palavrizar.tec.palavrizapp.modules.login

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.User
import com.palavrizar.tec.palavrizapp.models.UserType
import com.palavrizar.tec.palavrizapp.modules.MainActivity
import com.palavrizar.tec.palavrizapp.modules.register.RegisterActivity
import com.palavrizar.tec.palavrizapp.modules.welcome.WelcomeActivity
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.utils.repositories.UserRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private var userRepository: UserRepository? = null
    private var mAuth: FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
        private set
    private var sessionManager: SessionManager? = null
    private var callbackManager: CallbackManager? = null
    var versionName: String? = null
        private set

    val showEmailPasswordIncorrectDialog = MutableLiveData<Boolean>()
    val emailFailedDIalog = MutableLiveData<Boolean>()
    val showCompleteFields = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()

    /**
     * checa se o usuario ja esta online
     * @return
     */
    val isUserOnline: Boolean
        get() = sessionManager!!.isUserLoggedIn

    fun initViewModel() {

        //inicializacao de dados
        userRepository = UserRepository(getApplication())
        mAuth = FirebaseAuth.getInstance()
        sessionManager = SessionManager(getApplication())
        initGmailLogin()

        try {
            versionName = getApplication<Application>().packageManager.getPackageInfo(getApplication<Application>().packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            versionName = ""
        }

        val firebaseUser = mAuth!!.currentUser
        // se o cara ja tem um login no cache, loga o cidadão
        if (isUserOnline || firebaseUser != null) {
            getUserDataAndLogin()
        }
    }

    private fun checkFields(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            showCompleteFields.postValue(true)
            return false
        }
        return true
    }

    /**
     * Algumas configs para inicializar a autenticação via GMAIL
     */
    private fun initGmailLogin() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getApplication<Application>().getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(getApplication<Application>(), gso)

    }

    fun initFacebookLogin(activity: Activity, fbLogin: LoginButton): CallbackManager? {

        callbackManager = CallbackManager.Factory.create()
        fbLogin.setReadPermissions("email")

        // Callback registration
        fbLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("faceid", loginResult.accessToken.userId)
                authWithFacebook(activity, loginResult.accessToken)
            }

            override fun onCancel() {}

            override fun onError(exception: FacebookException) {
                Crashlytics.logException(exception.cause)
                Toast.makeText(activity.applicationContext, activity.applicationContext.getString(R.string.facebook_fail_login), Toast.LENGTH_SHORT).show()
            }
        })

        return callbackManager
    }

    /**
     * Método para fazer autenticação pelo EMAIL
     */
    fun authWithEmail(activity: Activity, email: String, password: String) {
        if (checkFields(email, password)) {
            isLoading.postValue(true)
            mAuth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            isLoading.postValue(false)
                            showEmailPasswordIncorrectDialog.postValue(false)
                            getUserDataAndLogin()

                        } else {
                            isLoading.postValue(false)
                            showEmailPasswordIncorrectDialog.postValue(true)
                        }

                    }
        }
    }


    /**
     * Metodo para fazer autenticação pelo GMAIL
     * @param acct
     */
    fun authWithGoogle(activity: Activity, acct: GoogleSignInAccount) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        isLoading.postValue(true)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        isLoading.postValue(false)
                        getUserDataAndLogin()
                    } else {
                        isLoading.postValue(false)
                        Crashlytics.logException(task.exception)
                        Toast.makeText(activity.applicationContext, activity.applicationContext.getString(R.string.google_fail_login), Toast.LENGTH_SHORT).show()
                    }
                }
    }

    /**
     * Método para fazer autenticação pelo Facebook
     * @param token
     */
    fun authWithFacebook(activity: Activity, token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        isLoading.postValue(true)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        isLoading.postValue(false)
                        getUserDataAndLogin()
                    } else {
                        isLoading.postValue(false)
                        Crashlytics.logException(task.exception)
                        Toast.makeText(activity.applicationContext, activity.applicationContext.getString(R.string.facebook_fail_login), Toast.LENGTH_SHORT).show()
                    }

                }
    }

    /**
     * Pega os dados do usuário de um Login com Sucesso e registra no REAL TIME DATABASE caso ainda não
     * tenha sido feito
     */
    private fun getUserDataAndLogin() {

        //pega o usuario corrente, independente do tipo de login
        val gUser = mAuth!!.currentUser

        //cria o objeto
        val user = User()
        user.userId = gUser!!.uid
        user.email = gUser.email
        user.fullname = gUser.displayName

        if (gUser.photoUrl != null)
            user.photoUri = gUser.photoUrl!!.toString()
        else
            user.photoUri = ""

        loadFirebaseInfo(user)
    }

    fun loadFirebaseInfo(user: User){
        userRepository?.getUser(user.userId,
                {
                    //NEW USER, REGISTER HIM
                    if (it == null){
                        //poe a padrãozada
                        user.userType = UserType.ESTUDANTE
                        user.plan = "free_plan"
                        user.registerDate = System.currentTimeMillis()

                        //registra usuario pelo repositorio
                        userRepository!!.registerUser(user)
                        sessionManager!!.setUserOnline(user, true)
                    }else{
                        //Salva Login no cache Shared Preferences
                        sessionManager!!.setUserOnline(it, true)
                    }

                    //Verifica se é a primeira vez dele e passa pra Welcome Screen
                    if (sessionManager!!.isUserFirstTime) {
                        startWelcomeActivity(user.photoUri, user.fullname)
                    } else {
                        startMainActivity()
                    }
                },
                {
                    emailFailedDIalog.postValue(true)
                })
    }


    /**
     * Chama a activity Main
     */
    private fun startMainActivity() {
        val it = Intent(getApplication(), MainActivity::class.java)
        it.flags = FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        getApplication<Application>().startActivity(it)
    }


    /**
     * Chama a activity Welcome First Time
     */
    private fun startWelcomeActivity(photoUri: String, username: String?) {
        val it = Intent(getApplication(), WelcomeActivity::class.java)
        it.flags = FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        it.putExtra("photoUri", photoUri)
        it.putExtra("username", username)
        getApplication<Application>().startActivity(it)

    }


    fun startRegisterActivity() {
        val it = Intent(getApplication(), RegisterActivity::class.java)
        it.addFlags(FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(it)
    }

}
