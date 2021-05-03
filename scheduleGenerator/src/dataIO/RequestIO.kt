package dataIO

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import dto.WorldClockApiResponse
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

object RequestIO {
    private val url = URL("http://worldtimeapi.org/api/timezone/Asia/Seoul")
    private val connection = url.openConnection() as HttpURLConnection

    fun getKSTServerTime(): LocalDate {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(
                LocalDateTime::class.java,
                JsonDeserializer { json, _, _ ->
                    LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_DATE_TIME)
                }).create()
        val httpResponse = sendRequest()
        val worldClockApiResponse = gson.fromJson(httpResponse, WorldClockApiResponse::class.java)
        return worldClockApiResponse.datetime.toLocalDate()
    }

    private fun sendRequest(): String {
        connection.requestMethod = "GET"
        val responseCode = connection.responseCode
        if (responseCode != 200) {
            println("인터넷 연결상태를 확인해주세요\n")
            println("시스템을 종료합니다.\n")
            exitProcess(-1)
        }

        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val httpResponse = StringBuffer()
        while (true) {
            val input = reader.readLine()
            if (input.isNullOrEmpty())
                break
            else
                httpResponse.append(input)
        }
        return httpResponse.toString()
    }
}