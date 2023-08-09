import kotlin.math.abs

enum class Actions(private val action: String) {
    BUY("buy"),
    FILL("fill"),
    TAKE("take"),
    REMAINING("remaining"),
    EXIT("exit"),
    BACK("back");
    fun getAction(): String {
        return this.action
    }
}

enum class CoffeeTypes(
    val coffeeType: String,
    private val water: UInt,
    private val milk: UInt,
    private val coffeeBeans: UInt,
    private val cost: UInt
) {
    ESPRESSO("espresso", 250u, 0u, 16u, 4u),
    LATTE("latte", 350u, 75u, 20u, 7u),
    CAPPUCCINO("cappuccino", 200u, 100u, 12u, 6u);

    fun getCoffeeInfo(): List<UInt> {
        return listOf(this.water, this.milk, this.coffeeBeans, this.cost)
    }
}

class CoffeeMachine {
    private var cups: UInt = 0u
    private var water: UInt = 0u
    private var milk: UInt = 0u
    private var coffeeBeans: UInt = 0u
    private var money: UInt = 0u

    constructor(water: UInt, milk: UInt, coffeeBeans: UInt, cups: UInt, money: UInt) {
        this.water = water
        this.milk = milk
        this.coffeeBeans = coffeeBeans
        this.cups = cups
        this.money = money
    }

    fun info(): Unit {
        println(
            """
            The coffee machine has:
            $water ml of water
            $milk ml of milk
            $coffeeBeans g of coffee beans
            $cups disposable cups
            ${'$'}$money of money
        """.trimIndent()
        )
    }

    private fun canMakeCoffee(coffeeIngredients: List<UInt>): Boolean {
        val water = coffeeIngredients[0]
        val milk = coffeeIngredients[1]
        val coffeeBeans = coffeeIngredients[2]
        var flag = 0
        when {
            this.water <= water -> {println("Sorry, not enough water!"); flag = 1}
            this.milk <= milk -> {println("Sorry, not enough milk!"); flag = 1}
            this.coffeeBeans <= coffeeBeans -> {println("Sorry, not enough coffee beans!"); flag = 1}
            this.cups < 1u -> {println("Sorry, not enough cups!"); flag = 1}
        }
        return flag == 0
    }

    private fun getCoffeeDetails(choice : UInt): List<UInt>? {
        for (coffeeType in CoffeeTypes.values()) {
            if ((coffeeType.ordinal + 1).toUInt() == choice)
            {
                return coffeeType.getCoffeeInfo()
            }
        }
        return null
    }

    private fun buyCoffee() {
        var options = ""
        for (coffeeType in CoffeeTypes.values()) {
            options += "${coffeeType.ordinal + 1} - ${coffeeType.coffeeType}, "
        }

        println("What do you want to buy? ${options}back - to main menu:")
        val choice = readln()
        if (choice == Actions.BACK.getAction()) return

        val details = this.getCoffeeDetails(choice.toUInt())
        if (!details.isNullOrEmpty() && canMakeCoffee(details)){
            println("I have enough resources, making you a coffee!")
            this.water -= details[0]
            this.milk -= details[1]
            this.coffeeBeans -= details[2]
            this.money += details[3]
            this.cups -= 1u
        }
    }

    private fun fillCoffee(): Unit{
        println("Write how many ml of water you want to add:")
        val waterFilled = readln().toUInt()
        this.water += waterFilled

        println("Write how many ml of milk you want to add:")
        val milkFilled = readln().toUInt()
        this.milk += milkFilled

        println("Write how many grams of coffee beans you want to add:")
        val coffeeBeansFilled = readln().toUInt()
        this.coffeeBeans += coffeeBeansFilled

        println("Write how many disposable cups you want to add:")
        val cupsFilled = readln().toUInt()
        this.cups += cupsFilled

    }

    private fun takeMoney(): Unit {
        println("I gave you $$money")
        this.money = 0u
    }

    fun displayUI() {
        while (true) {
            println("Write action (buy, fill, take, remaining, exit):")
            when (readln().lowercase()) {
                Actions.BUY.getAction() -> buyCoffee()
                Actions.FILL.getAction() -> fillCoffee()
                Actions.TAKE.getAction() -> takeMoney()
                Actions.REMAINING.getAction() -> this.info()
                Actions.EXIT.getAction() -> break
                else -> println("Invalid Action!")
            }
        }
    }
}

fun main() {
    val hyperskillCM = CoffeeMachine(400u, 540u, 120u, 9u, 550u)
    hyperskillCM.displayUI()
}