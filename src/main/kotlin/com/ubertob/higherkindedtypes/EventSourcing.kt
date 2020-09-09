package com.ubertob.higherkindedtypes


class Something<T> {
    fun <C: Collection<T>> map(f: (T) -> C, g: (C) -> C ): C = TODO()

}




interface Event

interface State<E: Event> {
    fun combine(event: E): State<E>
}

//fun <E: Event, S: State<E>> List<E>.fold(initialState: S): S = fold<E,S>(initialState){ acc, e -> acc.combine(e)}


interface Command<E: Event>


sealed class MyEvent: Event {}

sealed class MyState: State<MyEvent> {
    override fun combine(event: MyEvent): MyState = TODO()
}

object initialState : MyState() {
    override fun combine(event: MyEvent): MyState = TODO("not implemented")
}

fun List<MyEvent>.fold(): MyState = fold<MyEvent,MyState>(initialState){ acc, e -> acc.combine(e)}


sealed class MyCommand: Command<MyEvent> {}

abstract class CommandHandler <E: Event, C: Command<E>> (val eventStore: (String) -> List<E>) {
   abstract fun exec(c: C): List<E>

}

interface EventStore<E: Event>: (String) -> List<E>

class MyEventStore<E: Event>: EventStore<E> {
    override fun invoke(p1: String): List<E> = TODO("not implemented")

}

class MyCommandHandler: CommandHandler<MyEvent, MyCommand>(eventStore = MyEventStore()){
    override fun exec(c: MyCommand): List<MyEvent> {
        //when(c)
        val myId = "cmdId"
        val state: MyState = eventStore(myId).fold()

        return TODO("some MyEvents")
    }


}