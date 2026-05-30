package com.rizero.feature_take_photo.ui.screen

import android.widget.ImageButton
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.viewfinder.core.ImplementationMode
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rizero.feature_take_photo.R
import com.rizero.shared_ui.AppColors

@Composable
fun TakePhotoScreen(){

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }

    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }

    LaunchedEffect(lifecycleOwner) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            try {
                cameraProvider = providerFuture.get()
                val preview = androidx.camera.core.Preview.Builder().build()
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider?.unbindAll()

                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                preview.setSurfaceProvider { request ->
                    surfaceRequest = request
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, mainExecutor)
    }

    Column(
        modifier = Modifier
            .background(AppColors.defaultBackgroundColor)
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
                .height(60.dp)
        ) {
            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.arrow_back),
                    contentDescription = "Кнопка назад",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            Text (
                text = "Фото до забора мусора",
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 20.dp)
            )
        }
        HorizontalDivider()
        if (surfaceRequest == null){
            Box(
                modifier = Modifier.fillMaxSize()
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = "Нет доступа к камере",
                        fontSize = 20.sp,
                        color = Color.White,
                    )
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.buttonBackgroundColor
                        ),
                        onClick = {},
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(
                            text = "Предоставить доступ"
                        )
                    }
                }

            }
        }else{
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CameraXViewfinder(
                    surfaceRequest = surfaceRequest!!,
                    implementationMode = ImplementationMode.EXTERNAL,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = {},
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.photo_camera),
                        contentDescription = "Сделать фото",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(48.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun TakePhotoScreenPreview(){
    TakePhotoScreen()
}