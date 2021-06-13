package me.ihdeveloper.humans.service.protocol

enum class PacketResponseStatus(
    val code: Int
) {
    OK(200),
    INTERNAL_ERROR(500)
}