package xyz.thesixonenine.scanqrcode

import android.Manifest
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator

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
        val integrator = IntentIntegrator(this)
        // 设置要扫描的条码类型, ONE_D_CODE_TYPES: 一维码, QR_CODE_TYPES: 二维码
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("扫描条形码")
        // 使用默认的相机
        integrator.setCameraId(0)
        // 扫到码后播放提示音
        integrator.setBeepEnabled(false)
        integrator.setCaptureActivity(ScanActivity::class.java)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "扫码取消！", Toast.LENGTH_LONG).show()
            } else {
                textView!!.text = result.contents
                Toast.makeText(this, "扫描成功，条码值: " + result.contents, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun onClickView(v: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun onClickCopy(v: View?) {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        // 将文本内容放到系统剪贴板里。
        cm.text = textView!!.text
        Toast.makeText(this, "复制成功，可以发给朋友们了。", Toast.LENGTH_LONG).show()
    }
}
