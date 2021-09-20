package com.clxns.app.ui.casedetails.history

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.clxns.app.R
import com.clxns.app.databinding.ActivityHistoryDetailsBinding

class HistoryDetailsActivity : AppCompatActivity() {

    lateinit var ctx: Context
    lateinit var historyDetailsBinding: ActivityHistoryDetailsBinding
    lateinit var historyDetailsAdapter: HistoryDetailsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setInit()
        setListeners()
        setAdapter()

    }

    private fun setAdapter() {

        historyDetailsAdapter = HistoryDetailsAdapter(ctx)
        val ll = LinearLayoutManager(ctx)
        ll.isAutoMeasureEnabled = false
        historyDetailsBinding.rvDetailsTrack.apply {
            layoutManager = ll
            adapter = historyDetailsAdapter
        }

    }

    private fun setListeners() {

        historyDetailsBinding.imgBack.setOnClickListener { finish() }

    }


    private fun setInit() {
        ctx = this
        historyDetailsBinding = ActivityHistoryDetailsBinding.inflate(layoutInflater)
        setContentView(historyDetailsBinding.root)
    }
}