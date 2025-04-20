package com.example.bookkeeping

import android.os.Bundle
import androidx.activity.ComponentActivity //?
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookkeeping.ui.theme.BookKeepingTheme //?

class MainActivity : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookKeepingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavigation()
                }
            }
        }
    }
    @Composable
    fun AppNavigation(){
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "main"){
            composable("main"){ChoosingScreen(navController)}
            composable("chat"){ ChatScreen() } // 呼叫 BakingScreen()
        }
    }
//
//    @Composable
//    fun ChatActivityUI() {
//        BakingScreen()
//    }

    @Composable
    fun ChoosingScreen(navController: NavHostController){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center
        ){
            Button(onClick = {navController.navigate("chat")},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()

                ) {
                Text("選擇對話機器人")
            }
        }
    }

}