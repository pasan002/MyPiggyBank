package com.example.mypiggybank.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypiggybank.data.Transaction
import com.example.mypiggybank.data.TransactionType
import com.example.mypiggybank.databinding.FragmentDashboardBinding
import com.example.mypiggybank.ui.adapter.TransactionAdapter
import com.example.mypiggybank.viewmodel.DashboardViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.*

private const val TAG = "DashboardFragment"

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupPieChart()
        observeViewModel()
        
        // Force a refresh of data
        viewModel.refreshData()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
            transactionList = emptyList(),
            onItemClick = { transaction -> 
                Log.d(TAG, "Transaction clicked: ${transaction.description}")
            }
        )
        binding.rvRecentTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1400, Easing.EaseInOutQuad)
            legend.isEnabled = true
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
        }
    }

    private fun observeViewModel() {
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            Log.d(TAG, "Received ${transactions.size} transactions")
            updateDashboard(transactions)
            transactionAdapter.updateTransactions(transactions)
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            Log.d(TAG, "Received total income: $income")
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            binding.tvTotalIncome.text = currencyFormat.format(income)
        }

        viewModel.totalExpenses.observe(viewLifecycleOwner) { expenses ->
            Log.d(TAG, "Received total expenses: $expenses")
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            binding.tvTotalExpenses.text = currencyFormat.format(expenses)
        }

        viewModel.currentBalance.observe(viewLifecycleOwner) { balance ->
            Log.d(TAG, "Received current balance: $balance")
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            binding.tvBalance.text = currencyFormat.format(balance)
        }
    }

    private fun updateDashboard(transactions: List<Transaction>) {
        Log.d(TAG, "Updating dashboard with ${transactions.size} transactions")
        updateCharts(transactions)
    }

    private fun updateCharts(transactions: List<Transaction>) {
        if (transactions.isEmpty()) {
            binding.pieChart.setNoDataText("No transactions to display")
            binding.pieChart.invalidate()
            return
        }

        // Group transactions by type and category
        val expensesByCategory = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { (_, transactions) -> 
                transactions.sumOf { it.amount }
            }

        val incomeByCategory = transactions
            .filter { it.type == TransactionType.INCOME }
            .groupBy { it.category }
            .mapValues { (_, transactions) -> 
                transactions.sumOf { it.amount }
            }

        Log.d(TAG, "Expenses by category: $expensesByCategory")
        Log.d(TAG, "Income by category: $incomeByCategory")

        // Create pie chart entries
        val entries = mutableListOf<PieEntry>()
        
        // Add expense entries (without labels)
        expensesByCategory.forEach { (_, amount) ->
            entries.add(PieEntry(amount.toFloat()))
        }
        
        // Add income entries (without labels)
        incomeByCategory.forEach { (_, amount) ->
            entries.add(PieEntry(amount.toFloat()))
        }

        if (entries.isEmpty()) {
            binding.pieChart.setNoDataText("No transactions to display")
            binding.pieChart.invalidate()
            return
        }

        // Create color list - Blue for expenses, Green for income
        val colors = mutableListOf<Int>()
        val expenseColor = Color.rgb(33, 150, 243)  // Material Blue
        val incomeColor = Color.rgb(76, 175, 80)    // Material Green

        // Assign colors - blue for expenses, green for income
        repeat(expensesByCategory.size) {
            colors.add(expenseColor)
        }
        repeat(incomeByCategory.size) {
            colors.add(incomeColor)
        }

        // Create and configure the dataset
        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            valueTextSize = 16f
            valueTextColor = Color.BLACK  // Make values more visible
            valueFormatter = PercentFormatter(binding.pieChart)
            valueLinePart1Length = 0.2f
            valueLinePart2Length = 0.4f
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        }

        // Create and set the data
        val pieData = PieData(dataSet).apply {
            setValueTextSize(14f)
            setValueTextColor(Color.BLACK)
        }

        binding.pieChart.apply {
            data = pieData
            description.isEnabled = false
            legend.isEnabled = true        // Enable legend
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)     // Show entry labels
            setDrawEntryLabels(true)       // Enable entry labels
            centerText = "Transactions"
            setCenterTextSize(16f)
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            invalidate() // refresh
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 