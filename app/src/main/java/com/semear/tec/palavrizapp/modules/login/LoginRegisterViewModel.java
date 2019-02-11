package com.semear.tec.palavrizapp.modules.login;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.Plans;
import com.semear.tec.palavrizapp.utils.repositories.SessionManager;
import com.semear.tec.palavrizapp.modules.MainActivity;
import com.semear.tec.palavrizapp.modules.register.RegisterActivity;
import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.models.UserType;
import com.semear.tec.palavrizapp.utils.repositories.UserRepository;
import com.semear.tec.palavrizapp.modules.welcome.WelcomeActivity;
import com.semear.tec.palavrizapp.utils.constants.Constants;

public class LoginRegisterViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private SessionManager sessionManager;
    private CallbackManager callbackManager;
    private String versionName;

    public LoginRegisterViewModel(@NonNull Application application) {
        super(application);
    }

    public void initViewModel(){

        //inicializacao de dados
        userRepository = new UserRepository();
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(getApplication());
        initGmailLogin();

        try {
            versionName = getApplication().getPackageManager().getPackageInfo(getApplication().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "";
        }

        // se o cara ja tem um login no cache, loga o cidadão
        if (isUserOnline()){
            getUserDataAndLogin();
        }
    }


    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    /**
     * Algumas configs para inicializar a autenticação via GMAIL
     */
    public void initGmailLogin(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getApplication().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getApplication(), gso);

    }

    public CallbackManager initFacebookLogin(Activity activity, LoginButton fbLogin){

            callbackManager = CallbackManager.Factory.create();
            fbLogin.setReadPermissions("email");

            // Callback registration
            fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("faceid",loginResult.getAccessToken().getUserId());
                    authWithFacebook(activity, loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException exception) {
                    Crashlytics.logException(exception.getCause());
                    Toast.makeText(getApplication(), getApplication().getString(R.string.facebook_fail_login), Toast.LENGTH_SHORT).show();
                }
            });

        return callbackManager;
    }

    /**
     * Método para fazer autenticação pelo EMAIL
     */
    public void authWithEmail(Activity activity, String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        getUserDataAndLogin();
                    } else {
                        Crashlytics.logException(task.getException());
                        Toast.makeText(getApplication(), getApplication().getString(R.string.email_fail_login), Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public boolean checkFields(String fullname, String email, String password){
        if ( fullname.isEmpty() || email.isEmpty() || password.isEmpty())
            return false;
        return true;
    }

    /**
     * Método para fazer registro pelo EMAIL
     */
    public void registerWithEmail(Activity activity, String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        getUserDataAndLogin();
                    } else {
                        Crashlytics.logException(task.getException());
                        Toast.makeText(getApplication(), getApplication().getString(R.string.register_fail), Toast.LENGTH_SHORT).show();
                    }

                });
    }


    /**
     * Metodo para fazer autenticação pelo GMAIL
     * @param acct
     */
    public void authWithGoogle(Activity activity, GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        getUserDataAndLogin();
                    } else {
                        Crashlytics.logException(task.getException());
                        Toast.makeText(getApplication(), getApplication().getString(R.string.google_fail_login), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Método para fazer autenticação pelo Facebook
     * @param token
     */
    public void authWithFacebook(Activity activity, AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        getUserDataAndLogin();
                    } else {
                        Crashlytics.logException(task.getException());
                        Toast.makeText(getApplication(), getApplication().getString(R.string.facebook_fail_login), Toast.LENGTH_SHORT).show();
                    }

                });
    }

    /**
     * Pega os dados do usuário de um Login com Sucesso e registra no REAL TIME DATABASE caso ainda não
     * tenha sido feito
     */
    private void getUserDataAndLogin(){

        //pega o usuario corrente, independente do tipo de login
        FirebaseUser gUser = mAuth.getCurrentUser();

        //cria o objeto
        User user = new User();
        user.setUserId(gUser.getUid());
        user.setEmail(gUser.getEmail());

        user.setFullname(gUser.getDisplayName());

        if (gUser.getPhotoUrl() != null)
            user.setPhotoUri(gUser.getPhotoUrl().toString());
        else
            user.setPhotoUri("");

        //tipo e plano padrao, depois tem que trocar isso
        user.setUserType(UserType.STUDENT);
        user.setPlan(Plans.FREE_PLAN);

        //Salva Login no cache Shared Preferences
        sessionManager.setUserOnline(user, true);

        //registra usuario pelo repositorio
        userRepository.registerUser(user);

        //Verifica se é a primeira vez dele e passa pra Welcome Screen
        if (sessionManager.isUserFirstTime()){
            startWelcomeActivity(user.getPhotoUri(), gUser.getDisplayName());
        }else{
            startMainActivity();
        }


    }

    public String getVersionName(){ return this.versionName;}

    /**
     * Chama a activity Main
     */
    private void startMainActivity(){
        Intent it = new Intent(getApplication(), MainActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplication().startActivity(it);
    }


    /**
     * Chama a activity Welcome First Time
     */
    private void startWelcomeActivity(String photoUri, String username){
        Intent it = new Intent(getApplication(), WelcomeActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        it.putExtra("photoUri", photoUri);
        it.putExtra("username", username);
        getApplication().startActivity(it);

    }

    /**
     * Se tiver digitado algum e-mail no login, passa ele pro Registro
     * @param email
     */
    public void startRegisterActivity(String email){
        Intent it = new Intent(getApplication(), RegisterActivity.class);
        it.putExtra(Constants.EXTRA_LOGIN, email);
        getApplication().startActivity(it);
    }

    /**
     * checa se o usuario ja esta online
     * @return
     */
    public boolean isUserOnline(){
        return sessionManager.isUserLoggedIn();
    }

}
