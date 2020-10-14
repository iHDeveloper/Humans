package me.ihdeveloper.humans.core.entity.npc

import me.ihdeveloper.humans.core.entity.Prisoner
import me.ihdeveloper.humans.core.util.applyTexture
import me.ihdeveloper.humans.core.util.randomGameProfile
import org.bukkit.Location

enum class PrisonerType {
    SATTAM;
}

private val PROFILE_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYwMjY1OTM4OTEzOSwKICAicHJvZmlsZUlkIiA6ICJkODY1NjliNzg1ODU0OGU3OTJlYmJjNDM2MGYxNjkwNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJPY2FuYW0iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM4MDA0MWQzZTVhNGI2NTVhY2NlNTA3YWZjYzU3ZTg5NjhhYzYzODk4ZjM1NTkzODVmOTYzNzdiZTA3ODg5NCIKICAgIH0KICB9Cn0="
private val PROFILE_SIGNATURE = "vJPIR6cgMql/843abc3h06L/4NG/DPG6+R4uPiQEDfPj1+1BqhjpdwUtL/DJe6jEjML2muycZhc89Dhp3UOqVPOhh3A3XBp9aQbm6NjGM32/fPvBsePvwVgdsUxRorV6ei69FXWWEVCaOMXtUkDKuiAVIHQJapTiE93o1V7zEv741KUsKgmT+4e2RZxAB6kZ44PmHRyLuauJMh9NuSTbmHrAFdxidHKe7sqP5CIV+/cMs28zDt2iXRWzTfbNzXGOGHs4oZTyuGMFLNYlDVnWBGgxXVsXRFLsipiBkLNMLTZE3mxvk8m5FJMPEw8NO3nhQZCq/nkDkjt95jwWwH3KZlJiUXPdQeaDKbk31soMDeR0OOSwBp5j/BpKD6bqUOJFaOV7WR6jPOdfg2U3S1R307EmvTJFaUHf68lvMikFWrfFhySjfhrgvoXIpQ9DuLGovi5vtilBo7I4JjWTkSVAExz2aauxARYTLR6229LFawJgC1aOlBX0WD5DLfUNwWGHxXMsXeEtix4pQvaeZjY3UXxAVtlgKCT6QNyT101qejBWswCdYAGT2Nlx+X0un7BnZC5zO03Wq/g2oko6dCb0TKUMTaqDiCZXaIM3pmHT9pzYHLfoVBVWxH2gZxNOtdua6t/l7br/vgBu+uFwUcZwUhNXqhgehrTaD+uiV+Gi6Z4="

fun newPrisoner(location: Location, type: PrisonerType): Prisoner = when(type) {
    PrisonerType.SATTAM -> Prisoner(
        "Sattam",
        "S6aam",
        arrayOf(
            "§7When the §cWither King§7 opened the stone mine.",
            "§7This pickaxe was the one that he used to break the first block in the mine",
        ),
        location,
        randomGameProfile().apply { applyTexture(PROFILE_TEXTURE, PROFILE_SIGNATURE) }
    )
}
