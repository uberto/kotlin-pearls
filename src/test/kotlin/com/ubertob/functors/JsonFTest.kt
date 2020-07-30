package com.ubertob.functors


import com.ubertob.outcome.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class JsonFTest {

    @Test
    fun `JsonString`() {

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

        val jsonUserArray = JsonArray(JsonCustomer)

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
    fun `Json with objects inside and back`() {

        val ann = Customer(1, "ann")
        val expected = Invoice(1001, true, ann, listOf("a", "b", "c"), 123.45)
        val json = JsonInvoice.toJson(expected)

        val actual = JsonInvoice.from(json).shouldSucceed()

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

        expect{
            that(actualToothpaste).isEqualTo(toothpaste)
            that(actualOffer).isEqualTo(offer)
        }
    }

}
data class Customer(val id: Int, val name: String)

object JsonCustomer : JsonF<Customer> {

    val id by JField(JsonInt)
    val name by JField(JsonString)

    override fun from(node: JsonNode): Outcome<JsonError, Customer> = node.asObject {
        liftA2(::Customer, id.get(), name.get())
    }

    override fun toJson(value: Customer): JsonNode = writeObjNode(
        id.setTo(value.id),
        name.setTo(value.name)
    )
}

data class Invoice(val id: Int, val vat: Boolean, val customer: Customer, val items: List<String>, val total: Double)

object JsonInvoice : JsonF<Invoice> {
    val id by JField(JsonInt)
    val vat by JField(JsonBoolean)
    val customer by JField(JsonCustomer)
    val items by JField(JsonArray(JsonString))
    val total by JField(JsonDouble)

    override fun from(node: JsonNode): Outcome<JsonError, Invoice> = node.asObject {
        liftA5(::Invoice, id.get(), vat.get(), customer.get(), items.get(), total.get())
    }


    override fun toJson(value: Invoice): JsonNode= writeObjNode(
        id.setTo(value.id),
        vat.setTo(value.vat),
        customer.setTo(value.customer),
        items.setTo(value.items),
        total.setTo(value.total)
    )
}

data class Product(val id: Int, val desc: String, val price: Double?)

object JsonProduct: JsonF<Product>{
    val id by JField(JsonInt)
    val desc by JField(JsonString)
    val price by JFieldOp(JsonDouble)

    override fun from(node: JsonNode): Outcome<JsonError, Product> =
        node.asObject {
            liftA3(::Product, id.get(), desc.get(), price.get())
        }

    override fun toJson(value: Product): JsonNode=
        writeObjNode(
            id.setTo(value.id),
            desc.setTo(value.desc),
            price.setTo(value.price)
        )

}



//todo:
// nullable fields
// checking parsing error with the position (add parent and path)
// integration with Klaxon




fun <T : Any> Outcome<*, T>.shouldSucceed(): T =
    this.fold({ fail(it.msg) }, { it })
