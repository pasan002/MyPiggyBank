package com.example.mypiggybank.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mypiggybank.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailLayout = view.findViewById(R.id.emailLayout)
        passwordLayout = view.findViewById(R.id.passwordLayout)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (validateInput(email, password)) {
                if (authenticate(email, password)) {
                    findNavController().navigate(R.id.action_login_to_dashboard)
                } else {
                    passwordLayout.error = "Incorrect email or password"
                    Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            emailLayout.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.error = "Invalid email format"
            isValid = false
        } else {
            emailLayout.error = null
        }

        if (password.isEmpty()) {
            passwordLayout.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordLayout.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordLayout.error = null
        }

        return isValid
    }

    private fun authenticate(email: String, password: String): Boolean {
        val user = VALID_CREDENTIALS.firstOrNull { it.first.equals(email, ignoreCase = true) }
        return user?.second == password
    }

    companion object {
        private val VALID_CREDENTIALS = listOf(
            "demo@piggybank.com" to "Demo@123",
            "user@piggybank.com" to "User@123"
        )
    }
} 