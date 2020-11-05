package me.ihdeveloper.humans.core

import kotlin.reflect.KClass

/**
 * Changes the value of private field in the object
 */
fun setPrivateField(obj: Any, name: String, value: Any) = setPrivateField(obj::class, obj, name, value)
fun setPrivateField(kClass: KClass<*>, obj: Any, name: String, value: Any) {
    val field = kClass.java.getDeclaredField(name)
    field.isAccessible = true
    field.set(obj, value)
}

/**
 * Reads the value of a declared field in the object
 */
fun <T> getPrivateField(kClass: KClass<*>, obj: Any, name: String): T {
    val field = kClass.java.getDeclaredField(name)
    field.isAccessible = true

    @Suppress("UNCHECKED_CAST")
    return field.get(obj) as T
}
