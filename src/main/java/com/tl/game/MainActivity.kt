@file:OptIn(ExperimentalMaterial3Api::class)

package com.tl.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tl.game.ui.theme.GameTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            val columnSize = 8
            val rowSize = 10
            val boxWidth = screenWidth / columnSize
            val timeInSeconds = 30
            val otherColors = listOf(Color.Magenta, Color.Gray, Color.Yellow, Color.Blue, Color.Green)
            GameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var lastClickedTime by remember { mutableStateOf(System.currentTimeMillis()) }
                    var score by remember { mutableStateOf(0) }
                    var fastestReactionTime by remember { mutableStateOf(60000000L) }
                    var gameNumber by remember { mutableStateOf(0) }
                    var maxScore by remember { mutableStateOf(0) }
                    var timeLeft by remember { mutableStateOf(timeInSeconds) }
                    var redRowNum by remember { mutableStateOf(3) }
                    var redColNum by remember { mutableStateOf(2) }
                    var otherRowNum by remember { mutableStateOf(3) }
                    var otherColNum by remember { mutableStateOf(2) }
                    var showScore by remember { mutableStateOf(false) }
                    var otherColor by remember { mutableStateOf(otherColors.random()) }
                    val scope = rememberCoroutineScope()
                    LaunchedEffect(gameNumber) {
                        scope.launch {
                            while (timeLeft > 0) {
                                delay(1000)
                                timeLeft--
                            }
                        }
                    }
                    LaunchedEffect(timeLeft) {
                        if(timeLeft == 0) {
                            showScore = true
                            if (score > maxScore) {
                                maxScore = score
                            }
                        }
                    }
                    Column (Modifier.padding(innerPadding)) {
                        Spacer(Modifier.height(20.dp))
                        Text("We See Red", fontSize = 32.sp, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), textAlign = TextAlign.Center)
                        Spacer(Modifier.height(20.dp))
                        Text("Max Score: $maxScore", fontSize = 24.sp, modifier = Modifier.padding(horizontal = 16.dp))
                        Text("Score: $score", fontSize = 24.sp, modifier = Modifier.padding(horizontal = 16.dp))
                        Text("Time Left: $timeLeft", fontSize = 24.sp, modifier = Modifier.padding(horizontal = 16.dp))
                        val fastestReactionTimeInText = if(fastestReactionTime < 60000000L) fastestReactionTime else ""
                        Text("Fastest Reaction Time: $fastestReactionTimeInText", fontSize = 24.sp, modifier = Modifier.padding(horizontal = 16.dp))
                        Spacer(Modifier.height(40.dp))
                        Column {
                            for (rowNum in 0 until rowSize) {
                                Row {
                                    for (columnNum in 0 until columnSize) {
                                        Box(
                                            Modifier
                                                .size(boxWidth)
                                                .background(
                                                    if (rowNum == redRowNum && columnNum == redColNum) Color.Red else {
                                                        if(rowNum == otherRowNum && columnNum == otherColNum) {
                                                            otherColor
                                                        } else {
                                                            Color.White
                                                        }
                                                    }
                                                ).clickable(
                                                    onClick = {
                                                        if (rowNum == redRowNum && columnNum == redColNum && timeLeft > 0){
                                                            redRowNum = (0..rowSize-1).random()
                                                            redColNum = (0..columnSize-1).random()
                                                            otherRowNum = (0..rowSize-1).random()
                                                            otherColNum = (0..columnSize-1).random()
                                                            score++
                                                            otherColor = otherColors.random()
                                                            val reactionTime = System.currentTimeMillis() - lastClickedTime
                                                            if(reactionTime < fastestReactionTime) {
                                                                fastestReactionTime = reactionTime
                                                            }
                                                            lastClickedTime = System.currentTimeMillis()
                                                        }

                                                    },
                                                    indication = null,
                                                    interactionSource = remember { MutableInteractionSource() }
                                                )
                                        ) {

                                        }
                                    }
                                }

                            }
                        }
                    }
                    if(showScore) {
                        AlertDialog({}) {
                            Card {
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text(
                                        "Your Score is $score",
                                        modifier = Modifier.padding(20.dp),
                                        fontSize = 30.sp
                                    )
                                }

                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Button({
                                        showScore = false
                                        timeLeft = timeInSeconds
                                        score = 0
                                        gameNumber++
                                    }, modifier = Modifier.padding(bottom = 20.dp)) {
                                        Text("Let's Play Again!", fontSize = 24.sp)
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}