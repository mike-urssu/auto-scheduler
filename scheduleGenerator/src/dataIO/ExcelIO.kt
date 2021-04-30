package dataIO

import dto.ScheduleDto
import entity.Employee
import entity.Schedule
import entity.Time
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.time.LocalDate
import kotlin.system.exitProcess

class ExcelIO {
    private val path = "C:/projects/files/scheduler/"
    private var inputWorkbook: Workbook
    private var inputSheet: Sheet
    private lateinit var outputWorkbook: Workbook
    private lateinit var outputSheet: Sheet
    private lateinit var fileOutputStream: FileOutputStream

    init {
        try {
            val name = "input.xlsx"
            val fileInputStream = FileInputStream(path + name)
            inputWorkbook = XSSFWorkbook(fileInputStream)
            inputSheet = inputWorkbook.getSheetAt(0)
        } catch (e: FileNotFoundException) {
            println("C:/projects/files/scheduler 폴더에 엑셀 파일이 존재하지 않습니다.\n")
            println("엑셀 파일이 해당 위치에 있는지 확인하세요.\n")
            Thread.sleep(1000)
            println("프로그램을 종료합니다.\n")
            Thread.sleep(1000)
            exitProcess(-1)
        }
    }

    fun getNames(): List<String> {
        try {
            val names = ArrayList<String>()
            val row = inputSheet.getRow(0)
            for (i in 1 until row.physicalNumberOfCells) {
                val name = row.getCell(i).stringCellValue
                names.add(name)
            }

            if (names.size != 3)
                throw Exception()

            return names
        } catch (e: Exception) {
            println("직원 이름이 유효하지 않습니다.")
            Thread.sleep(1000)
            println("프로그램을 종료합니다.")
            Thread.sleep(1000)
            exitProcess(-1)
        }
    }

    fun getStartDate(): LocalDate {
        try {
            val row = inputSheet.getRow(1)
            return row.getCell(1).localDateTimeCellValue.toLocalDate()
        } catch (e: Exception) {
            println("시작 날짜가 유효하지 않습니다.")
            Thread.sleep(1000)
            println("프로그램을 종료합니다.")
            Thread.sleep(1000)
            exitProcess(-1)
        }
    }

    fun getWeek(): Int {
        try {
            val row = inputSheet.getRow(2)
            return row.getCell(1).numericCellValue.toInt()
        } catch (e: Exception) {
            println("기간이 유효하지 않습니다.")
            Thread.sleep(1000)
            println("프로그램을 종료합니다.")
            Thread.sleep(1000)
            exitProcess(-1)
        }
    }

    fun getSchedules(index: Int): List<ScheduleDto> {
        try {
            val schedules = ArrayList<ScheduleDto>()
            val row = inputSheet.getRow(index)
            for (i in 1 until row.physicalNumberOfCells) {
                val cell = row.getCell(i).stringCellValue
                val name = cell.split(",")[0]
                val date = cell.split(",")[1]
                schedules.add(ScheduleDto(name, date))
            }
            return schedules
        } catch (e: Exception) {
            if (index == 3)
                println("미드 날짜가 유효하지 않습니다.")
            else
                println("휴무 날짜가 유효하지 않습니다.")
            Thread.sleep(1000)
            println("프로그램을 종료합니다.")
            Thread.sleep(1000)
            exitProcess(-1)
        }
    }

    fun readyToPrint() {
        outputWorkbook = XSSFWorkbook()
        outputSheet = outputWorkbook.createSheet("schedule")
        val name = "output.xlsx"
        fileOutputStream = FileOutputStream(path + name)
    }

    fun printDate(startDate: LocalDate, week: Int) {
        val dateRow = outputSheet.createRow(week * 6)
        for (i in 0 until 7) {
            val cell = dateRow.createCell(i + 1)
            cell.setCellValue(startDate.plusDays((week * 7 + i).toLong()).toString().substring(5))
        }
    }

    fun printSchedules(schedules: Map<LocalDate, Schedule>, week: Int, startDate: LocalDate, time: Time) {
        val row: Row
        when (time) {
            Time.OPEN -> {
                row = outputSheet.createRow(week * 6 + 1)
                row.createCell(0).setCellValue("오픈")
            }
            Time.MID -> {
                row = outputSheet.createRow(week * 6 + 2)
                row.createCell(0).setCellValue("미드")
            }
            Time.CLOSE -> {
                row = outputSheet.createRow(week * 6 + 3)
                row.createCell(0).setCellValue("마감")
            }
            Time.REST -> {
                row = outputSheet.createRow(week * 6 + 4)
                row.createCell(0).setCellValue("휴무")
            }
        }

        for (i in 0 until 7) {
            val cell = row.createCell(i + 1)
            val date = startDate.plusDays((week * 7 + i).toLong())
            when (time) {
                Time.OPEN -> cell.setCellValue(schedules[date]!!.open)
                Time.MID -> cell.setCellValue(schedules[date]!!.mid)
                Time.CLOSE -> cell.setCellValue(schedules[date]!!.close)
                Time.REST -> cell.setCellValue(schedules[date]!!.rest)
            }
        }
    }

    fun printTotalWorkCount(employees: Map<String, Employee>) {
        val timeRow = outputSheet.getRow(6)
        timeRow.createCell(10).setCellValue("오픈")
        timeRow.createCell(11).setCellValue("미드")
        timeRow.createCell(12).setCellValue("마감")
        timeRow.createCell(13).setCellValue("휴무")

        var rowIndex = 7
        for (name in employees.keys) {
            val currentRow = outputSheet.getRow(rowIndex++)
            currentRow.createCell(9).setCellValue(name)
            currentRow.createCell(10).setCellValue(employees[name]!!.open.toString())
            currentRow.createCell(11).setCellValue(employees[name]!!.mid.toString())
            currentRow.createCell(12).setCellValue(employees[name]!!.close.toString())
            currentRow.createCell(13).setCellValue(employees[name]!!.rest.toString())
        }
    }

    fun exportScheduleFile() {
        outputWorkbook.write(fileOutputStream)
    }
}