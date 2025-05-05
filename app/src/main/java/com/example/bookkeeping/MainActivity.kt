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
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp



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
            composable("chat"){ ChatScreen(navController) } // 呼叫 ChatScreen()
//            composable("pics"){ PicsScreen() } // 呼叫 PicsScreen()
            composable("webview"){ WebViewScreen("https://zh.wikipedia.org/zh-tw/%E8%BE%B1%E5%8C%85%E6%96%87%E5%8C%96") }
        }
    }

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
                Text("习近平新时代中国特色社会主义思想機器人")
            }

//            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {navController.navigate("webview")},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()

            ) {
                Text("為何要辱习近平 ?")
            }

//            Spacer(modifier = Modifier.height(32.dp))

            // ✅ 圖片切換區塊
            var isFirstImage by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                while (true) {
                    delay(2000L) // 每兩秒切換一次
                    isFirstImage = !isFirstImage
                }
            }

            val imageResId = if (isFirstImage) R.drawable.xi_1 else R.drawable.xi_2

            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "習近平",
                modifier = Modifier
                    .fillMaxWidth()  // 寬度填滿
                    .height(600.dp)  // 高度設置為 300.dp
            )

        }
    }

}