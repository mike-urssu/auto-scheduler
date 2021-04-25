package schedule

class Employee(val name: String) {
    var count: Int = 0
    var used: Boolean = false

    var open: Int = 0
    var mid: Int = 0
    var close: Int = 0
    var rest: Int = 0
}