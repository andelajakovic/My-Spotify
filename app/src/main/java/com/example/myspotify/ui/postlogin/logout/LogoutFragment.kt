package com.example.myspotify.ui.postlogin.logout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myspotify.databinding.FragmentLogoutBinding
import com.example.myspotify.ui.state.SessionState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogoutFragment : Fragment() {

    private val viewModel: LogoutViewModel by viewModels()

    private var _binding: FragmentLogoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLogoutBinding.inflate(inflater, container, false)

        viewModel.sessionState.observe(this.viewLifecycleOwner) {
            if (it == SessionState.LOGGED_OUT) {
                findNavController().navigate(LogoutFragmentDirections.actionLogoutFragmentToMainActivity())
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}