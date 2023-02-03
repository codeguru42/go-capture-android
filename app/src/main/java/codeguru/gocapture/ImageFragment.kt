package codeguru.gocapture

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ImageFragment : Fragment() {
    private val args: ImageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_image, container, false)
        val repository = GoCaptureRepository(requireActivity(), view)
        val imageView = view.findViewById<ImageView>(R.id.image_view)
        val imageUri = Uri.parse(args.imageUri)
        imageView.setImageURI(imageUri)

        val uploadButton = view.findViewById<FloatingActionButton>(R.id.upload_button)
        uploadButton.setOnClickListener {
            repository.processImage(imageUri)
        }

        return view
    }
}
