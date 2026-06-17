package es.myvacations.myvacations.presentation.utils

import androidx.compose.runtime.Composable
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.argentina
import myvacations.shared.generated.resources.australia
import myvacations.shared.generated.resources.austria
import myvacations.shared.generated.resources.brazil
import myvacations.shared.generated.resources.canada
import myvacations.shared.generated.resources.china
import myvacations.shared.generated.resources.croatia
import myvacations.shared.generated.resources.egypt
import myvacations.shared.generated.resources.france
import myvacations.shared.generated.resources.germany
import myvacations.shared.generated.resources.greece
import myvacations.shared.generated.resources.iceland
import myvacations.shared.generated.resources.india
import myvacations.shared.generated.resources.indonesia
import myvacations.shared.generated.resources.ireland
import myvacations.shared.generated.resources.italy
import myvacations.shared.generated.resources.japan
import myvacations.shared.generated.resources.jordan
import myvacations.shared.generated.resources.kenya
import myvacations.shared.generated.resources.maldives
import myvacations.shared.generated.resources.mexico
import myvacations.shared.generated.resources.morocco
import myvacations.shared.generated.resources.netherlands
import myvacations.shared.generated.resources.new_zealand
import myvacations.shared.generated.resources.norway
import myvacations.shared.generated.resources.peru
import myvacations.shared.generated.resources.portugal
import myvacations.shared.generated.resources.singapore
import myvacations.shared.generated.resources.south_africa
import myvacations.shared.generated.resources.south_korea
import myvacations.shared.generated.resources.spain
import myvacations.shared.generated.resources.sri_lanka
import myvacations.shared.generated.resources.switzerland
import myvacations.shared.generated.resources.tanzania
import myvacations.shared.generated.resources.thailand
import myvacations.shared.generated.resources.tunisia
import myvacations.shared.generated.resources.united_arab_emirates
import myvacations.shared.generated.resources.united_kingdom
import myvacations.shared.generated.resources.united_states
import myvacations.shared.generated.resources.vietnam
import org.jetbrains.compose.resources.stringResource

enum class Country {
    SPAIN,
    FRANCE,
    ITALY,
    PORTUGAL,
    UNITED_KINGDOM,
    GERMANY,
    GREECE,
    CROATIA,
    NETHERLANDS,
    SWITZERLAND,
    AUSTRIA,
    ICELAND,
    NORWAY,
    IRELAND,
    UNITED_STATES,
    CANADA,
    MEXICO,
    BRAZIL,
    ARGENTINA,
    PERU,
    JAPAN,
    THAILAND,
    INDONESIA,
    VIETNAM,
    SINGAPORE,
    SOUTH_KOREA,
    CHINA,
    INDIA,
    MALDIVES,
    SRI_LANKA,
    AUSTRALIA,
    NEW_ZEALAND,
    EGYPT,
    MOROCCO,
    SOUTH_AFRICA,
    UNITED_ARAB_EMIRATES,
    JORDAN,
    TUNISIA,
    KENYA,
    TANZANIA
}

@Composable
fun Country.displayName(): String =
    when (this) {
        Country.SPAIN -> stringResource(Res.string.spain)
        Country.FRANCE -> stringResource(Res.string.france)
        Country.ITALY -> stringResource(Res.string.italy)
        Country.PORTUGAL -> stringResource(Res.string.portugal)
        Country.UNITED_KINGDOM -> stringResource(Res.string.united_kingdom)
        Country.GERMANY -> stringResource(Res.string.germany)
        Country.GREECE -> stringResource(Res.string.greece)
        Country.CROATIA -> stringResource(Res.string.croatia)
        Country.NETHERLANDS -> stringResource(Res.string.netherlands)
        Country.SWITZERLAND -> stringResource(Res.string.switzerland)
        Country.AUSTRIA -> stringResource(Res.string.austria)
        Country.ICELAND -> stringResource(Res.string.iceland)
        Country.NORWAY -> stringResource(Res.string.norway)
        Country.IRELAND -> stringResource(Res.string.ireland)
        Country.UNITED_STATES -> stringResource(Res.string.united_states)
        Country.CANADA -> stringResource(Res.string.canada)
        Country.MEXICO -> stringResource(Res.string.mexico)
        Country.BRAZIL -> stringResource(Res.string.brazil)
        Country.ARGENTINA -> stringResource(Res.string.argentina)
        Country.PERU -> stringResource(Res.string.peru)
        Country.JAPAN -> stringResource(Res.string.japan)
        Country.THAILAND -> stringResource(Res.string.thailand)
        Country.INDONESIA -> stringResource(Res.string.indonesia)
        Country.VIETNAM -> stringResource(Res.string.vietnam)
        Country.SINGAPORE -> stringResource(Res.string.singapore)
        Country.SOUTH_KOREA -> stringResource(Res.string.south_korea)
        Country.CHINA -> stringResource(Res.string.china)
        Country.INDIA -> stringResource(Res.string.india)
        Country.MALDIVES -> stringResource(Res.string.maldives)
        Country.SRI_LANKA -> stringResource(Res.string.sri_lanka)
        Country.AUSTRALIA -> stringResource(Res.string.australia)
        Country.NEW_ZEALAND -> stringResource(Res.string.new_zealand)
        Country.EGYPT -> stringResource(Res.string.egypt)
        Country.MOROCCO -> stringResource(Res.string.morocco)
        Country.SOUTH_AFRICA -> stringResource(Res.string.south_africa)
        Country.UNITED_ARAB_EMIRATES -> stringResource(Res.string.united_arab_emirates)
        Country.JORDAN -> stringResource(Res.string.jordan)
        Country.TUNISIA -> stringResource(Res.string.tunisia)
        Country.KENYA -> stringResource(Res.string.kenya)
        Country.TANZANIA -> stringResource(Res.string.tanzania)
    }

val Country.flag: String
    get() = when (this) {
        Country.SPAIN -> "🇪🇸"
        Country.FRANCE -> "🇫🇷"
        Country.ITALY -> "🇮🇹"
        Country.PORTUGAL -> "🇵🇹"
        Country.UNITED_KINGDOM -> "🇬🇧"
        Country.GERMANY -> "🇩🇪"
        Country.GREECE -> "🇬🇷"
        Country.CROATIA -> "🇭🇷"
        Country.NETHERLANDS -> "🇳🇱"
        Country.SWITZERLAND -> "🇨🇭"
        Country.AUSTRIA -> "🇦🇹"
        Country.ICELAND -> "🇮🇸"
        Country.NORWAY -> "🇳🇴"
        Country.IRELAND -> "🇮🇪"
        Country.UNITED_STATES -> "🇺🇸"
        Country.CANADA -> "🇨🇦"
        Country.MEXICO -> "🇲🇽"
        Country.BRAZIL -> "🇧🇷"
        Country.ARGENTINA -> "🇦🇷"
        Country.PERU -> "🇵🇪"
        Country.JAPAN -> "🇯🇵"
        Country.THAILAND -> "🇹🇭"
        Country.INDONESIA -> "🇮🇩"
        Country.VIETNAM -> "🇻🇳"
        Country.SINGAPORE -> "🇸🇬"
        Country.SOUTH_KOREA -> "🇰🇷"
        Country.CHINA -> "🇨🇳"
        Country.INDIA -> "🇮🇳"
        Country.MALDIVES -> "🇲🇻"
        Country.SRI_LANKA -> "🇱🇰"
        Country.AUSTRALIA -> "🇦🇺"
        Country.NEW_ZEALAND -> "🇳🇿"
        Country.EGYPT -> "🇪🇬"
        Country.MOROCCO -> "🇲🇦"
        Country.SOUTH_AFRICA -> "🇿🇦"
        Country.UNITED_ARAB_EMIRATES -> "🇦🇪"
        Country.JORDAN -> "🇯🇴"
        Country.TUNISIA -> "🇹🇳"
        Country.KENYA -> "🇰🇪"
        Country.TANZANIA -> "🇹🇿"
    }