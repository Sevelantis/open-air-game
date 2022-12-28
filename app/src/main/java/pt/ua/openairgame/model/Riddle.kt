package pt.ua.openairgame.model

import android.location.Location
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Riddle(var index: Int, var location: Location, var question: String, var answer: String) :
    Parcelable {}

//object RiddleParceler : Parceler<Riddle> {
//    override fun create(parcel: Parcel) = Riddle(
//        parcel.readInt(),
//        Location.CREATOR.createFromParcel(parcel),
//        parcel.readString()!!,
//        parcel.readString()!!
//    )
//
//    override fun Riddle.write(parcel: Parcel, flags: Int) {
//        parcel.writeInt(index)
//        location.writeToParcel(parcel, flags)
//        parcel.writeString(question)
//        parcel.writeString(answer)
//    }
//}