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
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private val client = OkHttpClient()
//    下面是我的 Api Key (有可能在不同電腦上跑會出事)
    private val apiKey = "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"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookKeepingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    BakingScreen()
                }
            }
        }
    }

    @Composable
    fun BakingScreen(){
        var messages by remember {mutableStateOf(listOf<ChatMessage>())}
        var inputText by remember {mutableStateOf("")}
        val coroutineScope = rememberCoroutineScope()

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(messages) { msg ->
                    Text("${msg.role}: ${msg.content}")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Row {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("寫中文請用虛擬鍵盤") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val userMessage = ChatMessage("user", inputText)
                    messages = messages + userMessage
                    inputText = ""
                    coroutineScope.launch {
                        val aiResponse = sendToGroq(messages + userMessage)
                        messages = messages + aiResponse
                    }
                }) {
                    Text("送出")
                }
            }
        }
    }
    private suspend fun sendToGroq(messages: List<ChatMessage>): ChatMessage =
        withContext(Dispatchers.IO){
            // 構建 JSON 消息列表
            val jsonMessages = JSONArray()
            for (msg in messages) {
                val messageObj = JSONObject()
                messageObj.put("role", msg.role)
                messageObj.put("content", msg.content)
                jsonMessages.put(messageObj)
            }
            val body = JSONObject()
            body.put("model","llama3-70b-8192")
            body.put("messages", jsonMessages)
            body.put("temperature", 0.7)
            val request = Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body.toString().toRequestBody("application/json".toMediaType()))
                .build()
            try{
                client.newCall(request).execute().use{ response ->
                    val responseBodyString = response.body?.string()
                    // 解析 JSON 回應
                    val jsonResponse = JSONObject(responseBodyString)
                    val choices = jsonResponse.getJSONArray("choices")
                    val firstChoice = choices.getJSONObject(0)
                    val content = firstChoice.getJSONObject("message").getString("content")
                    return@withContext ChatMessage("陰謀論機器人", content ?: "無回應內容")
                }
            }catch(e: Exception){
                return@withContext ChatMessage("assistant","出錯啦:${e.localizedMessage}")
            }
        }
}