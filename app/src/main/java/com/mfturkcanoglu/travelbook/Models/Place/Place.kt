package com.mfturkcanoglu.travelbook.Models.Place

class Place {
    var streetName : String? = null
        get () = field
    var lat : Double? = null
        get()  = field
    var long : Double? = null
        get() = field

    constructor(street : String, lat : Double, long : Double){
        this.streetName = street
        this.lat = lat
        this.long = long
    }

    init {
        println("Place object created")
    }

    override fun toString(): String {
        if(streetName != null){
            return streetName!!
        }
        return "Not Known Address"
    }
}