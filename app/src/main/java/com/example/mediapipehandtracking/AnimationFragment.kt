package com.example.mediapipehandtracking

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.framework.Packet
import com.google.mediapipe.framework.PacketGetter

class AnimationFragment : Fragment() {
    private val mediaPipe: MediaPipe = MediaPipe()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("machida", "START onCreateView at AnimationFragment")
        val view = inflater.inflate(R.layout.fragment_animation, container, false)
        val layout = view.findViewById<LinearLayout>(R.id.animation_view)
        val myView = MyView(this.activity!!.baseContext)
        layout.addView(myView)

        mediaPipe.initialize(this.requireActivity())
        mediaPipe.processor!!.addPacketCallback("multi_hand_landmarks") { packet: Packet ->
            Log.d("machida", "callback succeeded at onResume at AnimationFragment")
            val multiHandLandmarks =
                PacketGetter.getProtoVector(
                    packet,
                    LandmarkProto.NormalizedLandmarkList.parser()
                )
            myView.multiHandLandmarks = multiHandLandmarks
            myView.invalidate()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        mediaPipe.setConverterOnResume()
        startCamera()
    }

    override fun onPause() {
        super.onPause()
        mediaPipe.closeConverter()
    }

    private fun startCamera() {
        mediaPipe.startCamera(this.requireActivity()) { surfaceTexture: SurfaceTexture? ->
            val dm = DisplayMetrics()
            this.activity!!.windowManager.defaultDisplay.getMetrics(dm)
            val viewSize = Size(dm.widthPixels, dm.heightPixels)
            val displaySize = mediaPipe.cameraHelper!!.computeDisplaySizeFromViewSize(viewSize)
            mediaPipe.converter!!.setSurfaceTextureAndAttachToGLContext(
                surfaceTexture, displaySize.width, displaySize.height
            )
        }
    }
}
