package me.ihdeveloper.humans.core

/**
 * Changes the value of private field in the object
 */
fun setPrivateField(obj: Any, name: String, value: Any) {
    val field = obj.javaClass.getDeclaredField(name)
    field.isAccessible = true
    field.set(obj, value)
}
