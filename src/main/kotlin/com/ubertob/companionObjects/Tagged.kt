package com.ubertob.companionObjects


//from
//https://gist.github.com/avwie/4c4e4abd68c2b092e374ba8b4af26b3a

interface Tag<T>

interface Tagged<T> {
    val tag: Tag<T>
}

class ComponentBag {
    private val components = mutableMapOf<Tag<*>, Any>()

    fun <T : Tagged<T>> set(component: T) {
        components[component.tag] = component
    }

    @Suppress("UNCHECKED_CAST") // but we are bloody sure the tag is correct
    operator fun <T> get(tag: Tag<T>): T? = components[tag] as? T
}

data class Name(val value: String): Tagged<Name> {
    override val tag: Tag<Name> = Name // <- this is the companion object
    companion object : Tag<Name>
}

data class Age(val value: Int): Tagged<Age> {
    override val tag: Tag<Age> = Age
    companion object : Tag<Age>
}

data class Health(val value: Int): Tagged<Health> {
    override val tag: Tag<Health> = Health
    companion object : Tag<Health>
}

fun main() {
    val player = ComponentBag()
    player.set(Name("Beeblebrox"))
    player.set(Age(42))
    player.set(Health(1000))

    // typesafe fetch
    val age = player[Age] // this is automatically if type Age?
    val health = player[Health] // this is automatically of type Health?
    require(age?.value == 42)
    require(health?.value == 1000)
}