package me.ihdeveloper.humans.core

fun setPrivateField(obj: Any, name: String, value: Any) {
    val field = obj.javaClass.getDeclaredField(name)
    field.isAccessible = true
    field.set(obj, value)
}
