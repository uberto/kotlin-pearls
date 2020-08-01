package com.ubertob.functors


import com.ubertob.outcome.Outcome
import com.ubertob.outcome.liftA2
import com.ubertob.outcome.liftA3
import com.ubertob.outcome.liftA5
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class JsonFTest {

    @Test
    fun `JsonNode String`() {

        val expected = "abc"
        val json = JsonString.toJson(expected)

        val actual = JsonString.from(json).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }


    @Test
    fun `Json Double`() {

        val expected = 123.0
        val json = JsonDouble.toJson(expected)

        val actual = JsonDouble.from(json).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Json Int`() {

        val expected = 124
        val json = JsonInt.toJson(expected)

        val actual = JsonInt.from(json).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Json Customer and back`() {

        val expected = Customer(123, "abc")
        val json = JsonCustomer.toJson(expected)

        val actual = JsonCustomer.from(json).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }


    @Test
    fun `json array of Customers`() {

        val jsonUserArray = JsonArrayNode(JsonCustomer)

        val expected = listOf(
            Customer(1, "Adam"),
            Customer(2, "Bob"),
            Customer(3, "Carol")
        )

        val node = jsonUserArray.toJson(expected)

        val actual = jsonUserArray.from(node).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Json with nullable and back`() {

        val toothpaste = Product(1001, "toothpast \"whiter than white\"", 12.34)
        val offer = Product(10001, "special offer", null)
        val toothpasteJson = JsonProduct.toJson(toothpaste)
        val offerJson = JsonProduct.toJson(offer)

        val actualToothpaste = JsonProduct.from(toothpasteJson).shouldSucceed()
        val actualOffer = JsonProduct.from(offerJson).shouldSucceed()

        expect {
            that(actualToothpaste).isEqualTo(toothpaste)
            that(actualOffer).isEqualTo(offer)
        }
    }


    @Test
    fun `Json with objects inside and back`() {

        val ann = Customer(1, "ann")
        val expected = Invoice(1001, true, ann, listOf("a", "b", "c"), 123.45)
        val json = JsonInvoice.toJson(expected)

        val actual = JsonInvoice.from(json).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }


    @Test
    fun `JsonString Customer and back`() {

        val expected = Customer(123, "abc")
        val json = toJsonString(expected, JsonCustomer).shouldSucceed()

        println(json)

        val actual = fromJsonString(json, JsonCustomer).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }

    @Test
    fun `JsonString Product and back`() {

        val toothpaste = Product(1001, "toothpast \"whiter than white\"", 12.34)
        val offer = Product(10001, "special offer", null)

        val jsonToothpaste = toJsonString(toothpaste, JsonProduct).shouldSucceed()
        val jsonOffer = toJsonString(offer, JsonProduct).shouldSucceed()

        println(jsonToothpaste)
        println(jsonOffer)

        val actualToothpaste = fromJsonString(jsonToothpaste, JsonProduct).shouldSucceed()
        val actualOffer = fromJsonString(jsonOffer, JsonProduct).shouldSucceed()

        expectThat(actualToothpaste).isEqualTo(toothpaste)
        expectThat(actualOffer).isEqualTo(offer)
    }

    @Test
    fun `JsonString Invoice and back`() {



        val ann = Customer(1, "ann")
        val expected = Invoice(1001, true, ann, listOf("a", "b", "c"), 123.45)
        val json = toJsonString(expected, JsonInvoice).shouldSucceed()

        println(json)

        val actual = fromJsonString(json, JsonInvoice).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }

}

data class Customer(val id: Int, val name: String)

object JsonCustomer : JsonObj<Customer> {

    val id by JField(JsonInt)
    val name by JField(JsonString)

    override fun JsonNodeObject.deserialize(): Outcome<JsonError, Customer> =
        liftA2(::Customer, id.get(), name.get())

    override fun serialize(value: Customer) = writeObjNode(
        id.setTo(value.id),
        name.setTo(value.name)
    )
}

data class Invoice(val id: Int, val vat: Boolean, val customer: Customer, val items: List<String>, val total: Double)

object JsonInvoice : JsonObj<Invoice> {
    val id by JField(JsonInt)
    val vat = JsonProp("vat-to-pay", JsonBoolean)
    val customer by JField(JsonCustomer)
    val items by JField(JsonArrayNode(JsonString))
    val total by JField(JsonDouble)


    override fun JsonNodeObject.deserialize(): Outcome<JsonError, Invoice> =
        liftA5(::Invoice, id.get(), vat.get(), customer.get(), items.get(), total.get())

    override fun serialize(value: Invoice): JsonNodeObject = writeObjNode(
        id.setTo(value.id),
        vat.setTo(value.vat),
        customer.setTo(value.customer),
        items.setTo(value.items),
        total.setTo(value.total)
    )
}

data class Product(val id: Int, val desc: String, val price: Double?)

object JsonProduct : JsonObj<Product> {
    val id by JField(JsonInt)
    val desc by JField(JsonString)
    val price by JFieldOptional(JsonDouble)


    override fun JsonNodeObject.deserialize(): Outcome<JsonError, Product> =
        liftA3(::Product, id.get(), desc.get(), price.get())

    override fun serialize(value: Product): JsonNodeObject =
        writeObjNode(
            id.setTo(value.id),
            desc.setTo(value.desc),
            price.setTo(value.price)
        )

}


//todo:
// checking parsing error with the position (add parent and path)
// JProp<T?> would work instead of JPropOp?


fun <T : Any> Outcome<*, T>.shouldSucceed(): T =
    this.fold({ fail(it.msg) }, { it })
