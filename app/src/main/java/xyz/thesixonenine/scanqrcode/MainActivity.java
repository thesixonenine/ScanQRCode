//package xyz.thesixonenine.scanqrcode;
//
//import android.Manifest;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.content.Intent;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.zxing.integration.android.IntentIntegrator;
//import com.google.zxing.integration.android.IntentResult;
//
//public class MainActivity extends AppCompatActivity {
//
//    private TextView textView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        String[] permissions = new String[]{
//                Manifest.permission.CAMERA,
//                Manifest.permission.RECORD_AUDIO,
//                Manifest.permission.VIBRATE,
//        };
//        ActivityCompat.requestPermissions(this, permissions, 100);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        textView = (TextView) findViewById(R.id.textView);
//        IntentIntegrator integrator = new IntentIntegrator(this);
//        // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
//        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
//        integrator.setPrompt("扫描条形码");
//        integrator.setCameraId(0);  // 使用默认的相机
//        integrator.setBeepEnabled(false); // 扫到码后播放提示音
//        integrator.setCaptureActivity(ScanActivity.class);
//        integrator.initiateScan();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//            if (result.getContents() == null) {
//                Toast.makeText(this, "扫码取消！", Toast.LENGTH_LONG).show();
//            } else {
//                textView.setText(result.getContents());
//                Toast.makeText(this, "扫描成功，条码值: " + result.getContents(), Toast.LENGTH_LONG).show();
//            }
//        } else {
//            // This is important, otherwise the result will not be passed to the fragment
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//
//    public void onClickView(View v) {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//    }
//
//    public void onClickCopy(View v) {
//        // 从API11开始android推荐使用android.content.ClipboardManager
//        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
//        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        // 将文本内容放到系统剪贴板里。
//        cm.setText(textView.getText());
//        Toast.makeText(this, "复制成功，可以发给朋友们了。", Toast.LENGTH_LONG).show();
//    }
//}
