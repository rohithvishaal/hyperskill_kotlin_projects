import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

enum class ContactType(private val type: String) {
    PERSON("person"),
    ORG("organization");

    fun get() = type
}


open class SuperContact(
    var name: String,
    var phoneNumber: String,
    var timeCreated: String,
    var timeLastEdited: String
) {
    open lateinit var address: String
    open lateinit var birthDate: String
    open lateinit var gender: String
    open lateinit var surname: String

    open fun info(): String{
        return "override this info function"
    }



    companion object {
        private fun isValidPhoneNumber(phoneNumber: String): Boolean {
            val phoneRegex =
                "^\\+?([\\da-zA-Z]+[\\s-]?)?(\\([\\da-zA-Z]{2,}(\\)[\\s-]|\\)$))?([\\da-zA-Z]{2,}[\\s-]?)*([\\da-zA-Z]{2,})?$".toRegex()
            return phoneRegex.matches(phoneNumber)
        }

        fun getValidPhoneNumber(phoneNumber: String): String {
            return if (isValidPhoneNumber(phoneNumber)) phoneNumber
            else {
                println("Wrong number format!")
                "[no number]"
            }
        }

        fun getCurrentTimeStamp(): String {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            return currentDateTime.format(formatter)
        }
    }
}

class Contact(
    name: String,
    override var surname: String,
    override var birthDate: String,
    override var gender: String,
    phoneNumber: String,
    timeCreated: String,
    timeLastEdited: String
) : SuperContact(name, phoneNumber, timeCreated, timeLastEdited) {
    companion object {
        fun getValidPhoneNumber(phoneNumber: String) = SuperContact.getValidPhoneNumber(phoneNumber)
        fun getValidDOB(dateStr: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            dateFormat.isLenient = false

            try {
                val date = dateFormat.parse(dateStr)
                val cal = Calendar.getInstance()
                cal.time = date

                val today = Calendar.getInstance()
                today.time = Date()

                // Check if the date is not in the future and the year is within a reasonable range
                if (cal.after(today) || cal.get(Calendar.YEAR) < 1900 || cal.get(Calendar.YEAR) > today.get(Calendar.YEAR)) {
                    println("Bad birth date!")
                    return "[no data]"
                }
            } catch (e: Exception) {
                println("Bad birth date!")
                return "[no data]"
            }

            return dateStr
        }

        fun getValidGender(gender: String): String {
            val acceptedGenders = listOf("M", "F")
            return if (gender in acceptedGenders) gender
            else {
                println("Bad gender!")
                "[no data]"
            }
        }

        fun getCurrentTimeStamp() = SuperContact.getCurrentTimeStamp()
    }

    override fun info(): String {
        return """
            Name: $name
            Surname: $surname
            Birth date: $birthDate
            Gender: $gender
            Number: $phoneNumber
            Time created: $timeCreated
            Time last edit: $timeLastEdited
        """.trimIndent()
    }
}

class OrganizationContact(
    name: String,
    override var address: String,
    phoneNumber: String,
    timeCreated: String,
    timeLastEdited: String
) : SuperContact(name, phoneNumber, timeCreated, timeLastEdited) {
    companion object {
        fun getCurrentTimeStamp() = SuperContact.getCurrentTimeStamp()
        fun getValidPhoneNumber(phoneNumber: String) = SuperContact.getValidPhoneNumber(phoneNumber)
    }
    override fun info(): String {
        return """
            Organization name: $name
            Address: $address
            Number: $phoneNumber
            Time created: $timeCreated
            Time last edit: $timeLastEdited
        """.trimIndent()
    }
}

class PhoneBook {
    private val phoneBook: MutableList<SuperContact> = mutableListOf()
    private var contactCount = 0

    enum class Actions(private val action: String) {
        ADD("add"),
        REMOVE("remove"),
        EDIT("edit"),
        COUNT("count"),
        INFO("info");

        fun getAction() = action
    }

    fun add() {
        println("Enter the type (person, organization):")
        val type = readln()
        if (type == ContactType.PERSON.get()) {
            println("Enter the name:")
            val name: String = readln()
            println("Enter the surname:")
            val surname: String = readln()
            println("Enter the birth date:")
            val birthDate: String = readln()
            println("Gender:")
            val gender: String = readln()
            println("Enter the number:")
            val phoneNumber: String = readln()
            val timeCreated: String = Contact.getCurrentTimeStamp()
            val newContact = Contact(
                name,
                surname,
                Contact.getValidDOB(birthDate),
                Contact.getValidGender(gender),
                Contact.getValidPhoneNumber(phoneNumber),
                timeCreated,
                timeCreated
            )
            phoneBook.add(newContact)
        } else if (type == ContactType.ORG.get()) {
            println("Enter the organization name:")
            val orgName: String = readln()
            println("Enter the address:")
            val orgAddress = readln()
            println("Enter the number:")
            val orgPhoneNumber = OrganizationContact.getValidPhoneNumber(readln())
            val orgContact = OrganizationContact(
                orgName,
                orgAddress,
                orgPhoneNumber,
                timeCreated = OrganizationContact.getCurrentTimeStamp(),
                timeLastEdited = OrganizationContact.getCurrentTimeStamp()
            )
            phoneBook.add(orgContact)
        }
        if (phoneBook.size >= 1) {
            println("The record added.")
            contactCount += 1
        }
    }

    fun info() {
        if (contactCount != 0) {
            list()
            val contactIndex = readln().toInt()
            val selectedContact = phoneBook[contactIndex - 1]
            println(selectedContact.info())
        } else
            println("No records to list!")
    }

    private fun list(){
        if (contactCount != 0) {
            phoneBook.forEachIndexed { index, it -> println("${index + 1}. ${it.name}") }
        }
    }

    fun remove() {
        if (contactCount == 0) {
            println("No Records to remove!"); return
        }
        list()
        println("Select a record:")
        val recordSelected = readln().toIntOrNull()?.minus(1)
        if (recordSelected!! <= contactCount) {
            phoneBook.removeAt(recordSelected)
            println("The record removed!")
            contactCount -= 1
        }

    }

    fun edit() {
        if (contactCount == 0) {
            println("No Records to edit!"); return
        }
        list()
        println("Select a record:")
        val recordSelected = readln().toIntOrNull()?.minus(1)
        if (recordSelected!! <= contactCount) {
            val className = phoneBook[recordSelected]::class.qualifiedName
            println(className)
            if (className == "Contact") {
                println("Select a field (name, surname, birth, gender, number):")
                val field = readln()
                println("Enter $field")
                when (field) {
                    "name" -> phoneBook[recordSelected].name = readln()
                    "surname" -> phoneBook[recordSelected].surname = readln()
                    "number" -> phoneBook[recordSelected].phoneNumber = Contact.getValidPhoneNumber(readln())
                    "gender" -> phoneBook[recordSelected].gender = Contact.getValidGender(readln())
                    "birth" -> phoneBook[recordSelected].birthDate = Contact.getValidDOB(readln())
                }
                phoneBook[recordSelected].timeLastEdited = Contact.getCurrentTimeStamp()
                println("The record updated!")
            }
            else if (className == "OrganizationContact"){
                println("Select a field (name, address, number):")
                val field = readln()
                println("Enter $field")
                when (field) {
                    "name" -> phoneBook[recordSelected].name = readln()
                    "address" -> phoneBook[recordSelected].address = readln()
                    "number" -> phoneBook[recordSelected].phoneNumber = OrganizationContact.getValidPhoneNumber(readln())
                }
                phoneBook[recordSelected].timeLastEdited = OrganizationContact.getCurrentTimeStamp()
                println("The record updated!")
            }
        }

    }

    fun count() {
        println("The Phone Book has $contactCount records.")
    }
}

fun displayUI() {
    val phoneBook = PhoneBook()
    while (true) {
        println("Enter action (add, remove, edit, count, info, exit):")
        when (readln()) {
            PhoneBook.Actions.ADD.getAction() -> phoneBook.add()
            PhoneBook.Actions.REMOVE.getAction() -> phoneBook.remove()
            PhoneBook.Actions.EDIT.getAction() -> phoneBook.edit()
            PhoneBook.Actions.COUNT.getAction() -> phoneBook.count()
            PhoneBook.Actions.INFO.getAction() -> {
                println("Enter index to show info:");phoneBook.info()
            }

            "exit" -> return
        }
        println("")
    }
}

fun main() = displayUI()