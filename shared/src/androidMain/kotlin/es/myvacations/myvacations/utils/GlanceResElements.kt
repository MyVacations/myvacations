package es.myvacations.myvacations.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.graphics.createBitmap
import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.presentation.utils.Currency
import es.myvacations.myvacations.shared.R


@DrawableRes
fun TripCover.glanceImageRes(): Int = when (this) {
    TripCover.TOKYO -> R.drawable.tokyo
    TripCover.PARIS -> R.drawable.paris
    TripCover.LONDON -> R.drawable.london
    TripCover.BARCELONA -> R.drawable.barcelona
    TripCover.ROME -> R.drawable.roma
    TripCover.BEACH -> R.drawable.beach
    TripCover.MOUNTAIN -> R.drawable.mountain
}

@StringRes
fun Currency.glanceStringRes(): Int = when (this) {
    Currency.DOLLAR -> R.string.dollar
    Currency.EURO -> R.string.euro
    Currency.YEN -> R.string.yen
    Currency.YUAN -> R.string.yuan
    Currency.PESO -> R.string.peso
}

@StringRes
fun Country.glanceStringRes(): Int = when (this) {
    Country.SPAIN -> R.string.spain
    Country.FRANCE -> R.string.france
    Country.ITALY -> R.string.italy
    Country.PORTUGAL -> R.string.portugal
    Country.UNITED_KINGDOM -> R.string.united_kingdom
    Country.GERMANY -> R.string.germany
    Country.GREECE -> R.string.greece
    Country.CROATIA -> R.string.croatia
    Country.NETHERLANDS -> R.string.netherlands
    Country.SWITZERLAND -> R.string.switzerland
    Country.AUSTRIA -> R.string.austria
    Country.ICELAND -> R.string.iceland
    Country.NORWAY -> R.string.norway
    Country.IRELAND -> R.string.ireland
    Country.UNITED_STATES -> R.string.united_states
    Country.CANADA -> R.string.canada
    Country.MEXICO -> R.string.mexico
    Country.BRAZIL -> R.string.brazil
    Country.ARGENTINA -> R.string.argentina
    Country.PERU -> R.string.peru
    Country.JAPAN -> R.string.japan
    Country.THAILAND -> R.string.thailand
    Country.INDONESIA -> R.string.indonesia
    Country.VIETNAM -> R.string.vietnam
    Country.SINGAPORE -> R.string.singapore
    Country.SOUTH_KOREA -> R.string.south_korea
    Country.CHINA -> R.string.china
    Country.INDIA -> R.string.india
    Country.MALDIVES -> R.string.maldives
    Country.SRI_LANKA -> R.string.sri_lanka
    Country.AUSTRALIA -> R.string.australia
    Country.NEW_ZEALAND -> R.string.new_zealand
    Country.EGYPT -> R.string.egypt
    Country.MOROCCO -> R.string.morocco
    Country.SOUTH_AFRICA -> R.string.south_africa
    Country.UNITED_ARAB_EMIRATES -> R.string.united_arab_emirates
    Country.JORDAN -> R.string.jordan
    Country.TUNISIA -> R.string.tunisia
    Country.KENYA -> R.string.kenya
    Country.TANZANIA -> R.string.tanzania
}

fun createBudgetProgressBitmap(
    percentage: Int,
    color: Int = android.graphics.Color.RED,
    size: Int = 160
): Bitmap {
    val bitmap = createBitmap(size, size)

    val canvas = Canvas(bitmap)

    val strokeWidth = 14f

    val backgroundPaint = Paint().apply {
        style = Paint.Style.STROKE
        this.strokeWidth = strokeWidth
        isAntiAlias = true
        this.color = android.graphics.Color.DKGRAY
    }

    val progressPaint = Paint().apply {
        style = Paint.Style.STROKE
        this.strokeWidth = strokeWidth
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        this.color = color
    }

    val textPaint = Paint().apply {
        this.color = android.graphics.Color.WHITE
        textSize = 38f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }

    val rect = RectF(
        strokeWidth,
        strokeWidth,
        size - strokeWidth,
        size - strokeWidth
    )

    // Fondo del círculo
    canvas.drawArc(
        rect,
        0f,
        360f,
        false,
        backgroundPaint
    )

    // Progreso
    val progress = percentage
        .coerceIn(0, 100) / 100f

    canvas.drawArc(
        rect,
        -90f,
        360f * progress,
        false,
        progressPaint
    )

    // Texto central
    val y = size / 2f -
            (textPaint.descent() + textPaint.ascent()) / 2f

    canvas.drawText(
        "$percentage%",
        size / 2f,
        y,
        textPaint
    )

    return bitmap
}