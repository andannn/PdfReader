package pdfreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import pdfreader.ui.pdfviewer.PdfViewerActivity
import pdfreader.ui.theme.PdfReaderTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PdfReaderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val filePicker =
                        rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.GetContent(),
                            onResult = { uri: Uri? ->
                                Log.d(TAG, "pdf picker onResult: uri=$uri")
                                // Launch PdfViewerActivity with the selected PDF file URI
                                if (uri != null) {
                                    val intent =
                                        Intent(this, PdfViewerActivity::class.java)
                                            .apply {
                                                putExtra("PDF_URI", uri.toString())
                                            }
                                    startActivity(intent)
                                }
                            },
                        )
                    TextButton(
                        onClick = { filePicker.launch("application/pdf") },
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        Text("Pick a PDF file")
                    }
                }
            }
        }
    }
}
