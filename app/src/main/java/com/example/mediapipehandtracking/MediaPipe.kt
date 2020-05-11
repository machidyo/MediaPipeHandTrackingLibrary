package com.example.mediapipehandtracking

import android.app.Activity
import android.graphics.SurfaceTexture
import android.util.Log
import com.google.mediapipe.components.CameraHelper
import com.google.mediapipe.components.CameraXPreviewHelper
import com.google.mediapipe.components.ExternalTextureConverter
import com.google.mediapipe.components.FrameProcessor
import com.google.mediapipe.components.PermissionHelper
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.framework.AndroidAssetUtil
import com.google.mediapipe.framework.Packet
import com.google.mediapipe.framework.PacketGetter
import com.google.mediapipe.glutil.EglManager

class MediaPipe {
    companion object {
        init {
            // Load all native libraries needed by the app.
            System.loadLibrary("mediapipe_jni")
            System.loadLibrary("opencv_java3")
        }
    }

    private val TAG = "MainActivity"
    private val BINARY_GRAPH_NAME = "multihandtrackinggpu.binarypb"
    private val INPUT_VIDEO_STREAM_NAME = "input_video"
    private val OUTPUT_VIDEO_STREAM_NAME = "output_video"
    private val OUTPUT_LANDMARKS_STREAM_NAME = "multi_hand_landmarks"
    private val CAMERA_FACING = CameraHelper.CameraFacing.FRONT
    // Flips the camera-preview frames vertically before sending them into FrameProcessor to be
    // processed in a MediaPipe graph, and flips the processed frames back when they are displayed.
    // This is needed because OpenGL represents images assuming the image origin is at the
    // bottom-left corner, whereas MediaPipe in general assumes the image origin is at top-left.
    private val FLIP_FRAMES_VERTICALLY = true

    // Creates and manages an {@link EGLContext}.
    private var eglManager: EglManager? = null
    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    public var processor: FrameProcessor? = null
    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    public var converter: ExternalTextureConverter? = null
    // Handles camera access via the {@link CameraX} Jetpack support library.
    public var cameraHelper: CameraXPreviewHelper? = null

    fun initialize(activity: Activity) {
        // Initialize asset manager so that MediaPipe native libraries can access the app assets,
        // e.g., binary graphs.
        AndroidAssetUtil.initializeNativeAssetManager(activity)

        eglManager = EglManager(null)
        processor = FrameProcessor(
            activity,
            eglManager!!.nativeContext,
            BINARY_GRAPH_NAME,
            INPUT_VIDEO_STREAM_NAME,
            OUTPUT_VIDEO_STREAM_NAME
        )
        processor!!.videoSurfaceOutput.setFlipY(FLIP_FRAMES_VERTICALLY)
        processor!!.addPacketCallback(OUTPUT_LANDMARKS_STREAM_NAME) { packet: Packet ->
            Log.d(TAG, "Received multi-hand landmarks packet.")
            val multiHandLandmarks =
                PacketGetter.getProtoVector(
                    packet,
                    LandmarkProto.NormalizedLandmarkList.parser()
                )
            Log.d(
                TAG,
                "[TS:"
                        + packet.timestamp
                        + "] "
                        + getMultiHandLandmarksDebugString(multiHandLandmarks)
            )
        }

        PermissionHelper.checkAndRequestCameraPermissions(activity)
    }

    fun setConverterOnResume() {
        converter = ExternalTextureConverter(eglManager!!.context)
        converter!!.setFlipY(FLIP_FRAMES_VERTICALLY)
        converter!!.setConsumer(processor)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun startCamera(activity: Activity, callback: (SurfaceTexture?) -> Unit) {
        if (!PermissionHelper.cameraPermissionsGranted(activity)) return

        cameraHelper = CameraXPreviewHelper()
        cameraHelper!!.setOnCameraStartedListener { surfaceTexture: SurfaceTexture? ->
            callback(surfaceTexture)
        }
        cameraHelper!!.startCamera(activity, CAMERA_FACING,  /*surfaceTexture=*/null)
    }

    fun closeConverter() {
        converter!!.close()
    }

    private fun getMultiHandLandmarksDebugString(
        multiHandLandmarks: List<LandmarkProto.NormalizedLandmarkList>
    ): String {
        if (multiHandLandmarks.isEmpty()) {
            return "No hand landmarks"
        }
        var multiHandLandmarksStr =
            "Number of hands detected: " + multiHandLandmarks.size + "\n"
        for ((handIndex, landmarks) in multiHandLandmarks.withIndex()) {
            multiHandLandmarksStr +=
                "\t#Hand landmarks for hand[" + handIndex + "]: " + landmarks.landmarkCount + "\n"
            for ((landmarkIndex, landmark) in landmarks.landmarkList.withIndex()) {
                multiHandLandmarksStr += ("\t\tLandmark ["
                        + landmarkIndex
                        + "]: ("
                        + landmark.x
                        + ", "
                        + landmark.y
                        + ", "
                        + landmark.z
                        + ")\n")
            }
        }
        return multiHandLandmarksStr
    }
}