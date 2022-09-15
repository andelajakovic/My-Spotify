package com.example.myspotify.ui.prelogin.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myspotify.R
import com.example.myspotify.data.model.User
import com.example.myspotify.databinding.FragmentSignupBinding
import com.example.myspotify.ui.state.SignupError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private val viewModel: SignupViewModel by viewModels()

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createAccountButton.isClickable = false
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.nameEditText.addTextChangedListener(textWatcher)
        binding.emailEditText.addTextChangedListener(emailTextWatcher)
        binding.passwordEditText.addTextChangedListener(textWatcher)
        binding.createAccountButton.setOnClickListener { addNewUser() }
    }

    private fun initObservers() {
        viewModel.continueButtonState.observe(this.viewLifecycleOwner) {
            it.isEnabled.also {
                binding.createAccountButton.apply {
                    setBackgroundResource(if (it) R.drawable.white_button_selector else R.drawable.white_button_disabled)
                    isClickable = it
                }
            }
        }

        viewModel.signupState.observe(this.viewLifecycleOwner) {
            when {
                it.isSuccess -> {
                    findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToChooseArtistsFragment())
                }
                it.error == SignupError.USER_ALREADY_EXISTS -> {
                    binding.emailTextInputLayout.isErrorEnabled = true
                    binding.emailTextInputLayout.error = getString(it.error.getMessageId())
                }
                else -> {
                    Toast.makeText(context, SignupError.UNEXPECTED_ERROR.getMessageId(), Toast.LENGTH_SHORT).show()
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
            viewModel.updateButtonState(binding.nameEditText.text.toString(), binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
        }
    }

    private val emailTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.emailTextInputLayout.isErrorEnabled = false
            viewModel.updateButtonState(binding.nameEditText.text.toString(), binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
        }
    }

    private fun addNewUser() {
        viewModel.addNewUser(
            User(
                name = binding.nameEditText.text.toString(),
                email = binding.emailEditText.text.toString(),
                password = binding.passwordEditText.text.toString()
            )
        )
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}