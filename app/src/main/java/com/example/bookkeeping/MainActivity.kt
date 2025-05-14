package com.example.bookkeeping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.bookkeeping.ui.theme.BookKeepingTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

//import com.example.bookkeeping.ui.theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookKeepingTheme  {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val presidents = listOf(
        President("川普", "USA", R.drawable.trump),
        President("習近平", "China", R.drawable.xi),
        President("石破茂", "Japan", R.drawable.shigeru),
        President("尹錫悅", "South Korea", R.drawable.yeol),
    )

    NavHost(navController = navController, startDestination = "carousel") {
        composable("carousel") {
            CarouselScreen(presidents, navController)
        }
        composable("main/{presidentName}") { backStackEntry ->
            val presidentName = backStackEntry.arguments?.getString("presidentName") ?: ""
            ChoosingScreen(navController, presidentName)
        }
        composable("chat/{presidentName}") { backStackEntry ->
            val presidentName = backStackEntry.arguments?.getString("presidentName") ?: ""
            ChatScreen(navController, presidentName)
        }
        composable("webview_china") {
            WebViewScreen("https://zh.wikipedia.org/zh-tw/辱包文化")
        }
        composable("webview_america") {
            WebViewScreen("https://zh.wikipedia.org/wiki/%E5%94%90%E7%B4%8D%C2%B7%E5%B7%9D%E6%99%AE")
        }
        composable("webview_japan") {
            WebViewScreen("https://zh.wikipedia.org/zh-tw/%E7%9F%B3%E7%A0%B4%E8%8C%82")
        }
        composable("webview_korea") {
            WebViewScreen("https://zh.wikipedia.org/wiki/%E5%B0%B9%E9%94%A1%E6%82%A6")
        }
    }
}


@Composable
fun ChoosingScreen(navController: NavHostController, presidentName: String) {
    val images = when (presidentName) {
        "習近平" -> listOf(R.drawable.xi_1, R.drawable.xi_2)
        "川普" -> listOf(R.drawable.trump1, R.drawable.trump2)
        "石破茂" -> listOf(R.drawable.shigeru1, R.drawable.shigeru2)
        "尹錫悅" -> listOf(R.drawable.yeol1, R.drawable.yeol2)
        else -> listOf(R.drawable.default1, R.drawable.default1)
    }

    var currentImageIndex by remember { mutableStateOf(0) }

    val title = when (presidentName) {
        "習近平" -> "$presidentName 的思想中心"
        "川普" -> "$presidentName 的推特發言牆"
        "石破茂" -> "$presidentName 的政策構想區"
        "尹錫悅" -> "$presidentName 的國政聊天室"
        else -> "$presidentName 的總統專區"
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000L)
            currentImageIndex = (currentImageIndex + 1) % images.size
        }
    }

    // ✅ 外層 Box 用來疊加內容 + 固定底部欄
    Box(modifier = Modifier.fillMaxSize()) {
        // 主內容區塊
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 60.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 60.dp, bottom = 60.dp)
            )

            Image(
                painter = painterResource(id = images[currentImageIndex]),
                contentDescription = "$presidentName 圖片",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 12f)
                    .padding(bottom = 35.dp)
            )
///////////////////////////////////////////////////////////////////////////////////
            Button(
                onClick = {
                    if (title == "習近平 的思想中心") {
                        navController.navigate("chat/習近平")
                    }else if(title == "川普 的推特發言牆") {
                        navController.navigate("chat/川普")
                    }else if(title == "石破茂 的政策構想區") {
                        navController.navigate("chat/石破茂")
                    }else if(title == "尹錫悅 的國政聊天室") {
                        navController.navigate("chat/尹錫悅")
                    }
                },
                /////////////////////////////////////////////////////////////////
                modifier = Modifier
                    .width(450.dp)
                    .height(64.dp) // ✅ 加入高度
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(65.dp) // ✅ 邊角弧度
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp // ✅ 調整字體大小
                )
            }

/////////////////////////////////////////////
            Button(
                onClick = {
                    when (presidentName) {
                        "尹錫悅" -> navController.navigate("webview_korea")
                        "川普" -> navController.navigate("webview_america")
                        "習近平" -> navController.navigate("webview_china")
                        "石破茂" -> navController.navigate("webview_japan")
                    }
                },
                modifier = Modifier
                    .width(450.dp)
                    .height(64.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(65.dp)
            ) {
                Text(
                    text = "閱讀 $presidentName 的文章",
                    fontSize = 20.sp
                )
            }

        //////////////////////////////////////////////////////////////
        }

        // ✅ 疊加的底部橫條，不影響上方
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(MaterialTheme.colorScheme.primary)
                .align(Alignment.BottomCenter) // ✅ 固定在底部
        )
    }
}





fun samplePresidents(): List<President> {
    return listOf(
        President("川普", "USA", R.drawable.trump),
        President("習近平", "China", R.drawable.xi),
        President("石破茂", "Japan", R.drawable.shigeru),
        President("尹錫悅", "South Korea", R.drawable.yeol),
    )
}

@Composable
fun HomeScreen(presidents: List<President>, navController: NavHostController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = "選擇總統",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(presidents) { president ->
                PresidentCard(president) {
                    navController.navigate("detail/${president.name}")
                }
            }
        }
    }
}

@Composable
fun CarouselScreen(presidents: List<President>, navController: NavHostController) {
    var currentIndex by remember { mutableStateOf(0) }
    val currentPresident = presidents[currentIndex]

    Box(modifier = Modifier.fillMaxSize()) {
        // 主標題
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 60.dp, top = 60.dp, end = 24.dp)) {

            Text(
                text = "🌐 領袖互動中心",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 40.sp),
                color = MaterialTheme.colorScheme.primary
            )

            Divider(
                color = Color.Black,
                thickness = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, end = 24.dp) // 和標題對齊右邊
            )
        }

        // 主內容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 225.dp, bottom = 40.dp), // 留空間給底部條
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "選擇你想查看的總統",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { if (currentIndex > 0) currentIndex-- },
                    enabled = currentIndex > 0
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        contentDescription = "上一個"
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .width(200.dp)
                ) {
                    Image(
                        painter = painterResource(id = currentPresident.imageResId),
                        contentDescription = currentPresident.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentPresident.name,
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 30.sp)
                    )
                    Text(
                        text = currentPresident.country,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                IconButton(
                    onClick = { if (currentIndex < presidents.lastIndex) currentIndex++ },
                    enabled = currentIndex < presidents.lastIndex
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = "下一個"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate("main/${currentPresident.name}")
                },
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth(), // 讓按鈕左右撐滿 padding 範圍
                shape = RoundedCornerShape(35), // 圓弧更順眼，不建議 300.dp 過頭
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = "確定選擇 ${currentPresident.name}",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                )
            }

        }

        // ✅ 正確放法：在 Box 的底部放長方形
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(MaterialTheme.colorScheme.primary)
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun PresidentCard(president: President, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE0F7FA))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(id = president.imageResId),
            contentDescription = president.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = president.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = president.country,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

@Composable
fun DetailScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "這是 $name 的詳細資料頁面。",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}
