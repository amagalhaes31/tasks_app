package com.example.tasks.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tasks.R
import com.example.tasks.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Inicializa eventos
        setListeners()
        observe()

        mViewModel.isAuthenticationAvailable()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.button_login) {
            handleLogin()
        } else if (v.id == R.id.text_register) {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Inicializa os eventos de click
     */
    private fun setListeners() {
        button_login.setOnClickListener(this)                                                       // Botão de ENTRAR (login)
        text_register.setOnClickListener(this)                                                      // Text View Cadastra-se
    }


    /**
     * Observa ViewModel
     */
    private fun observe() {

        // Verifica a validação do login do usuário
        mViewModel.login.observe(this, Observer {
            if (it.successMessage()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, it.failureMessage(), Toast.LENGTH_LONG).show()
            }
        })

        // Verifica se o usuário já está logado na aplicação
        mViewModel.fingerPrint.observe(this, Observer {
            if (it) {
                showFingerAuthentication()
            }
        })

    }

    /**
     * Autentica usuário
     */
    private fun handleLogin() {

        val email = edit_email.text.toString()
        val password = edit_password.text.toString()

        mViewModel.doLogin(email, password)
    }

    /**
     * Autencticação do fingerprintf (biometria)
     */
    private fun showFingerAuthentication() {

        // Executor (Uma thred -> Mostra a autenticação para o usuário, mas a resposta não é imediata)
        val executor: Executor = ContextCompat.getMainExecutor(this)

        // BiometricPrompt
        val biometricPrompt = BiometricPrompt(
            this@LoginActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationFailed() {                                             // Falha na autenticação, não reconhece a impressão digital (fingerprint)
                    super.onAuthenticationFailed()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {       // Falha no hardware
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {    // Autenticação com sucesso
                    super.onAuthenticationSucceeded(result)
                    startActivity(Intent(applicationContext,MainActivity::class.java))
                    finish()
                }
            })

        //BiometricPrompt Info
        val info : BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Titulo")
            .setSubtitle("Subtitulo")
            .setDescription("Descricao")
            .setNegativeButtonText("Cancelar")                                                      // Obrigatório ter na aplicação
            .build()

        biometricPrompt.authenticate(info)
    }

}
