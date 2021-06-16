package me.ihdeveloper.humans.core.util

import com.mojang.authlib.GameProfile
import java.lang.reflect.Field
import org.bukkit.inventory.meta.SkullMeta

private val class_SkullMeta = Class.forName("org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaSkull")
private val field_SkullMeta_Profile: Field = class_SkullMeta.getDeclaredField("profile").apply {
    isAccessible = true
}
fun SkullMeta.setGameProfile(profile: GameProfile) {
    field_SkullMeta_Profile.set(this, profile)
}
