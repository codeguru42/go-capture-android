package codeguru.gocapture

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import codeguru.gocapture.databinding.FragmentImageBinding


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

        val processingView = binding.root.findViewById<ConstraintLayout>(R.id.processing_view)
        processingView.visibility = View.GONE
        binding.uploadButton.setOnClickListener {
            processingView.visibility = View.VISIBLE
            repository.processImage(imageUri, processingView)
        }

        return binding.root
    }
}
