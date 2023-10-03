package com.google.mediapipe.examples.handlandmarker.presentation.handtherapy

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.components.CameraHelper
import com.google.mediapipe.components.CameraXPreviewHelper
import com.google.mediapipe.components.ExternalTextureConverter
import com.google.mediapipe.components.FrameProcessor
import com.google.mediapipe.components.PermissionHelper
import com.google.mediapipe.examples.handlandmarker.R
import com.google.mediapipe.framework.AndroidAssetUtil
import com.google.mediapipe.glutil.EglManager


class HandTherapyActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "HandTherapyActivity"

        private const val OUTPUT_LANDMARK_STREAM_NAME = "hand_landmarks"
        private const val OUTPUT_HANDEDNESS_STREAM_NAME = "handedness"
        private const val INPUT_NUM_HANDS_SIDE_PACKET_NAME = "num_hands"
        private const val FLIP_FRAMES_VERTICALLY = true
        const val NUM_HANDS = 2
//        private val CAMERA_FACING_BACK: CameraHelper.CameraFacing = CameraHelper.CameraFacing.BACK
//        private val CAMERA_FACING_FRONT: CameraHelper.CameraFacing = CameraHelper.CameraFacing.FRONT
        private val BINARY_GRAPH_NAME: String? = "hand_tracking_mobile_gpu.binarypb"
        private const val INPUT_VIDEO_STREAM_NAME = "input_video"
        private const val OUTPUT_VIDEO_STREAM_NAME = "output_video"

        init {
            System.loadLibrary("mediapipe_jni")
            System.loadLibrary("opencv_java3")
        }
    }

    private var previewFrameTexture: SurfaceTexture? = null
    private var previewDisplayView: SurfaceView? = null
    private var cameraHelper: CameraXPreviewHelper? = null
    private var applicationInfo: ApplicationInfo? = null
    private var eglManager: EglManager? = null
    private var converter: ExternalTextureConverter? = null
    private var processor: FrameProcessor? = null
//    private var cameraFacing: CameraHelper.CameraFacing = CAMERA_FACING_FRONT


//------------------------------------- LifeCycle Functions ----------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate run~~")
        setContentView(R.layout.activity_hand_therapy)

        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        } catch (e: NameNotFoundException) {
            Log.e(TAG, "Cannot find application info: $e")
        }

        initMediapipeModules()
        // Camera Permission Request
        PermissionHelper.checkAndRequestCameraPermissions(this)
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume run~~")
        initConverter()
        checkPermissionAndStartCamera()
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause run~~")
        closeConverter()
    }


//------------------------------------- Initialization ---------------------------------------------
    private fun initMediapipeModules() {
        previewDisplayView = SurfaceView(this)
        setupPreviewDisplayView()
        AndroidAssetUtil.initializeNativeAssetManager(this)
        eglManager = EglManager(null)
        initializeProcessor()
    }


//------------------------------------- Processor Functions ----------------------------------------
    private fun initializeProcessor() {
        Log.i(TAG, "initializeProcessor: run~~")

        processor = FrameProcessor(
            this,
            eglManager!!.nativeContext,
            BINARY_GRAPH_NAME,
            INPUT_VIDEO_STREAM_NAME,
            OUTPUT_VIDEO_STREAM_NAME
        )
    }


//------------------------------------- Display Functions ------------------------------------------
    private fun setupPreviewDisplayView() {
        previewDisplayView?.visibility = View.GONE
        val viewGroup: ViewGroup = findViewById(R.id.preview_display_layout)
        viewGroup.addView(previewDisplayView)

        previewDisplayView?.holder?.addCallback(object: SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.i(TAG, "surfaceCreated: run~~")
                processor?.videoSurfaceOutput?.setSurface(holder.surface)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                Log.i(TAG, "surfaceChanged: run~~")

                // (Re-)Compute the ideal size of the camera-preview display (the area that the
                // camera-preview frames get rendered onto, potentially with scaling and rotation)
                // based on the size of the SurfaceView that contains the display.
                val viewSize = Size(width, height)
                val displaySize: Size = cameraHelper!!.computeDisplaySizeFromViewSize(viewSize)

                // Connect the converter to the camera-preview frames as its input (via
                // previewFrameTexture), and configure the output width and height as the computed
                // display size.
                converter!!.setSurfaceTextureAndAttachToGLContext(
                    previewFrameTexture, displaySize.width, displaySize.height
                )
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.i(TAG, "surfaceDestroyed: run~~")
                processor?.videoSurfaceOutput?.setSurface(null)
            }

        })
    }


//------------------------------------- Converter Functions ---------------------------------------
    private fun initConverter() {
        eglManager?.let {
            converter = ExternalTextureConverter(it.context, 2)
            converter!!.setConsumer(processor)
        } ?: let {
            Log.e(TAG, "EglManager has not be initialized.")
        }
    }

    private fun closeConverter() {
        converter?.close()
    }


//--------------------------------------- Camera Permission ----------------------------------------
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


//---------------------------------------- Camera Function -----------------------------------------
    private fun checkPermissionAndStartCamera() {
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera()
        } else {
            Log.e(TAG, "Application doesn't have the permission to open camera")
        }
    }

    private val USE_FRONT_CAMERA = false
    private fun startCamera() {
        cameraHelper = CameraXPreviewHelper()
        cameraHelper!!.setOnCameraStartedListener { surfaceTexture: SurfaceTexture? ->
            previewFrameTexture = surfaceTexture
            previewDisplayView!!.visibility = View.VISIBLE
        }

        val cameraFacing =
            if (USE_FRONT_CAMERA) CameraHelper.CameraFacing.FRONT else CameraHelper.CameraFacing.BACK
        cameraHelper!!.startCamera(this, cameraFacing, null, null)
    }

}




