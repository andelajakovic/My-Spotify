package com.example.myspotify.ui.prelogin.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myspotify.R
import com.example.myspotify.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.emailEditText.addTextChangedListener(textWatcher)
        binding.passwordEditText.addTextChangedListener(textWatcher)
        binding.loginButton.setOnClickListener { verifyUser() }
    }

    private fun verifyUser() {
        viewModel.verifyUser(
            binding.emailEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )
    }

    private fun initObservers() {
        viewModel.continueButtonState.observe(this.viewLifecycleOwner) {
            it.isEnabled.also {
                binding.loginButton.apply {
                    setBackgroundResource(if (it) R.drawable.white_button_selector else R.drawable.white_button_disabled)
                    isClickable = it
                }
            }
        }

        viewModel.loginState.observe(this.viewLifecycleOwner) {
            when {
                it.isUserVerified -> {
//                    val action = TODO
//                    findNavController().navigate(action)
//                    activity?.finish()
                }
                else -> {
                    binding.emailTextInputLayout.error = getString(R.string.fragment_login_error_message)
                    binding.passwordTextInputLayout.error = getString(R.string.fragment_login_error_message)
                }
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.updateButtonState(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}