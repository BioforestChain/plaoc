package org.bfchain.plaoc.plugin.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import org.bfchain.plaoc.MyScaffold
import java.lang.Exception

private const val TAG = "QrCodeScanner"

@Composable
fun QrCodeScanner(navController: NavController) {
    var code by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }


    MyScaffold(navController, "QrCodeScanner") { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            if (hasCamPermission) {
                Box(modifier = Modifier.weight(1f)) {
                    Text(text = "正在使用您都相机权限", modifier = Modifier.fillMaxWidth())
                    AndroidView(
                        factory = { context ->

                            val previewView = PreviewView(context).also { it ->
                                it.scaleType = PreviewView.ScaleType.FIT_CENTER
                                it.previewStreamState.observe(lifecycleOwner) { state ->
                                    Log.i(
                                        TAG,
                                        "previewView size(${state.name}): ${it.width}x${it.height}"
                                    )
                                    if (state.name == "STREAMING") {

                                    }
                                };
                            }

                            val preview = Preview.Builder().build()
                            val selector = CameraSelector.Builder()
                                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                .build()
                            preview.setSurfaceProvider(previewView.surfaceProvider)

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                            Log.i(
                                TAG,
                                "previewView size: ${previewView.width}x${previewView.height}=${imageAnalysis.resolutionInfo}"
                            )


                            imageAnalysis.setAnalyzer(
                                ContextCompat.getMainExecutor(context),
                                QrCodeAnalyzer { result ->
                                    code = result
                                }
                            )

                            try {
                                cameraProviderFuture.get().bindToLifecycle(
                                    lifecycleOwner,
                                    selector,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            cameraProviderFuture.get().bindToLifecycle(
                                lifecycleOwner,
                                selector,
                                preview,
                                imageAnalysis
                            )

                            previewView
                        },
                    )
                    Text(
                        text = code,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                }

            } else {
                Text(text = "未取得相机权限")
                Button(onClick = {
                    launcher.launch(Manifest.permission.CAMERA)
                }) {
                    Text(text = "授权相机权限")
                }
            }
        }
    }
}