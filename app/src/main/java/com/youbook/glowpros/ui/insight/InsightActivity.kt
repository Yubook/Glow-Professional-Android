package com.youbook.glowpros.ui.insight

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivityInsightBinding
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.youbook.glowpros.extension.visible
import com.youbook.glowpros.utils.Utils.hide
import com.youbook.glowpros.utils.Utils.snackbar
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.floor

class InsightActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityInsightBinding
    private lateinit var viewModel: InsightViewModel
    private lateinit var adapter: MostBookedServiceAdapter
    private var driverId: String = ""
    private var currentChartType: String = ""
    private var totalRevenue: Float? = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this,
            InsightViewModelFactory(
                InsightRepository(
                    MyApi.getInstanceToken(Prefrences.getPreferences(this, Constants.API_TOKEN)!!)
                )
            )
        ).get(InsightViewModel::class.java)


        driverId = Prefrences.getPreferences(this, Constants.USER_ID)!!

        adapter = MostBookedServiceAdapter(this)
        binding.bookedServiceRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookedServiceRecyclerView.adapter = adapter

        setOnClickListener()

        getYearlyGraphData()

        getMostBookedServices()

        viewModel.mostBookedServicesResponse.observe(this) {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    binding.progressBar.hide()
                    if (it.value.success!!) {
                        if (it.value.result != null)
                            adapter.updateList(it.value.result.data as ArrayList<DataItem>)
                        else
                            binding.root.snackbar(it.value.message!!)
                    } else {
                        binding.root.snackbar(it.value.message!!)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                    binding.progressBar.hide()
                }
            }

        }


        viewModel.graphResponse.observe(this) {
            binding.chartProgressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    binding.chartProgressBar.hide()
                    if (it.value.success!!) {
                        if (currentChartType == Constants.WEEKLY_CHART_DATA) {
                            setBarChartData(it.value.result!!.weekly, currentChartType)
                        } else if (currentChartType == Constants.MONTHLY_CHART_DATA) {
                            setBarChartData(it.value.result!!.monthly, currentChartType)
                        } else if (currentChartType == Constants.THREE_MONTH_CHART_DATA || currentChartType == Constants.SIX_MONTH_CHART_DATA) {
                            setBarChartData(it.value.result!!.somemonths, currentChartType)
                        } else {
                            setBarChartData(it.value.result!!.yearly, currentChartType)
                        }

                    } else {
                        binding.root.snackbar(it.value.message!!)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                    binding.chartProgressBar.hide()
                }
            }

        }
    }

    private fun getMostBookedServices() {
        viewModel.viewModelScope.launch {
            viewModel.getMostBookedServices()
        }
    }

    private fun getChartData(searchString: String) {
        viewModel.viewModelScope.launch {
            viewModel.getRevenueMapData(driverId, searchString)
        }
    }

    private fun getYearlyGraphData() {
        currentChartType = ""
        viewModel.viewModelScope.launch {
            viewModel.getRevenueDataWithoutSearch(driverId)
        }
    }

    private fun setOnClickListener() {
        binding.ivBackButton.setOnClickListener(this)
        binding.rb1Week.setOnClickListener(this)
        binding.rb3Month.setOnClickListener(this)
        binding.rb6Month.setOnClickListener(this)
        binding.rb1Month.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackButton -> finish()
            R.id.rb1Week -> oneWeekGraphData()
            R.id.rb3Month -> one3MonthGraphData()
            R.id.rb6Month -> one6MonthGraphData()
            R.id.rb1Month -> one1MonthGraphData()
        }
    }

    private fun one1MonthGraphData() {
        binding.rb6Month.isChecked = false
        binding.rb1Week.isChecked = false
        binding.rb3Month.isChecked = false
        currentChartType = Constants.MONTHLY_CHART_DATA
        totalRevenue = 0f
        getChartData(Constants.MONTHLY_CHART_DATA)
    }

    private fun one6MonthGraphData() {
        binding.rb1Month.isChecked = false
        binding.rb1Week.isChecked = false
        binding.rb3Month.isChecked = false
        currentChartType = Constants.SIX_MONTH_CHART_DATA
        totalRevenue = 0f
        getChartData(Constants.SIX_MONTH_CHART_DATA)
    }

    private fun one3MonthGraphData() {
        binding.rb1Month.isChecked = false
        binding.rb1Week.isChecked = false
        binding.rb6Month.isChecked = false
        currentChartType = Constants.THREE_MONTH_CHART_DATA
        totalRevenue = 0f
        getChartData(Constants.THREE_MONTH_CHART_DATA)
    }

    private fun oneWeekGraphData() {
        binding.rb1Month.isChecked = false
        binding.rb3Month.isChecked = false
        binding.rb6Month.isChecked = false
        currentChartType = Constants.WEEKLY_CHART_DATA
        totalRevenue = 0f
        getChartData(Constants.WEEKLY_CHART_DATA)
    }

    private fun setBarChartData(yearly: List<YearlyItem?>?, graphType: String) {
        val xAxisLabel = ArrayList<String>()
        val barChartValues: MutableList<BarEntry> = ArrayList()
        if (graphType.equals("") || graphType.equals(Constants.THREE_MONTH_CHART_DATA) || graphType.equals(
                Constants.SIX_MONTH_CHART_DATA
            )
        ) {
            for (i in yearly!!.indices) {
                totalRevenue = totalRevenue!!.toFloat() + yearly[i]!!.revenue!!.toFloat()
                barChartValues.add(BarEntry(i.toFloat(), yearly[i]!!.revenue!!.toFloat()))

                if (yearly[i]!!.month != null) {
                    xAxisLabel.add(yearly[i]!!.month!!)
                } else {
                    yearly[i]!!.month?.let { xAxisLabel.add(it) }
                }
            }

            binding.tvTotalRevenue.text = "Apnt. booked\n".plus(totalRevenue!!.toInt().toString())

            val barDataSet = BarDataSet(barChartValues, "")
            barDataSet.barBorderColor = R.color.black
            barDataSet.setColors(ContextCompat.getColor(binding.barChart.context, R.color.black))
            val xAxis: XAxis = binding.barChart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
            //        xAxis.setCenterAxisLabels(true);
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.axisLineColor = R.color.chart_line_color
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true

            // Create pie data
            val barData = BarData(barDataSet)
            // Remove values from chart
            barData.barWidth = 0.8f
            //barData.groupBars(0f, 0.5f, 0.1f);
            barData.setDrawValues(false)

            // Set Pie data to pieChart.
            binding.barChart.data = barData
            binding.barChart.setTouchEnabled(false)
            binding.barChart.isDragEnabled = true
            binding.barChart.setExtraOffsets(10f, 10f, 10f, 10f)
        } else if (graphType.equals(Constants.WEEKLY_CHART_DATA)) {
            for (i in yearly!!.indices) {
                totalRevenue = totalRevenue!!.toFloat() + yearly[i]!!.revenue!!.toFloat()
                barChartValues.add(BarEntry(i.toFloat(), yearly[i]!!.revenue!!.toFloat()))

                if (yearly[i]!!.date != null) {
                    xAxisLabel.add(yearly[i]!!.date!!.toString().substring(0, 5))
                } else {
                    yearly[i]!!.date?.let { xAxisLabel.add(it) }
                }
            }

            binding.tvTotalRevenue.text =
                "Apnt. booked\n".plus(totalRevenue!!.toInt().toString())
            val barDataSet = BarDataSet(barChartValues, "")
            barDataSet.barBorderColor = R.color.black
            barDataSet.setColors(ContextCompat.getColor(binding.barChart.context, R.color.black))
            val xAxis: XAxis = binding.barChart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
            //        xAxis.setCenterAxisLabels(true);
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.axisLineColor = R.color.black
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true

            // Create pie data
            val barData = BarData(barDataSet)
            // Remove values from chart
            barData.barWidth = 0.8f
            //barData.groupBars(0f, 0.5f, 0.1f);
            barData.setDrawValues(false)

            // Set Pie data to pieChart.
            binding.barChart.data = barData
            binding.barChart.setTouchEnabled(false)
            binding.barChart.isDragEnabled = true
            binding.barChart.setExtraOffsets(10f, 10f, 10f, 10f)
        } else if (graphType.equals(Constants.MONTHLY_CHART_DATA)) {
            for (i in yearly!!.indices) {
                totalRevenue = totalRevenue!!.toFloat() + yearly[i]!!.revenue!!.toFloat()
                barChartValues.add(BarEntry(i.toFloat(), yearly[i]!!.revenue!!.toFloat()))

                if (yearly[i]!!.start_week != null) {
                    xAxisLabel.add(
                        yearly[i]!!.start_week!!.toString().substring(0, 5).plus(" | ")
                            .plus(yearly[i]!!.end_week!!.toString().substring(0, 5))
                    )
                } else {
                    yearly[i]!!.date?.let { xAxisLabel.add(it) }
                }
            }

            binding.tvTotalRevenue.text =
                "Apnt. booked\n".plus(totalRevenue!!.toInt().toString())

            val barDataSet = BarDataSet(barChartValues, "")
            barDataSet.barBorderColor = R.color.black
            barDataSet.setColors(ContextCompat.getColor(binding.barChart.context, R.color.black))
            val xAxis: XAxis = binding.barChart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
            //        xAxis.setCenterAxisLabels(true);
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.axisLineColor = R.color.black
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true

            // Create pie data
            val barData = BarData(barDataSet)
            // Remove values from chart
            barData.barWidth = 0.8f
            //barData.groupBars(0f, 0.5f, 0.1f);
            barData.setDrawValues(false)

            // Set Pie data to pieChart.
            binding.barChart.data = barData
            binding.barChart.setTouchEnabled(false)
            binding.barChart.isDragEnabled = true
            binding.barChart.setExtraOffsets(10f, 10f, 10f, 10f)
        }


        // Lines color set
        binding.barChart.axisRight.gridColor = R.color.black
        binding.barChart.axisLeft.gridColor = R.color.black
        binding.barChart.axisLeft.axisLineColor = R.color.black
        binding.barChart.axisRight.axisLineColor = R.color.black
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)

//        binding.barChart.getXAxis().setCenterAxisLabels(true);
//        binding.barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

//        binding.barChart.getXAxis().setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return xAxisLabel.get((int) value);
//            }
//        });

        // Set y-Axis labels
        binding.barChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return floor(value.toDouble()).toString()
            }
        }

        // Remove line from chart
        binding.barChart.xAxis.setDrawGridLines(false)
        binding.barChart.xAxis.gridColor = R.color.black

        // Set x-Axis labels color
        binding.barChart.xAxis.textColor = R.color.black

        // Disable zoom
        binding.barChart.setScaleEnabled(false)
        binding.barChart.setPinchZoom(false)

        // Set y-Axis labels color
        binding.barChart.axisLeft.textColor = R.color.black


        // Remove right axis values
        binding.barChart.axisRight.isEnabled = false

        // Change x-Axis position to bottom
        binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.barChart.xAxis.axisLineColor = R.color.black

        // Set Count of x and y, how much data will be show
        binding.barChart.xAxis.setLabelCount(xAxisLabel.size, false)
        binding.barChart.axisLeft.setLabelCount(6, false)

        // Remove description from chart
        binding.barChart.description.isEnabled = false

        // Remove bottom legend from chart
        binding.barChart.legend.isEnabled = false

        //Set animation to chart
        binding.barChart.invalidate()
        binding.barChart.animateY(1000)

        binding.barChart.invalidate()
    }
}