package dataIO

import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

object RequestIO {

    private val url = URL("http://worldclockapi.com/api/json/utc/now")
    private val connection = url.openConnection() as HttpURLConnection

    fun getKSTServerTime(): LocalDate {
        val httpResponse = sendRequest()
        val utc = getUTCServerTime(httpResponse)
        return convertUTCIntoKST(utc)
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

    private fun getUTCServerTime(httpResponse: String): LocalDateTime {
        val parsedJson = JsonParser.parseString(httpResponse)
        val jsonObject = parsedJson.asJsonObject
        val currentDateTime = jsonObject.asJsonObject["currentDateTime"].toString().replace("\"", "")
        return LocalDateTime.parse(
            currentDateTime,
            DateTimeFormatter.ISO_DATE_TIME
        )
    }

    private fun convertUTCIntoKST(utc: LocalDateTime): LocalDate {
        return utc.plusHours(9).toLocalDate()
    }
}