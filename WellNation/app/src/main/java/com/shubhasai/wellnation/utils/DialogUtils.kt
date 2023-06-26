package com.shubhasai.wellnation.utils

import android.animation.Animator
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shubhasai.wellnation.R

object DialogUtils {

    fun showLottieBottomSheetDialog(context: Context, animationFileName: Int,text:String) {
        val dialog = BottomSheetDialog(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_lottie, null)

        val animationView = dialogView.findViewById<LottieAnimationView>(R.id.animation_view)
        val cancelIcon = dialogView.findViewById<ImageView>(R.id.cancel_icon)

        animationView.setAnimation(animationFileName)
        animationView.playAnimation()
        animationView.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                Toast.makeText(context,text, Toast.LENGTH_SHORT).show()
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
        cancelIcon.setOnClickListener { dialog.dismiss() }
        dialog.setContentView(dialogView)
        dialog.show()

    }
}
