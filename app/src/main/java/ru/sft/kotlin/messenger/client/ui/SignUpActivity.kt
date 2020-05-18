package ru.sft.kotlin.messenger.client.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_sign_up.*
import ru.sft.kotlin.messenger.client.R
import ru.sft.kotlin.messenger.client.util.afterTextChanged

class SignUpActivity : AppCompatActivity() {

    private lateinit var model: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        model = ViewModelProvider(this).get(SignUpViewModel::class.java)

        // Показ ошибок при вводе данных
        model.state.observe(this, Observer {
            val signupState = it ?: return@Observer
            sign_up_button.isEnabled = signupState.isDataValid

            if (signupState.userIdError != null) {
                login.error = getString(signupState.userIdError)
            } else {
                login.error = null
            }
            if (signupState.passwordError != null) {
                password.error = getString(signupState.passwordError)
            } else {
                password.error = null
            }

            if (signupState.displayNameError != null) {
                display_name.error = getString(signupState.displayNameError)
            } else {
                display_name.error = null
            }
        })

        // Реакция на изменение состояния формы
        model.result.observe(this, Observer {
            val signupResult = it ?: return@Observer

//            loading.visibility = View.GONE
            if (signupResult.error != null) {
                showRegistrationFailed(signupResult.error)
            } else {
                setResult(Activity.RESULT_OK)
                finish()
            }
        })

        // Обработчики ввода имени пользователя
        login.afterTextChanged {
            model.dataChanged(
                login.text.toString(),
                display_name.text.toString(),
                password.text.toString(),
                confirm_password.text.toString()
            )
        }

        display_name.afterTextChanged {
            model.dataChanged(
                login.text.toString(),
                display_name.text.toString(),
                password.text.toString(),
                confirm_password.text.toString()
            )
        }

        // Обработчики ввода пароля
        password.afterTextChanged {
            model.dataChanged(
                login.text.toString(),
                display_name.text.toString(),
                password.text.toString(),
                confirm_password.text.toString()
            )
        }

        confirm_password.apply {
            afterTextChanged {
                model.dataChanged(
                    login.text.toString(),
                    display_name.text.toString(),
                    password.text.toString(),
                    confirm_password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        model.signUp(
                            login.text.toString(),
                            display_name.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            // Кнопа Sign Up
            sign_up_button.setOnClickListener {
                model.signUp(
                    login.text.toString(),
                    display_name.text.toString(),
                    password.text.toString()
                )
            }
        }

        back_button.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showRegistrationFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_LONG).show()
    }
}

