package xyz.thesixonenine.scanqrcode

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class MainActivity : AppCompatActivity() {
    private var textView: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.VIBRATE,
        )
        ActivityCompat.requestPermissions(this, permissions, 100)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById<View>(R.id.textView) as TextView
        val barcodeLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            val contents = result.contents
            if (contents == null) {
                Toast.makeText(this, "扫码取消", Toast.LENGTH_LONG).show()
            } else {
                textView!!.text = contents
                Toast.makeText(this, "扫描内容: $contents", Toast.LENGTH_LONG).show()
            }
        }
        val options = ScanOptions()
        // 设置要扫描的条码类型, ONE_D_CODE_TYPES: 一维码, QR_CODE_TYPES: 二维码
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
        options.setPrompt("请扫描二维码/条形码\n\n\n")
        // 使用默认的相机
        options.setCameraId(0)
        // 扫到码后播放提示音
        options.setBeepEnabled(false)
        options.setCaptureActivity(ScanActivity::class.java)
        barcodeLauncher.launch(options)
    }

    fun onClickView(v: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun onClickCopy(v: View?) {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager, 虽然提示deprecated, 但不影响使用
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("text", textView!!.text))
        Toast.makeText(this, "已复制", Toast.LENGTH_LONG).show()
    }
}
