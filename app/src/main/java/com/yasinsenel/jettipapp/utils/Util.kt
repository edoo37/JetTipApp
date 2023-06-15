package com.yasinsenel.jettipapp.utils

val calculateTip: (Double, Int)-> Double ={
    totalBill,tipPercentage -> if(totalBill>1 &&
        totalBill.toString().isNotEmpty())
        (totalBill*tipPercentage)/100 else 0.0
}

val calculateTotalPerPerson : (Double, Int,Int) -> Double ={
    totalBill,splitBy,tipPercentage->
    (calculateTip(totalBill,tipPercentage) + totalBill)/splitBy
}

