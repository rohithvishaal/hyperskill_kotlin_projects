
enum class Conversions(private val factor: Double, private val type: String) {
    METER(1.0, "length"),
    KILOMETER(1000.0, "length"),
    CENTIMETER(0.01, "length"),
    MILLIMETER(0.001, "length"),
    MILE(1609.35, "length"),
    YARD(0.9144, "length"),
    FOOT(0.3048, "length"),
    INCH(0.0254, "length"),
    GRAM(1.0, "weight"),
    KILOGRAM(1000.0, "weight"),
    MILLIGRAM(0.001, "weight"),
    POUND(453.592, "weight"),
    OUNCE(28.3495, "weight"),
    CELSIUS(0.0, "temperature"),
    FAHRENHEIT(0.0, "temperature"),
    KELVIN(0.0, "temperature"),
    UNKNOWN(0.0, "???");

    fun getConversionValue(): Double {
        return this.factor
    }

    fun getType(): String {
        return this.type
    }
}

fun getMeasure(srcUnit: String): Conversions {
    return when (srcUnit) {
        "m", "meter", "meters" -> Conversions.METER
        "km", "kilometer", "kilometers" -> Conversions.KILOMETER
        "cm", "centimeter", "centimeters" -> Conversions.CENTIMETER
        "mm", "millimeter", "millimeters" -> Conversions.MILLIMETER
        "mi", "mile", "miles" -> Conversions.MILE
        "yd", "yard", "yards" -> Conversions.YARD
        "ft", "foot", "feet" -> Conversions.FOOT
        "in", "inch", "inches" -> Conversions.INCH
        "g", "gram", "grams" -> Conversions.GRAM
        "kg", "kilogram", "kilograms" -> Conversions.KILOGRAM
        "mg", "milligram", "milligrams" -> Conversions.MILLIGRAM
        "lb", "pound", "pounds" -> Conversions.POUND
        "oz", "ounce", "ounces" -> Conversions.OUNCE
        "celsius", "dc", "c" -> Conversions.CELSIUS
        "fahrenheit", "df", "f" -> Conversions.FAHRENHEIT
        "kelvin", "kelvins", "k" -> Conversions.KELVIN
        else -> {
            return Conversions.UNKNOWN
        }
    }
}

fun isValidConversion(srcMeasure: Conversions, targetMeasure: Conversions): Boolean {
    val srcType = srcMeasure.getType()
    val targetType = targetMeasure.getType()
    return (srcType == targetType) && srcType != "???"
}

fun getUnit(value: Double, measureFactor:Conversions): String{
    return when(measureFactor){
        Conversions.FOOT -> { if (value != 1.0) "feet" else "foot"}
        Conversions.INCH -> { if (value != 1.0) "inches" else "inch"}
        Conversions.CELSIUS -> { if (value != 1.0) "degrees celsius" else "degree celsius"}
        Conversions.FAHRENHEIT -> { if (value != 1.0) "degrees fahrenheit" else "degree fahrenheit"}
        else -> {if (value != 1.0) "${measureFactor.toString().lowercase()}s" else measureFactor.toString().lowercase()
        }
    }
}


fun handleTemperatureCases(srcValue: Double, srcUnit: Conversions, targetUnit: Conversions): Double{
//    println("${srcUnit}_TO_${targetUnit}")
    return when("${srcUnit}_TO_${targetUnit}"){
        "CELSIUS_TO_FAHRENHEIT" -> (srcValue * 9/5) + 32
        "FAHRENHEIT_TO_CELSIUS" -> (srcValue - 32) * 5/9
        "KELVIN_TO_CELSIUS" -> srcValue - 273.15
        "CELSIUS_TO_KELVIN" -> srcValue + 273.15
        "FAHRENHEIT_TO_KELVIN" ->  (srcValue - 32) * 5/9 + 273.15
        "KELVIN_TO_FAHRENHEIT" -> (srcValue - 273.15) * 9/5 + 32
        else -> srcValue
    }
}

fun main() {
    while (true) {
        println("Enter what you want to convert (or exit):")
        val command = readln().split(" ").map { it.lowercase() }
        if (command[0] == "exit") return
        var (srcValStr, srcUnit, targetUnit) = listOf(command.first(), command[1], command.last())
        if (srcUnit.contains("degree"))
            srcUnit = command[2]
        val srcMeasure = getMeasure(srcUnit)
        val targetMeasure = getMeasure(targetUnit)
        if (isValidConversion(srcMeasure, targetMeasure)) {
            val srcValue = srcValStr.toDouble()
            if(srcValue < 0 && srcMeasure.getType() != "temperature"){
                val unitType = srcMeasure.getType()[0].uppercase() + srcMeasure.getType().substring(1)
                println("$unitType shouldn't be negative")
                continue
            }
            val convertedValue = if (srcMeasure.getType() == "temperature"){
                handleTemperatureCases(srcValue ,srcMeasure, targetMeasure)
            } else {
                (srcValue * srcMeasure.getConversionValue()) / targetMeasure.getConversionValue()
            }
            val src = getUnit(srcValue, srcMeasure)
            val target = getUnit(convertedValue, targetMeasure)
            println("$srcValue $src is $convertedValue $target")
        } else {
            val src = if(srcMeasure.getType() != "???") getUnit(2.0, srcMeasure) else "???"
            val target = if(targetMeasure.getType() != "???") getUnit(2.0, targetMeasure) else "???"
            val srcValNull = srcValStr.toDoubleOrNull()
            if (src == target && srcValNull == null)
                println("Parse error")
            else
                println("Conversion from $src to $target is impossible")
        }
    }
}

