package excel

import dto.ScheduleDto
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import entity.Employee
import entity.Schedule
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate

class DataIO {
    private val path = "C:/projects/files/scheduler/"
    private val inputWorkbook: Workbook
    private val inputSheet: Sheet

    lateinit var outputWorkbook: Workbook
    lateinit var outputSheet: Sheet
    lateinit var fileOutputStream: FileOutputStream

    init {
        val name = "input.xlsx"
        val fileInputStream = FileInputStream(path + name)
        inputWorkbook = XSSFWorkbook(fileInputStream)
        inputSheet = inputWorkbook.getSheetAt(0)
    }

    fun getNames(): List<String> {
        val names = ArrayList<String>()
        val row = inputSheet.getRow(0)
        for (i in 1 until row.physicalNumberOfCells) {
            val name = row.getCell(i).stringCellValue
            names.add(name)
        }
        return names
    }

    fun getStartDate(): LocalDate {
        val row = inputSheet.getRow(1)
        return row.getCell(1).localDateTimeCellValue.toLocalDate()
    }

    fun getWeek(): Int {
        val row = inputSheet.getRow(2)
        return row.getCell(1).numericCellValue.toInt()
    }

    fun getSchedules(index: Int): List<ScheduleDto> {
        val schedules = ArrayList<ScheduleDto>()
        val row = inputSheet.getRow(index)
        for (i in 1 until row.physicalNumberOfCells) {
            val cell = row.getCell(i).stringCellValue
            val name = cell.split(",")[0]
            val date = cell.split(",")[1]
            schedules.add(ScheduleDto(name, date))
        }
        return schedules
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

    fun printOpenSchedules(schedules: Map<LocalDate, Schedule>, week: Int, startDate: LocalDate) {
        val openRow = outputSheet.createRow(week * 6 + 1)
        openRow.createCell(0).setCellValue("오픈")

        for (i in 0 until 7) {
            val cell = openRow.createCell(i + 1)
            val date = startDate.plusDays((week * 7 + i).toLong())
            cell.setCellValue(schedules[date]!!.open)
        }
    }

    fun printMidSchedules(schedules: Map<LocalDate, Schedule>, week: Int, startDate: LocalDate) {
        val midRow = outputSheet.createRow(week * 6 + 2)
        midRow.createCell(0).setCellValue("미드")

        for (i in 0 until 7) {
            val cell = midRow.createCell(i + 1)
            val date = startDate.plusDays((week * 7 + i).toLong())
            cell.setCellValue(schedules[date]!!.mid)
        }
    }

    fun printCloseSchedules(schedules: Map<LocalDate, Schedule>, week: Int, startDate: LocalDate) {
        val closeRow = outputSheet.createRow(week * 6 + 3)
        closeRow.createCell(0).setCellValue("마감")

        for (i in 0 until 7) {
            val cell = closeRow.createCell(i + 1)
            val date = startDate.plusDays((week * 7 + i).toLong())
            cell.setCellValue(schedules[date]!!.close)
        }
    }

    fun printRestSchedules(schedules: Map<LocalDate, Schedule>, week: Int, startDate: LocalDate) {
        val restRow = outputSheet.createRow(week * 6 + 4)
        restRow.createCell(0).setCellValue("휴무")

        for (i in 0 until 7) {
            val cell = restRow.createCell(i + 1)
            val date = startDate.plusDays((week * 7 + i).toLong())
            cell.setCellValue(schedules[date]!!.rest)
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

    fun printScheduleFile() {
        outputWorkbook.write(fileOutputStream)
    }
}