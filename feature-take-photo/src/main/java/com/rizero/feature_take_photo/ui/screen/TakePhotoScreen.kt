package com.rizero.feature_take_photo.ui.screen

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
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
import com.rizero.feature_take_photo.component.MockTakePhotoComponent
import com.rizero.feature_take_photo.component.TakePhotoComponent
import com.rizero.shared_ui.AppColors
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

@Composable
fun TakePhotoScreen(takePhotoComponent: TakePhotoComponent){

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
                    takePhotoComponent.navigateBack()
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
                    onClick = {
                        takePhoto(
                            address = takePhotoComponent.address,
                            imageCapture = imageCapture,
                            executor = mainExecutor,
                            context = context,
                        ){ uri->
                            takePhotoComponent.onPhotoTaken(uri)
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.photo_camera),
                        contentDescription = "Сделать фото",
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .size(60.dp)
                    )
                }
            }
        }
    }
}

private fun takePhoto(
    address: String,
    imageCapture: ImageCapture?,
    executor: Executor,
    context: Context,
    callback: (Uri) -> Unit
) {
    if (imageCapture == null) return

    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val originalBitmap = image.toRotatedBitmap()
                image.close()

                // Добавляем водяной знак
                val watermarkedBitmap = addWatermark(originalBitmap,address)

                // Сохраняем с водяным знаком
                saveBitmapWithWatermark(context, watermarkedBitmap, callback)
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        }
    )
}
private fun addWatermark(original: Bitmap, address: String): Bitmap {
    val result = original.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(result)

    val paint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 48f
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        setShadowLayer(8f, 3f, 3f, android.graphics.Color.BLACK)
    }

    val dateTime = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        .format(System.currentTimeMillis())

    val padding = 50f
    val lineHeight = paint.textSize * 1.1f  // небольшой отступ между строками

    // Первая строка — дата и время
    val yDate = result.height - padding
    canvas.drawText(dateTime, padding, yDate, paint)

    // Вторая строка — адрес (чуть меньшим шрифтом, если текст длинный)
    val addressPaint = Paint(paint).apply {
        textSize = 42f  // чуть меньше, чтобы адрес лучше влезал
    }

    // Перенос длинного адреса на несколько строк (опционально)
    val addressLines = address.splitByLength(50) // можно улучшить

    addressLines.forEachIndexed { index, line ->
        val yAddress = yDate - lineHeight * (index + 1)
        canvas.drawText(line, padding, yAddress, addressPaint)
    }

    return result
}

private fun String.splitByLength(maxLength: Int): List<String> {
    val words = this.split(" ")
    val lines = mutableListOf<String>()
    var currentLine = StringBuilder()

    for (word in words) {
        if (currentLine.length + word.length + 1 > maxLength) {
            lines.add(currentLine.toString().trim())
            currentLine = StringBuilder(word)
        } else {
            if (currentLine.isNotEmpty()) currentLine.append(" ")
            currentLine.append(word)
        }
    }
    if (currentLine.isNotEmpty()) lines.add(currentLine.toString().trim())

    return lines
}

private fun saveBitmapWithWatermark(
    context: Context,
    bitmap: Bitmap,
    callback: (Uri) -> Unit
) {
    val name = "CameraX_${System.currentTimeMillis()}.jpg"

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Images")
        }
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, outputStream)
        }
        callback(it)
    }
}

fun ImageProxy.toRotatedBitmap(): Bitmap {
    val bitmap = this.toBitmap() // базовое преобразование

    val rotation = imageInfo.rotationDegrees
    if (rotation == 0) return bitmap

    val matrix = Matrix().apply {
        postRotate(rotation.toFloat())
    }

    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
}

fun ImageProxy.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}


@Composable
@Preview
fun TakePhotoScreenPreview(){
    TakePhotoScreen(MockTakePhotoComponent())
}