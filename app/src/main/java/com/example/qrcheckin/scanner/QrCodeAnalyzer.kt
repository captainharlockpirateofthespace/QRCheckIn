package com.example.qrcheckin.scanner

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
//import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(
    private val onQrCodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    private var isProcessing = false

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        if (isProcessing) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image

        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        isProcessing = true

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        scanner.process(image)
            .addOnSuccessListener { barcodes ->

                val qrCode = barcodes.firstOrNull {
                    it.format == Barcode.FORMAT_QR_CODE
                }

                val value = qrCode?.rawValue

                if (!value.isNullOrBlank()) {
                    onQrCodeDetected(value)
                }
            }
            .addOnFailureListener {
                // Ignore individual frame failures.
            }
            .addOnCompleteListener {
                isProcessing = false
                imageProxy.close()
            }
    }
}