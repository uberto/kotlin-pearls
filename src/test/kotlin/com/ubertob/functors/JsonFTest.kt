package com.ubertob.functors


import com.ubertob.outcome.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class JsonFTest {

    @Test
    fun `JsonNode String`() {

        val expected = "abc"
        val json = JString.pure(expected)

        val actual = JString.extract(json).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }


    @Test
    fun `Json Double`() {

        val expected = 123.0
        val json = JDouble.pure(expected)

        val actual = JDouble.extract(json).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Json Int`() {

        val expected = 124
        val json = JInt.pure(expected)

        val actual = JInt.extract(json).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Json Customer and back`() {

        val expected = Customer(123, "abc")
        val json = JCustomer.pure(expected)

        val actual = JCustomer.extract(json).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }


    @Test
    fun `json array of Customers`() {

        val jsonUserArray = JArray(JCustomer)

        val expected = listOf(
            Customer(1, "Adam"),
            Customer(2, "Bob"),
            Customer(3, "Carol")
        )

        val node = jsonUserArray.pure(expected)

        val actual = jsonUserArray.extract(node).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Json with nullable and back`() {

        val toothpaste = Product(1001, "toothpaste \"whiter than white\"", 12.34)
        val offer = Product(10001, "special offer", null)
        val toothpasteJson = JProduct.pure(toothpaste)
        val offerJson = JProduct.pure(offer)

        val actualToothpaste = JProduct.extract(toothpasteJson).shouldSucceed()
        val actualOffer = JProduct.extract(offerJson).shouldSucceed()

        expect {
            that(actualToothpaste).isEqualTo(toothpaste)
            that(actualOffer).isEqualTo(offer)
        }
    }

    val ann = Customer(1, "ann")

    @Test
    fun `Json with objects inside and back`() {

        val json = JInvoice.pure(invoice)

        val actual = JInvoice.extract(json).shouldSucceed()

        expectThat(actual).isEqualTo(invoice)
    }


    @Test
    fun `JsonString Customer and back`() {

        val expected = Customer(123, "abc")
        val json = toJsonString(expected, JCustomer).shouldSucceed()

        println(json)

        val actual = fromJsonString(json, JCustomer).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }

    val toothpaste = Product(1001, "toothpaste \"whiter than white\"", 12.34)
    val offer = Product(10001, "special offer", null)

    @Test
    fun `JsonString Product and back`() {


        val jsonToothpaste = toJsonString(toothpaste, JProduct).shouldSucceed()
        val jsonOffer = toJsonString(offer, JProduct).shouldSucceed()

        println(jsonToothpaste)
        println(jsonOffer)

        val actualToothpaste = fromJsonString(jsonToothpaste, JProduct).shouldSucceed()
        val actualOffer = fromJsonString(jsonOffer, JProduct).shouldSucceed()

        expectThat(actualToothpaste).isEqualTo(toothpaste)
        expectThat(actualOffer).isEqualTo(offer)
    }

    val invoice = Invoice(InvoiceId("1001"), true, ann, listOf(toothpaste, offer), 123.45)


    @Test
    fun `JsonString Invoice and back`() {

        val json = toJsonString(invoice, JInvoice).shouldSucceed()

        println(json)

        val actual = fromJsonString(json, JInvoice).shouldSucceed()

        expectThat(actual).isEqualTo(invoice)
    }

    @Test
    fun `parsing illegal json gives us precise errors`() {
        val illegalJson =
            "{\"id\":1001,\"vat-to-pay\":true,\"customer\":{\"id\":1,\"name\":\"ann\"},\"items\":[{\"id\":1001,\"desc\":\"toothpaste \\\"whiter than white\\\"\",\"price:12.34},{\"id\":10001,\"desc\":\"special offer\"}],\"total\":123.45}"

        val error = fromJsonString(illegalJson, JInvoice).shouldFail()

        expectThat(error.msg).isEqualTo("error at parsing - Unexpected character at position 140: 'i' (ASCII: 105)'")
    }

    @Test
    fun `parsing wrong json gives us precise errors`() {
        val jsonWithDifferentField =
            "{\"id\":\"1001\",\"vat-to-pay\":true,\"customer\":{\"id\":1,\"name\":\"ann\"},\"items\":[{\"id\":1001,\"desc\":\"toothpaste \\\"whiter than white\\\"\",\"price\":125},{\"id\":10001,\"desc\":\"special offer\"}],\"total\":123.45}"

        val error = fromJsonString(jsonWithDifferentField, JInvoice).shouldFail()

        expectThat(error.msg).isEqualTo("error at </items/0/price> - Expected Double but found JsonNodeInt(num=125, path=[items, 0, price])")
    }

}

data class Customer(val id: Int, val name: String)

object JCustomer : JAny<Customer> {

    val id by JField(JInt)
    val name by JField(JString)

    override fun JsonNodeObject.deserialize(): Outcome<JsonError, Customer> =
        liftA2(::Customer, id.get(), name.get())

    override fun serialize(value: Customer) = foldObjNode(
        id.setter(value.id),
        name.setter(value.name)
    )
}


data class Product(val id: Int, val desc: String, val price: Double?)

object JProduct : JAny<Product> {
    val id by JField(JInt)
    val desc by JField(JString)
    val price by JFieldOptional(JDouble)


    override fun JsonNodeObject.deserialize(): Outcome<JsonError, Product> =
        liftA3(::Product, id.get(), desc.get(), price.get())

    override fun serialize(value: Product): JsonNodeObject =
        foldObjNode(
            id.setter(value.id),
            desc.setter(value.desc),
            price.setter(value.price)
        )

}


data class InvoiceId(override val raw: String) : StringWrapper


data class Invoice(
    val id: InvoiceId,
    val vat: Boolean,
    val customer: Customer,
    val items: List<Product>,
    val total: Double
)

object JInvoice : JAny<Invoice> {
    val id by JField(JStringWrapper(::InvoiceId))
    val vat = JsonPropMandatory("vat-to-pay", JBoolean)
    val customer by JField(JCustomer)
    val items by JField(JArray(JProduct))
    val total by JField(JDouble)


    override fun JsonNodeObject.deserialize(): Outcome<JsonError, Invoice> =
        liftA5(::Invoice, id.get(), vat.get(), customer.get(), items.get(), total.get())

    override fun serialize(value: Invoice): JsonNodeObject = foldObjNode(
        id.setter(value.id),
        vat.setter(value.vat),
        customer.setter(value.customer),
        items.setter(value.items),
        total.setter(value.total)
    )
}


fun <T : Any> Outcome<*, T>.shouldSucceed(): T =
    this.fold({ fail(it.msg) }, { it })

fun <E : OutcomeError> Outcome<E, *>.shouldFail(): E =
    this.fold({ it }, { fail("Should have failed but was $it") })
