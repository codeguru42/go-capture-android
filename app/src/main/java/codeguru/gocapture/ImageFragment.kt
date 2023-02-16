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
        val view = binding.root
        val repository = GoCaptureRepository(requireActivity())
        val imageView = binding.imageView
        val imageUri = Uri.parse(args.imageUri)
        imageView.setImageURI(imageUri)

        val uploadButton = binding.uploadButton
        val processingView = view.findViewById<ConstraintLayout>(R.id.processing_view)
        processingView.visibility = View.GONE
        uploadButton.setOnClickListener {
            processingView.visibility = View.VISIBLE
            repository.processImage(imageUri, processingView)
        }

        return view
    }
}
