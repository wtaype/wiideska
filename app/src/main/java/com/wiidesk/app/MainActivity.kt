package com.wiidesk.app

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.wiidesk.app.componentes.Principal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val startupBg = Color.parseColor("#ccefff")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(createFastStartupView())

        lifecycleScope.launch {
            delay(120)
            setTheme(R.style.Theme_Wiidesk)
            setContent {
                Principal(viewModel = viewModel)
            }
        }
    }

    private fun createFastStartupView(): FrameLayout {
        return FrameLayout(this).apply {
            setBackgroundColor(startupBg)
            addView(
                ImageView(this@MainActivity).apply {
                    setImageResource(R.drawable.splash_logo)
                    adjustViewBounds = true
                    scaleType = ImageView.ScaleType.FIT_CENTER
                },
                FrameLayout.LayoutParams(220.dpToPx(), 220.dpToPx(), Gravity.CENTER),
            )
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
