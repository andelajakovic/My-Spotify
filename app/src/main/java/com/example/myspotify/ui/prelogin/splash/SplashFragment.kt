package com.example.myspotify.ui.prelogin.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.myspotify.databinding.FragmentSplashBinding
import com.example.myspotify.ui.state.SessionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by viewModels()

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var action: NavDirections = SplashFragmentDirections.actionSplashFragmentToWelcomeFragment()

        viewModel.sessionState.observe(this.viewLifecycleOwner) {
            action = when (it) {
                SessionState.LOGGED_IN -> {
                    SplashFragmentDirections.actionSplashFragmentToHomeActivity()
                }
                SessionState.CHOOSE_ARTISTS -> {
                    SplashFragmentDirections.actionSplashFragmentToChooseArtistsFragment()
                }
                else -> {
                    SplashFragmentDirections.actionSplashFragmentToWelcomeFragment()
                }
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            findNavController().navigate(action)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}