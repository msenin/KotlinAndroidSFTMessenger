package ru.sft.kotlin.messenger.client.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_new_chat.*
import ru.sft.kotlin.messenger.client.R
import ru.sft.kotlin.messenger.client.util.afterTextChanged

class NewChatActivity : AppCompatActivity() {

    private lateinit var model: NewChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        model = ViewModelProvider(this).get(NewChatViewModel::class.java)

        // Показ ошибок при вводе данных
        model.state.observe(this, Observer {
            val newChatState = it ?: return@Observer
            create_button.isEnabled = newChatState.isDataValid

            if (newChatState.titleError != null) {
                chat_title.error = getString(newChatState.titleError)
            } else {
                chat_title.error = null
            }
        })

        // Реакция на изменение состояния формы
        model.result.observe(this, Observer {
            val newChatResult = it ?: return@Observer

//            loading.visibility = View.GONE
            if (newChatResult.error != null) {
                showNewChatFailed(newChatResult.error)
            } else {
                setResult(Activity.RESULT_OK)
                finish()
            }
        })

        // Обработчики ввода имени пользователя
        chat_title.apply {
            afterTextChanged {
                model.dataChanged(
                    chat_title.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        model.newChat(
                            chat_title.text.toString()
                        )
                }
                false
            }

            // Кнопа Sign Up
            create_button.setOnClickListener {
                model.newChat(
                    chat_title.text.toString()
                )
            }
        }
    }

    private fun showNewChatFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_LONG).show()
    }
}
