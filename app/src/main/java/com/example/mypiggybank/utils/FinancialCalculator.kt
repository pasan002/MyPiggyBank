package com.example.mypiggybank.utils

import kotlin.math.pow

object FinancialCalculator {
    

    fun calculateEMI(principal: Double, annualRate: Double, years: Double): Triple<Double, Double, Double> {
        val monthlyRate = annualRate / (12 * 100) // Convert annual rate to monthly decimal
        val months = years * 12 // Convert years to months

        val emi = principal * monthlyRate * (1 + monthlyRate).pow(months) /
                ((1 + monthlyRate).pow(months) - 1)
        val totalAmount = emi * months
        val totalInterest = totalAmount - principal

        return Triple(emi, totalAmount, totalInterest)
    }


    fun calculateSIP(monthlyInvestment: Double, annualRate: Double, years: Double): Triple<Double, Double, Double> {
        val monthlyRate = annualRate / (12 * 100)
        val months = years * 12
        
        val totalInvestment = monthlyInvestment * months
        val totalValue = monthlyInvestment * ((1 + monthlyRate).pow(months) - 1) * 
                (1 + monthlyRate) / monthlyRate
        val totalGain = totalValue - totalInvestment

        return Triple(totalInvestment, totalValue, totalGain)
    }




    fun splitBill(totalAmount: Double, numberOfPeople: Int, tipPercentage: Double = 0.0): Triple<Double, Double, Double> {
        val tipAmount = totalAmount * (tipPercentage / 100)
        val totalWithTip = totalAmount + tipAmount
        val amountPerPerson = totalWithTip / numberOfPeople
        
        return Triple(amountPerPerson, tipAmount, totalWithTip)
    }


    fun convertCurrency(amount: Double, exchangeRate: Double): Triple<Double, Double, Double> {
        val convertedAmount = amount * exchangeRate
        return Triple(amount, exchangeRate, convertedAmount)
    }
} 