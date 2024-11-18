package com.critt.trandroidlator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.critt.trandroidlator.data.ApiResult
import com.critt.trandroidlator.databinding.FragmentFirstBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class FirstFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSupportedLanguages().observe(viewLifecycleOwner) { result ->
            //binding.textviewFirst.text = it
            //binding.progressBar.isVisible = result is ApiResult.Loading
            when(result) {
                is ApiResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    Timber.d(result.message)
                }
                is ApiResult.Success -> {
                    val languages = result.data
                    languages?.let {
                        val languageNames = languages.map { it.language }
                        Timber.d(languageNames.joinToString(", "))
                    }
                }
                else -> Unit
            }
        }

        binding.buttonFirst.setOnClickListener {
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            Timber.d("Button clicked")
            viewModel.connect("en", "de")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}