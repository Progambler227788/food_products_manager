package com.prm.foodproductsmanager

import android.os.Parcel
import android.os.Parcelable
import  java.util.*

// Item Class implementing Parcelable because data will be exchanged when data is added or modified
data class Item(
    val productName: String,
    val expirationDate: Date,
    val category: String,
    val quantity: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        Date(parcel.readLong()),
        parcel.readString() ?: "",
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productName)
        parcel.writeLong(expirationDate.time)
        parcel.writeString(category)
        parcel.writeValue(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}

