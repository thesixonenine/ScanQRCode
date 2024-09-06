package xyz.thesixonenine.scanqrcode

import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView


class ScanActivity : CaptureActivity() {

    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.activity_scan);
        return findViewById(R.id.zxing_barcode_scanner)
    }
}
