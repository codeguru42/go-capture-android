package codeguru.gocapture

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import codeguru.gocapture.databinding.FragmentImageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ImageFragment : Fragment() {
    private val args: ImageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentImageBinding.inflate(inflater, container, false)
        val repository = GoCaptureRepository(requireActivity())
        val imageUri = Uri.parse(args.imageUri)
        binding.imageView.setImageURI(imageUri)

        val processingView = binding.processing.processingView
        processingView.visibility = View.GONE
        binding.uploadButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    processingView.visibility = View.VISIBLE
                }
                repository.processImage(imageUri, processingView)
                withContext(Dispatchers.Main) {
                    processingView.visibility = View.GONE
                }
            }
        }

        return binding.root
    }
}
