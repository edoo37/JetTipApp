package com.yasinsenel.jettipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yasinsenel.jettipapp.components.InputField
import com.yasinsenel.jettipapp.ui.theme.JetTipAppTheme
import com.yasinsenel.jettipapp.utils.calculateTip
import com.yasinsenel.jettipapp.utils.calculateTotalPerPerson
import com.yasinsenel.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content : @Composable () -> Unit){
    JetTipAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.onBackground
        ) {
            content()
        }

    }

}

@Preview
@Composable
fun TopHeader(totalPerPerson : Double = 134.5555){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp)
        .height(150.dp)
        .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
        //.clip(shape = RoundedCornerShape(corner = CornerSize(12.dp)))
        ) {
        val total = "%.2f".format(totalPerPerson)
        Column(modifier = Modifier.padding(12.dp),
               horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.Center) {
            Text(text = "Total Per Person",
                 style = MaterialTheme.typography.bodyLarge)
            Text(text = total,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Preview
@Composable
fun MainContent(){

    val range = IntRange(start = 1, endInclusive = 5)

    val splitByState = remember() {
        mutableStateOf(1)
    }
    val tipAmountState = remember() {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember() {
        mutableStateOf(0.0)
    }

    Column(modifier = Modifier.padding(12.dp)) {
        TopHeader(totalPerPerson = totalPerPersonState.value)
        BillForm(splitByState = splitByState,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState){it->
            println(it)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier,
             range : IntRange = 1..100,
             splitByState : MutableState<Int>,
             tipAmountState : MutableState<Double>,
             totalPerPersonState : MutableState<Double>,
             onValueChange : (String)-> Unit){
    val totalBillState = remember{
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val sliderState = remember() {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderState.value*100).toInt()






    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(modifier = modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(modifier = modifier
            .padding(6.dp)
            .fillMaxWidth(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start) {
                    InputField(valueState = totalBillState,
                        modifier = Modifier.fillMaxWidth(),
                                labelId = "Enter Bill",
                        enabled = true,
                        isSingleLine = true,
                        onAction = KeyboardActions {
                            if(!validState) return@KeyboardActions
                            onValueChange(totalBillState.value)
                            keyboardController?.hide()
                })
            if(validState){
                Row(modifier = modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top) {
                    Text(text = "Split",
                        modifier = Modifier.align(
                            alignment = Alignment.CenterVertically
                        ))
                    Spacer(modifier = modifier.width(120.dp))
                    Row(modifier = modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically){
                    RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                        if(splitByState.value<range.last){
                            splitByState.value = splitByState.value+1
                        }
                        totalPerPersonState.value = calculateTotalPerPerson(totalBillState.value.toDouble(),
                            splitByState.value,tipPercentage)
                    })
                    Text(text = "${splitByState.value}",
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 9.dp, end = 9.dp))
                    RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                        splitByState.value = if(splitByState.value>1) splitByState.value-1 else 1
                        totalPerPersonState.value = calculateTotalPerPerson(totalBillState.value.toDouble(),
                            splitByState.value,tipPercentage)
                    })
                    }

                }
                Row(modifier = modifier
                    .padding(horizontal = 3.dp, vertical = 12.dp)){
                    Text(text = "Tip",modifier.align(Alignment.CenterVertically))
                    Spacer(modifier = modifier.width(200.dp))
                    Text(text = "$ ${tipAmountState.value}",modifier.align(Alignment.CenterVertically))

                }
                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "% $tipPercentage")
                    Spacer(modifier = modifier.height(14.dp))
                    Slider(value = sliderState.value,
                        onValueChange = {
                            sliderState.value = it
                            if(totalBillState.value.isNotEmpty()){
                                tipAmountState.value = calculateTip(totalBillState.value.toDouble(),tipPercentage)
                            }
                            totalPerPersonState.value = calculateTotalPerPerson(totalBillState.value.toDouble(),
                                splitByState.value,tipPercentage)
                        },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    steps = 5,
                    onValueChangeFinished = { println("DENEME") })
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {
        Text(text = "Selam Yasin")
    }
}