package com.youbook.glowpros.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivityWelComeBinding
import com.youbook.glowpros.ui.login.LoginActivity

class WelComeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityWelComeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelComeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.tvLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvLogin -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }
}