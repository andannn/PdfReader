package pdfreader.ui.pdfviewer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.andannn.pdfreader.R

class PdfViewerActivity : AppCompatActivity() {
    private var pdfViewerFragment: HostFragment? = null

    // Get the PDF URI from the intent, which is passed from the calling activity.
    private val pdfUri: String? by lazy {
        intent.getStringExtra(PDF_URI)
    }

    private val uri: Uri
        get() =
            pdfUri?.let { Uri.parse(pdfUri) }
                ?: throw IllegalArgumentException("PDF URI is null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pdf_viewer)

        if (SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S) >= 13) {
            // Check if a fragment is currently visible (automatically restored by FragmentManager)
            val currentFragment = supportFragmentManager.findFragmentByTag(SINGLE_PDF_FRAGMENT_TAG)
            if (currentFragment == null) {
                setChildFragment()
                pdfViewerFragment?.documentUri = uri
            }
        } else {
            /**
             * Send an intent to other apps who support opening PDFs in case PdfViewer library is
             * not supported due to SdkExtension limitations.
             */
            sendIntentToOpenPdf(uri)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
    private fun setChildFragment() {
        val fragmentManager: FragmentManager = supportFragmentManager

        // Fragment initialization
        pdfViewerFragment = HostFragment()

        // Replace an existing fragment in a container with an instance of a new fragment
        fragmentManager
            .beginTransaction()
            .replace(
                R.id.single_pdf_fragment_container_view,
                pdfViewerFragment!!,
                SINGLE_PDF_FRAGMENT_TAG,
            ).commit()

        fragmentManager.executePendingTransactions()
    }

    private fun sendIntentToOpenPdf(uri: Uri) {
        val intent =
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
            }
        val chooser = Intent.createChooser(intent, "Open PDF")
        startActivity(chooser)
    }

    companion object {
        private const val SINGLE_PDF_FRAGMENT_TAG = "single_pdf_fragment_tag"
        private const val PDF_URI = "PDF_URI"
    }
}
