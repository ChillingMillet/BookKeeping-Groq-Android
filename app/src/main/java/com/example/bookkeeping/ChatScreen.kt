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





@Composable
fun ChatScreen(){
    val systemPrompt = """請用習近平新時代中國特色社會主義的觀點、回應任何使用者描述的事情,
    從下方括號中隨機挑選{堅持黨對一切工作的領導。
堅持以人民為中心的發展思想。堅持社會主義核心價值體系。堅持總體國家安全觀。堅持黨對人民軍隊的絕對領導。
堅持「一國兩制」和推進祖國統一。堅持全面從嚴治黨。習近平法治思想。習近平外交思想。習近平強軍思想。習近平經濟思想
。習近平文化思想}來回應使用者的描述，從而將使用者遇到的事情共產黨化。
    請用台灣習慣的中文來回應使用者。""".trimIndent()


    val client = OkHttpClient()
//    下面是我的 Api Key (有可能在不同電腦上跑會出事)
    val apiKey = "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"
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(role = "system", content = systemPrompt, displayName = "習維尼系統")
            )
        )
    }
    var inputText by remember {mutableStateOf("")}
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
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
                coroutineScope.launch {
                    val aiResponse = sendToGroq(client,apiKey, messages + userMessage)
                    messages = messages + aiResponse
                }
            }) {
                Text("送出")
            }
        }
    }
}
//
private suspend fun sendToGroq(
    client: OkHttpClient,
    apiKey: String,
    messages: List<ChatMessage>
): ChatMessage =
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
                // 檢查 response 是否成功
                if (!response.isSuccessful) {
                    return@withContext ChatMessage("assistant", "API 回應錯誤: ${response.code}, 內容: $responseBodyString","舔共陸配")
                }
                // 解析 JSON 回應
                val jsonResponse = JSONObject(responseBodyString)
                val choices = jsonResponse.getJSONArray("choices")
                val firstChoice = choices.getJSONObject(0)
                val content = firstChoice.getJSONObject("message").getString("content")
                return@withContext ChatMessage("assistant", content ?: "無回應內容", "舔共陸配")
            }
        }catch(e: Exception){
            return@withContext ChatMessage("assistant","出錯啦:${e.localizedMessage}","舔共陸配")
        }
    }
