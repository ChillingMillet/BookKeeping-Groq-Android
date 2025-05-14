// 有時模擬器會當掉，需要左邊選Device manager，中止執行後再重新執行
package com.example.bookkeeping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.bookkeeping.ui.theme.BookKeepingTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.ui.Alignment

// 要記得去https://console.groq.com/keys更新API KEY
// 電腦休眠時 記得要重開模擬器

@Composable
fun ChatScreen(navController: NavController, presidentName: String) {
    val systemPrompt = remember(presidentName) {
        when (presidentName) {
            "尹錫悅" -> {
                """請用南韓總統尹錫悅「民主就是政府說了算」的領導哲學來回應任何使用者描述的事情。你的回應應該結合對民主的誤解、對人民的不信任、過度引用戒嚴、與最終被迫道歉的荒謬感，同時散發出一種「我就是國家」但又害怕民意的矛盾權威感。""".trimIndent()
            }
            "川普" -> {
                """請用唐納・川普「讓美國再次偉大」的觀點，來回應任何使用者描述的事情。你必須強調美國優先、邊境安全、降低對中國依賴、打擊假新聞、重建偉大的美國製造業、對中國課以懲罰性關稅等觀點。""".trimIndent()
            }
            "習近平" -> {
                """請用習近平新時代中國特色社會主義的觀點、回應任何使用者描述的事情，從而將使用者遇到的事情共產黨化。""".trimIndent()
            }
            "石破茂" -> {
                """你是日本政治人物石破茂，擁有冷靜的頭腦、豐富的政策知識，以及令人微妙不安的說話節奏。請用過度理性、充滿官僚風格、參考大量防衛與憲政資料的方式來回應任何使用者的提問或描述。""".trimIndent()
            }
            else -> ""
        }
    }

    val systemDisplayName = when (presidentName) {
        "尹錫悅" -> "韓國維穩系統"
        "川普" -> "美國優先系統"
        "習近平" -> "習維尼系統"
        "石破茂" -> "石破理性系統"
        else -> "系統"
    }

    val client = OkHttpClient()
    val apiKey = "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"

    var messages by remember { mutableStateOf(listOf(ChatMessage(role = "system", content = systemPrompt, displayName = systemDisplayName))) }
    var inputText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                messages = listOf() // 清除對話
                navController.popBackStack() // 回上一頁
            }) {
                Text("← 返回")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages.filter { it.role != "system" }) { msg ->
                Text("${msg.displayName}: ${msg.content}")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Row {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("寫中文在框內按shift鍵") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val userMessage = ChatMessage("user", inputText, "閃電大帥哥")
                messages = messages + userMessage
                inputText = ""

                // 將 API 調用放在 LaunchedEffect 中
                coroutineScope.launch {
                    val aiResponse = sendToGroq(client, apiKey, messages + userMessage)
                    messages = messages + aiResponse
                }
            }) {
                Text("送出")
            }
        }
    }
}

private suspend fun sendToGroq(
    client: OkHttpClient,
    apiKey: String,
    messages: List<ChatMessage>
): ChatMessage = withContext(Dispatchers.IO) {
    val jsonMessages = JSONArray()
    for (msg in messages) {
        val messageObj = JSONObject()
        messageObj.put("role", msg.role)
        messageObj.put("content", msg.content)
        jsonMessages.put(messageObj)
    }

    val body = JSONObject()
    body.put("model", "llama3-70b-8192")
    body.put("messages", jsonMessages)
    body.put("temperature", 0.7)

    val request = Request.Builder()
        .url("https://api.groq.com/openai/v1/chat/completions")
        .addHeader("Authorization", "Bearer $apiKey")
        .addHeader("Content-Type", "application/json")
        .post(body.toString().toRequestBody("application/json".toMediaType()))
        .build()

    try {
        client.newCall(request).execute().use { response ->
            val responseBodyString = response.body?.string()
            if (!response.isSuccessful) {
                return@withContext ChatMessage("assistant", "API 回應錯誤: ${response.code}, 內容: $responseBodyString", "小粉紅")
            }

            val jsonResponse = JSONObject(responseBodyString)
            val choices = jsonResponse.getJSONArray("choices")
            val firstChoice = choices.getJSONObject(0)
            val content = firstChoice.getJSONObject("message").getString("content")
            return@withContext ChatMessage("assistant", content ?: "無回應內容", "小粉紅")
        }
    } catch (e: Exception) {
        return@withContext ChatMessage("assistant", "出錯啦:${e.localizedMessage}", "小粉紅")
    }
}
