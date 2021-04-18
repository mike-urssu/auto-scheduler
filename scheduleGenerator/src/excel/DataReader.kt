package excel

import dto.ScheduleDto
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.time.LocalDate

class DataReader {
    private val file = "C:/projects/files/scheduler/scheduler_input_format.xlsx"
    private var sheet: Sheet

    init {
        val fileInputStream = FileInputStream(file)
        val workbook = XSSFWorkbook(fileInputStream)
        sheet = workbook.getSheetAt(0)
    }

    fun getNames(): List<String> {
        val names = ArrayList<String>()
        val row = sheet.getRow(0)
        for (i in 1 until row.physicalNumberOfCells) {
            val name = row.getCell(i).stringCellValue
            names.add(name)
        }
        return names
    }

    fun getStartDate(): LocalDate {
        val row = sheet.getRow(1)
        return row.getCell(1).localDateTimeCellValue.toLocalDate()
    }

    fun getWeek(): Int {
        val row = sheet.getRow(2)
        return row.getCell(1).numericCellValue.toInt()
    }

    fun getSchedules(index: Int): List<ScheduleDto> {
        val midSchedules = ArrayList<ScheduleDto>()
        val row = sheet.getRow(index)
        for (i in 1 until row.physicalNumberOfCells) {
            val cell = row.getCell(i).stringCellValue
            val name = cell.split(",")[0]
            val date = cell.split(",")[1]
            val midScheduleDto = ScheduleDto(name, date)
            midSchedules.add(midScheduleDto)
        }
        return midSchedules
    }
}