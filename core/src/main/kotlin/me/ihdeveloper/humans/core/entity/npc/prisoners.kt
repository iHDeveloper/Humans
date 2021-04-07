package me.ihdeveloper.humans.core.entity.npc

import me.ihdeveloper.humans.core.entity.Prisoner
import me.ihdeveloper.humans.core.util.randomGameProfile
import me.ihdeveloper.humans.core.util.applyTexture
import org.bukkit.Location

enum class PrisonerType {
    CAMERON,
    HAKIM,
    SARAH,
    XANDER,
    ALI;
}

private const val CAMERON_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYxNzgyMDc0MzA5MiwKICAicHJvZmlsZUlkIiA6ICJmMjc0YzRkNjI1MDQ0ZTQxOGVmYmYwNmM3NWIyMDIxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJIeXBpZ3NlbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MzMyMjhlYmM0NTJlOTE0NDQ3NmMwMjVkODk1NDg4N2VjNTI5YmU0ODBmMzljMWU2NjI5ODE3ZDZlMDNkMjYyIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="
private const val CAMERON_SIGNATURE = "o8xbO/OyhSiGvznu45QjfLpACzuFR8Nxea8oj9vHVAfOJwyY8hXKCEpVH7cKCv4z/euOEpKPqz/z3LoDlGmlwflKT4FpfciqM36vNTTHnVnFPPGAYcj/DP3KfLMtxlr6C8yLBfDGbra/Thj38J+rQJsPJxaUk3/rjqtw3fGXzmFOW69ozk/h9IU5yvRJrKGwGDFmYseEn2Dnq3N3+rk1AKVGKKZgts/heaUdhI9z4qsF5rPfCd0smIzolOcyGICGHNXM7HlxuH61T+eyRi1ZtMWzCKuLaVOQtUPVweIssNOQzAH3LHgrQoLsGWPj7pd/0JC15tyzAME5GerVDmmPEkhvYmW1U1LGG56N59JXDXQhODy+QJHZuT7nEZ9RoUEWexoaSF11RAA4M13fH4ZX25gC3V2Y6JcE1LPpUrwXvsg5q0iyqQDe6p9Ph9hvQPXvBkJzpHS4bYnNa2IeoRdQEno7+gAqvJPB6fNc/V5f0R0upd359Ass6vTSjJIO6T0FLuOkZULWhkfc+97Os1pqfC6i8kniu3tsr8BDy1v1sKyUvWrcmE1O0hLzJGGwKIRpFH6yNzBCp8WVhEOlj7WZTbv9hej901GCs7VwsVvS2DhMyQ24pszRPa7WZTPnsfIdjHKjH93zCEOi2q8pvOs4QK6qN5Fir6fJ1imadgHpMWM="

private const val HAKIM_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYxNzgyNDQxMTQ1MCwKICAicHJvZmlsZUlkIiA6ICI2MTZiODhkNDMwNzM0ZTM3OWM3NDc1ODdlZTJkNzlmZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJfX25vdGFodW1hbl9fIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FkZmFiYjRmMWRjZGM5YWFlMDVmODJhNDIxYTExMmFiNDJjOTVkZTI0NGU0NDYzYjhjYTY5MzkzNTUyNjBiYjkiCiAgICB9CiAgfQp9"
private const val HAKIM_SIGNATURE = "IJaQduMw6kG/njVdDjGvBOb3WNBQcPs7HRr6PlCa2EPb7J+SKKWYFynfRqHoinsTN8hnnnmVUqTNi/+eQk9h+3Nsa2bzZvTNItqaRgw+afcJik4Iv7RN1gYMxQsaL+I3QLLTZDqxsi92tFMKs9LvJKy8+Xk75IYVCo2fzfk4fEqWUIe8oCkoKN9BvIAHxxH4RV3/LUaNzhDfxr+uohnLEzPJ29AB+6uTxr+V7xAoZ6Fk1bA56W0DkhCvpXdChb8m7zIZIvlUu407EhVjhTm4U1uelTZlI5SAf/xejiiyPm9RJXjge1JL3x6Gp/sONjATITzHaSNexQRSIctJ01CYIVfQlKi/chrxcUe+EH/TFfkdbWDW4BbT4+QIKgNRvCHRtGsi2iZCWLW6gFD6BvF4O8lDgFOgyBzwhNA0w3q0SRXT1g2MT08a/WRuuupeRMp+U3Gs5c7qj1ixtVe7EfLZSq87+JoMpQn0Iz8RX3HHr4iNQpBJLRWYUOdU7cZthY1QzcGKW3lbAAqFx8CKCikv5tokNOnFD5YIe2O/wxZj+w/QHGlvP+kvaucjS/lLhVEqSJBdtcADc4LXdEUZcNl4mPSBoYqgU+P1wVFMOJCHGStWAKJaX0AZO6SF/VObYQ0uyJ+O8vNTtkTq57SrrMzcpeAKzidFtydOqNadjlmdxEA="

private const val SARAH_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYxNzgyNDQ2MzE0MSwKICAicHJvZmlsZUlkIiA6ICI3ODc0OTU4NzQ5Y2Q0NzBiYjEyYTY2NmNkZTVkODVmMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYW50cmlYeEQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGZmOWUyN2JkNTdlOTE3ZTdkYmI0M2RkZWI3MWZmOGUyZTVjZGE4NmVlMTZjOTE0NjFkNmE1MDRjMzMyNmQzIgogICAgfQogIH0KfQ=="
private const val SARAH_SIGNATURE = "QQ9nVD1ta0GOPBG1Qcn/XzwZz6ZVy9njzZZV9h2ebanOhvBK4Gz+JuSCmEs7RtwAnk1MF2WJ9hwiPByeoExJBhvpDCipcRw0UPO0cm6IWVyJh+R1MRpdtyLLRtGJHZNE/sJV2R9qdfoI7hzGqk3CZOv/7wU7Ets4z+gvw8n5X+k2aWH5SXj+BpJww/F9tZg/u/FbYTUo5hNWH+k3woat+vp+R/ttmByiyIum8j6hWXtaM4aUY7WC4fmlcWTFj1Ci8iX+/pLl8mFNwr3zFjSFGAkf2lH5hy3faPOGsrZ1e2w2uwbwa7VHzG0rMmt1zIS2iWQlMOF4lubS8CtK1hX0WAnMgd/xg8mhrgboNwMObMzx22Mo5Nq1OQpVzlgn4X5oJ0HV3cColYhEOKaNgBy9Ia+psfb4R7X6OIjTR0bBrTzkk/a+qah/P9b/hIBiy2SN6F+13vT8B9sAqUFjZNAdoTu8g7aaUMFreCwfg3blS7Dn8jof3HiSwBMG8RxWCw+BE87bGtjQDxLbNRcFr8DD6xbezYy54zfEskYwtzdmXeL0fvwfed9fUARpOOTbaUyyFwUYxo8Mxtvak8VtgYypuHaEzaoS+oRe5QXLldfIueLXjFv7t6WhHVTzD0jggbdKZcQqvBGp+Palfm/zAFbNvtfod1MCLiebDO8lk8PtiZU="

private const val XANDER_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYxNzgyNDM3MzExMywKICAicHJvZmlsZUlkIiA6ICJkZTE0MGFmM2NmMjM0ZmM0OTJiZTE3M2Y2NjA3MzViYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTUlRlYW0iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY4Yzg4MzFkMWU0OTJhYTQ4ZTU0MGZhM2Y5MWM0ZjAwY2MzN2FmNzk2YWVhYzRlOTlmNjUwNjc4YmNkYjY3OCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
private const val XANDER_SIGNATURE = "T5PCs+3aiAp6ofTgUJteZNZ6zEUIL/SLFBektVugMpW6EZJH0dGYNfGJGufAvKfeCbdbKeGdjH/bT8Xd/n7rc3Wwa/017rO0jYC7bWYcc8lJwXj1+Xfk2vQ5XQijOmWDMIbmzAkPATaxRTzlt5w44hqFDib54Mib5k8HyUJIrHGuLoB658lR8yb8H6wJ5NAeKMQ908rtK+c6DFGdS/HuIMo7VyIEDQaZPBGGUAosC8zWhORnAeqXvo2ybDNSNgRdNYCmO8uD7SwUf8WB3y2vv7Xggl2NWRBSjGBI1MqLK/c98zjbzupJ7ch0Fx6DuQXVUuUKDaaB7XfKF1HmebH1cW3u016EdzfnVnwJ4o2zoo4m7R0pO1MnS0qcuiJWWmaCLGwXQU63dQ64lMd0dN5dWBYPCHY+Lwoow8FmiEt/lc3znXhjS0HuAq3jW6Khrjn8w66vGfP2BZDa8g9I4youpWrFpDaeRDBbangQv5eNgkSc3kT+ktFg8pkPoqprM1BhbO00jKulWkFWJejgcCbfqYY3oOSFbEuptveTQ0G9YEEL7N7Gip5v1EaWE01WH2YWd6dtSI+Mnd6nalneUDBDdkuxRHtR4LBQlNhuu5cDBjfa016qu64JWIabQ0GWhQf84UUi6X1Z3Oo0PRrNpiICpnB4qBqbqn/BJqylk2HWik8="

private const val ALI_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYxNzgyMjk0Mzg4NywKICAicHJvZmlsZUlkIiA6ICIzYTNmNzhkZmExZjQ0OTllYjE5NjlmYzlkOTEwZGYwYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb19jcmVyYXIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY0YzE3Njc3MDY4YjgwNzFlY2I0M2YwYmVlMTUzNjZjMGY4NmY1OWMxZWQ4Y2ZlMTE1OWNkOTUwMjRlMWMwNyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
private const val ALI_SIGNATURE = "bntjbBRnBrIxoqvlRRrOIenj1nZZcM2dCkG88S3m+1U3ZhsfRkAJD0mj0tnKEZvro4Q4t5J/8JRaNgu0u8KTlxR213KCwsAEUUmBD7iZxJ6rKh5A3AkMJsdzXBcSU76JO3BPdAWe2qsF08WqPgDRnnZc1VpmYz60nKNXmFG8hLvi8BXeYSP328ZbjCw4fQs4dS6ZtzyZJeK3DZ1g4uE0Oby1dp/brEeSuz/VwW/PYjU1hgHWsBKNN3pMmN3WPs7g2QsO52WOgmJ0+nY2g5HO8/4kUHP7wmUpDMob2H1xflS/jpmhGbdo16I2xju4KDADGvpYdVHV1wqC1pPZbD2iwbBLRMRyB7iA2vtAWpmreP6Y4l+HiW3qmpQwgTLb56nDUO08SaegbxOvFhSoh4WAsOID7d9kOeFZtxMteLauwOTJ6DK86zv+Rc7syNdYL15sI6UW7isAMCwgjcJVqYZ95GHUcZfziIuhIU7+sRZlsa6E+qE/+kjwxt65lz8O2sfAHjrlkUXUnAj3q8qwEI1PFFQlG5HndY+dH1k3F7bj4qayToUr+cR8qd9E/5stXy54Mq7P28l86UCwL0e53MrKv5SQpq5QGuj1J+1+RT45lEjZSsf/O9uxpsA8SMTH7J8nzgy1wFQ4gXwrq9V0JDdirNzkl3mzXZdcexB6YQuCaqI="

private const val PROFILE_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYwODU4Mzg3Nzg5NSwKICAicHJvZmlsZUlkIiA6ICJmMTA0NzMxZjljYTU0NmI0OTkzNjM4NTlkZWY5N2NjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJ6aWFkODciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhmZjE0MzE3ODJkOWI3MjUzY2Q1ODk2ZWMwYjBkODkyY2NlYzg3ZDIwN2JmZDM5NTA1MmNlOTE1MjU0M2NmZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
private const val PROFILE_SIGNATURE = "FLyBKi5r4XiOVN4t7eIvsKTofh03+7ooxKd7q9RMN5geFzz09dMOWRXbSazUXLlPyiKgpfzwo5/gAIV1RH0XZxp/4cK0wpESnrVN0gFTlK3up5RnPZKMRk2PNCfeYDOSbqtr34w1ahinmvmSe6KP2dvWmCyPW4/3XYqhaAF4S1GnwvpO7vGTwR876agsANUqpcySN8Su8LeoU2Ow9RQnWcqFwCDleZUKuK+SlESVTrmsKoOOd8W9IWxcc/eRfCV7Q/fj3YRHth7oiH1bRkuYASyb4BmYyjcfnjFQPM4SaBUvt4qy/TmDA9xWTLSmsGb09hBjmpH/DYaHV4aIMxzrVzC2Jk6Pq65+62QMax7NGi/vEfj5An1F7REVguIRccZePMAKj4wkF8YmIQpJl/O8S6CtxNI4fCIjcmycRmFnzFe8Ca7nhuhwI0UY5DNmR2IDqm6CotnFYhcmNXI6y6zK5jzIQQgx8oJA1PCDyK1VDuYyDGMKYDrpfCSgAWm9EeJPS3CDaPtfEaQjnAqAEa92RaW9iZJcrms+6hSXa23DRCgfC+FnFYWR+stqQnhyuN+rKc8qBw+XboJ6zgCAqrfVEz/E4VweqBi8hRrdBt0nzf2uSXXxVUDa471GpEIJYxuYn4oPXd0YY43ILd2JHJ/CGt6LQwhJxKDytHkLZkF4I4s="

fun newPrisoner(location: Location, type: PrisonerType): Prisoner = when(type) {
    PrisonerType.CAMERON -> Prisoner(
        "Cameron",
        randomGameProfile().apply { applyTexture(CAMERON_TEXTURE, CAMERON_SIGNATURE) },
        arrayOf(
            "§7[NPC] §eCameron: §7When the §cWither King§7 opened the stone mine the first mine in this prison",
            "§7[NPC] §eCameron: §7This pickaxe was the one that he used to break the first block in the mine",
        ),
        location,
        randomGameProfile().apply { applyTexture(PROFILE_TEXTURE, PROFILE_SIGNATURE) }
    )
    PrisonerType.HAKIM -> Prisoner(
        "Hakim",
        randomGameProfile().apply { applyTexture(HAKIM_TEXTURE, HAKIM_SIGNATURE) },
        arrayOf(
            "§7[NPC] §eHakim: §7The challenge is to survive in this prison as \"Human\"",
            "§7[NPC] §eHakim: §7The §cWither Soldier§7 is going to lead the prison",
            "§7[NPC] §eHakim: §7His only goal is to make your life as \"Human\" miserable in this prison!"
        ),
        location,
        randomGameProfile().apply { applyTexture(PROFILE_TEXTURE, PROFILE_SIGNATURE) }
    )
    PrisonerType.SARAH -> Prisoner(
        "Sarah",
        randomGameProfile().apply { applyTexture(SARAH_TEXTURE, SARAH_SIGNATURE) },
        arrayOf(
            "§7[NPC] §eSarah: §7We are not ready yet...",
            "§7[NPC] §eSarah: §cMy time will come soon..."
        ),
        location,
        randomGameProfile().apply { applyTexture(PROFILE_TEXTURE, PROFILE_SIGNATURE) }
    )
    PrisonerType.XANDER -> Prisoner(
        "Xander",
        randomGameProfile().apply { applyTexture(XANDER_TEXTURE, XANDER_SIGNATURE) },
        arrayOf(
            "§7[NPC] §eXander: §7Mining a stack of §ePrison Stone§7 in the §eStone Mine§7",
            "§7[NPC] §eXander: §7will give you a pass to access the §eCoal Mine§7!"
        ),
        location,
        randomGameProfile().apply { applyTexture(PROFILE_TEXTURE, PROFILE_SIGNATURE) }
    )
    PrisonerType.ALI -> Prisoner(
        "Ali",
        randomGameProfile().apply { applyTexture(ALI_TEXTURE, ALI_SIGNATURE) },
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
