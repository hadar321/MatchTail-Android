package com.example.matchtail.data.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "animals")
data class Animal(
    @PrimaryKey
    var id: String = "",
    val name: String = "",
    val minHeight: Double? = null,
    val maxHeight: Double? = null,
    val averageLifeExpectancy: Double? = null,
    var lastUpdated: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        null
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeValue(minHeight)
        parcel.writeValue(maxHeight)
        parcel.writeValue(averageLifeExpectancy)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Animal> {
        override fun createFromParcel(parcel: Parcel): Animal {
            return Animal(parcel)
        }

        override fun newArray(size: Int): Array<Animal?> {
            return arrayOfNulls(size)
        }

        private const val ID_KEY = "id"
        private const val NAME_KEY = "name"
        private const val MIN_HEIGHT_KEY = "minHeight"
        private const val MAX_HEIGHT_KEY = "maxHeight"
        private const val LIFE_EXPECTANCY_KEY = "averageLifeExpectancy"
        internal const val TIMESTAMP_KEY = "lastUpdated"

        fun fromJSON(json: Map<String, Any>): Animal {
            val id = json[ID_KEY] as? String ?: ""
            val name = json[NAME_KEY] as? String ?: ""
            val minHeight: Double = extractDouble(json, MIN_HEIGHT_KEY)
            val maxHeight: Double = extractDouble(json, MAX_HEIGHT_KEY)
            val averageLifeExpectancy: Double = extractDouble(json, LIFE_EXPECTANCY_KEY)
            val timestamp = (json[TIMESTAMP_KEY] as? Timestamp ?: Timestamp(0, 0))
            val lastUpdated = timestamp.toDate().time
            return Animal(
                id,
                name,
                minHeight,
                maxHeight,
                averageLifeExpectancy,
                lastUpdated
            )
        }

        private fun extractDouble(json: Map<String, Any>, key: String): Double {
            return if (json[key] is Double) {
                json[key] as? Double ?: 0.0
            } else if (json[key] is Long) {
                (json[key] as? Long ?: 0L).toDouble()
            } else {
                0.0
            }
        }
    }
}