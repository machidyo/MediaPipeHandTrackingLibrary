package com.example.mediapipehandtracking

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.formats.proto.LandmarkProto

class MyView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var paint: Paint = Paint()
    var multiHandLandmarks: List<LandmarkProto.NormalizedLandmarkList> = mutableListOf()

    init {
        paint.color = Color.argb(255, 255, 0, 255)
        paint.strokeWidth = 2.0f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = Color.argb(255, 255, 0, 255)
        paint.strokeWidth = 2.0f

        for ((handIndex, landmarks) in multiHandLandmarks.withIndex()) {
            var prevLandmark: LandmarkProto.NormalizedLandmark? = null
            for ((landmarkIndex, landmark) in landmarks.landmarkList.withIndex()) {
                val x = landmark.x * width
                val y = landmark.y * height
                canvas.drawRect(x - 5f, y - 5f, x + 5f, y + 5f, paint)

                // 指を描く
                if (landmarkIndex > 0) {
                    // それぞれ指の付け根なので線を引かないで手のひらで処理
                    if (landmarkIndex != 5 &&
                        landmarkIndex != 9 &&
                        landmarkIndex != 13 &&
                        landmarkIndex != 17
                    ) {
                        drawLine(canvas, prevLandmark!!, landmark)
                    }
                }
                prevLandmark = landmark
            }
            // 手のひらを描く
            drawLine(canvas, landmarks.landmarkList[1], landmarks.landmarkList[5])
            drawLine(canvas, landmarks.landmarkList[5], landmarks.landmarkList[9])
            drawLine(canvas, landmarks.landmarkList[9], landmarks.landmarkList[13])
            drawLine(canvas, landmarks.landmarkList[13], landmarks.landmarkList[17])
            drawLine(canvas, landmarks.landmarkList[17], landmarks.landmarkList[0])
        }
    }

    private fun drawLine(
        canvas: Canvas,
        start: LandmarkProto.NormalizedLandmark,
        end: LandmarkProto.NormalizedLandmark
    ) {
        val startX = start.x * width
        val startY = start.y * height
        val endX = end.x * width
        val endY = end.y * height
        canvas.drawLine(startX, startY, endX, endY, paint)
    }
}
