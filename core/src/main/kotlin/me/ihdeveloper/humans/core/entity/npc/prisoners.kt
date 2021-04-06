package me.ihdeveloper.humans.core.entity.npc

import me.ihdeveloper.humans.core.entity.Prisoner
import me.ihdeveloper.humans.core.util.randomGameProfile
import me.ihdeveloper.humans.core.util.applyTexture
import org.bukkit.Location

enum class PrisonerType {
    SATTAM,
    IDHOOM,
    ALMOND,
    BRHOM,
    ALI;
}

// private const val PROFILE_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYwNTU1MDg4NDA4MiwKICAicHJvZmlsZUlkIiA6ICIxZDUyMzNkMzg4NjI0YmFmYjAwZTMxNTBhN2FhM2E4OSIsCiAgInByb2ZpbGVOYW1lIiA6ICIwMDAwMDAwMDAwMDAwMDBKIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzY4ZmYxNDMxNzgyZDliNzI1M2NkNTg5NmVjMGIwZDg5MmNjZWM4N2QyMDdiZmQzOTUwNTJjZTkxNTI1NDNjZmUiCiAgICB9CiAgfQp9"
// private const val PROFILE_SIGNATURE = "SC1ss2bP9UGxqtZjk4p2VWgdNpU58/GX8wU+F6XHTQYQYlrJmRjwuRZJhnLETcHn0YX3trKuPrEbVgpuvQ/1O0CQwJ6fQIIoZ++kZOvJOPKwMRbSgLRGYeZpb0kh3xrHfj64OnJOqXw1rs+deei/0Ekwi+8ohsLprpE2XgDw0rM4ZZ3k/yR7S9lv3SS7/dQwWPY0LgvkdfX3FFEg3K5GAUXexZ3HA8O7vsBt9XhBmVX6xDky2PS1UThp0H+86Bt/mVx+YHJvxuev986M3kE8yR1FYzI8rhE78F3SbQ0duTEBMsnpVCTjLVvRC3DnTNsuboaBcK6KSqTLG3JbxyiebfFsimMQ1fIW0+WIJ2BMTYn7mCczToKY74E+dP4l/7ew65Sviu02FXTF86FGkZhqDEz1DBGKeNQaqIFiH5LHTELSQh9y3Qea0C4U8CDAoiUMmZzssd8p0SJLq3ODFx909fIqpNmqi52c60h5fsaW0B9CF6RaVgKe4Utlp4HnG0D/96K8JVF2gw3xKOAppujjhKXB5YfIiymJKWC9RfLgqHqdUxsKbgCfPccoaIdp+fPpSDIkZZ/OjOQnPCtZxHKhs1t8H1nrtyAyYH0bVLHtzu3h5aSnaSUzQp1A3UDhq/6lLR5YBcafgY7sh06vM4/L0B2lB5mimEa8k6yDGGiMMyw="

private const val PROFILE_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYwODU4Mzg3Nzg5NSwKICAicHJvZmlsZUlkIiA6ICJmMTA0NzMxZjljYTU0NmI0OTkzNjM4NTlkZWY5N2NjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJ6aWFkODciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhmZjE0MzE3ODJkOWI3MjUzY2Q1ODk2ZWMwYjBkODkyY2NlYzg3ZDIwN2JmZDM5NTA1MmNlOTE1MjU0M2NmZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"

private const val PROFILE_SIGNATURE = "FLyBKi5r4XiOVN4t7eIvsKTofh03+7ooxKd7q9RMN5geFzz09dMOWRXbSazUXLlPyiKgpfzwo5/gAIV1RH0XZxp/4cK0wpESnrVN0gFTlK3up5RnPZKMRk2PNCfeYDOSbqtr34w1ahinmvmSe6KP2dvWmCyPW4/3XYqhaAF4S1GnwvpO7vGTwR876agsANUqpcySN8Su8LeoU2Ow9RQnWcqFwCDleZUKuK+SlESVTrmsKoOOd8W9IWxcc/eRfCV7Q/fj3YRHth7oiH1bRkuYASyb4BmYyjcfnjFQPM4SaBUvt4qy/TmDA9xWTLSmsGb09hBjmpH/DYaHV4aIMxzrVzC2Jk6Pq65+62QMax7NGi/vEfj5An1F7REVguIRccZePMAKj4wkF8YmIQpJl/O8S6CtxNI4fCIjcmycRmFnzFe8Ca7nhuhwI0UY5DNmR2IDqm6CotnFYhcmNXI6y6zK5jzIQQgx8oJA1PCDyK1VDuYyDGMKYDrpfCSgAWm9EeJPS3CDaPtfEaQjnAqAEa92RaW9iZJcrms+6hSXa23DRCgfC+FnFYWR+stqQnhyuN+rKc8qBw+XboJ6zgCAqrfVEz/E4VweqBi8hRrdBt0nzf2uSXXxVUDa471GpEIJYxuYn4oPXd0YY43ILd2JHJ/CGt6LQwhJxKDytHkLZkF4I4s="

fun newPrisoner(location: Location, type: PrisonerType): Prisoner = when(type) {
    PrisonerType.SATTAM -> Prisoner(
        "Sattam",
        "S6aam",
        arrayOf(
            "§7[NPC] §eSattam: §7When the §cWither King§7 opened the stone mine the first mine in this prison",
            "§7[NPC] §eSattam: §7This pickaxe was the one that he used to break the first block in the mine",
        ),
        location,
        randomGameProfile().apply { applyTexture(PROFILE_TEXTURE, PROFILE_SIGNATURE) }
    )
    PrisonerType.IDHOOM -> Prisoner(
        "Dhoom",
        "iDhoom",
        arrayOf(
            "§7[NPC] §eDhoom: §7The challenge is to survive in this prison as \"Human\"",
            "§7[NPC] §eDhoom: §7The §cWither Soldier§7 is going to lead the prison",
            "§7[NPC] §eDhoom: §7His only goal is to make your life as \"Human\" miserable in this prison!"
        ),
        location,
        randomGameProfile().apply { applyTexture(PROFILE_TEXTURE, PROFILE_SIGNATURE) }
    )
    PrisonerType.ALMOND -> Prisoner(
        "Almond",
        "AlmondMilky",
        arrayOf(
            "§7[NPC] §eAlmond: §7We are not ready yet...",
            "§7[NPC] §eAlmond: §cMy time will come soon..."
        ),
        location,
        randomGameProfile().apply { applyTexture(PROFILE_TEXTURE, PROFILE_SIGNATURE) }
    )
    PrisonerType.BRHOM -> Prisoner(
        "Brhom",
        "1Brhom",
        arrayOf(
            "§7[NPC] §eBrhom: §7Mining a stack of §ePrison Stone§7 in the §eStone Mine§7",
            "§7[NPC] §eBrhom: §7will give you a pass to access the §eCoal Mine§7!"
        ),
        location,
        randomGameProfile().apply { applyTexture(PROFILE_TEXTURE, PROFILE_SIGNATURE) }
    )
    PrisonerType.ALI -> Prisoner(
        "Ali",
        "7aS",
        arrayOf(
            "§7[NPC] §eAli: §7Funding projects in the hub will open opportunities",
            "§7[NPC] §eAli: §7It will help humans survive in this prison",
            "§7[NPC] §eAli: §7You can find projects to fund by finding NPC with type §ePrison Builder",
            "§7[NPC] §eAli: §7Each project has different materials depending on the type and design of the project",
        ),
        location,
        randomGameProfile().apply { applyTexture(PROFILE_TEXTURE, PROFILE_SIGNATURE) }
    )
}
