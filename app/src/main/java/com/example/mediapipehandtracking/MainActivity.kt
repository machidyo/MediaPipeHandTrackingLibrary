package com.example.mediapipehandtracking

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Size
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
//    val mediaPipe: MediaPipe = MediaPipe()
//
//    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
//    private var previewFrameTexture: SurfaceTexture? = null
//    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
//    private var previewDisplayView: SurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        previewDisplayView = SurfaceView(this)
//        setupPreviewDisplayView()
//
//        mediaPipe.initialize(this)
//
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.add(
            R.id.container,
            AnimationFragment()
        )
        fragmentTransaction.commit()
    }

//    override fun onResume() {
//        super.onResume()
//        mediaPipe.setConverterOnResume()
//        startCamera()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mediaPipe.closeConverter()
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        mediaPipe.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
//
//    private fun setupPreviewDisplayView() {
//        previewDisplayView!!.visibility = View.GONE
//        val viewGroup = findViewById<ViewGroup>(R.id.container)
//        viewGroup.addView(previewDisplayView)
//        previewDisplayView!!
//            .holder
//            .addCallback(
//                object : SurfaceHolder.Callback {
//                    override fun surfaceCreated(holder: SurfaceHolder) {
//                        mediaPipe.processor!!.videoSurfaceOutput.setSurface(holder.surface)
//                    }
//
//                    override fun surfaceChanged(
//                        holder: SurfaceHolder,
//                        format: Int,
//                        width: Int,
//                        height: Int
//                    ) {
//                        // (Re-)Compute the ideal size of the camera-preview display (the area that
//                        // the camera-preview frames get rendered onto, potentially with scaling and
//                        // rotation) based on the size of the SurfaceView that contains the display.
//                        val viewSize = Size(width, height)
//                        val displaySize =
//                            mediaPipe.cameraHelper!!.computeDisplaySizeFromViewSize(viewSize)
//                        // Connect the converter to the camera-preview frames as its input (via
//                        // previewFrameTexture), and configure the output width and height as the
//                        // computed display size.
//                        mediaPipe.converter!!.setSurfaceTextureAndAttachToGLContext(
//                            previewFrameTexture, displaySize.width, displaySize.height
//                        )
//                    }
//
//                    override fun surfaceDestroyed(holder: SurfaceHolder) {
//                        mediaPipe.processor!!.videoSurfaceOutput.setSurface(null)
//                    }
//                })
//    }
//
//    private fun startCamera() {
//        mediaPipe.startCamera(this) { surfaceTexture: SurfaceTexture? ->
//            previewFrameTexture = surfaceTexture
//            // Make the display view visible to start showing the preview. This triggers the
//            // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
//            previewDisplayView!!.visibility = View.VISIBLE
//        }
//    }
}
