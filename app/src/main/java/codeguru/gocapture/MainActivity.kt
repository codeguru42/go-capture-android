package codeguru.gocapture

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun captureImage(view: View) {
        val intent = Intent().apply {
            action = MediaStore.ACTION_IMAGE_CAPTURE
        }
        startActivity(intent)
    }
}
