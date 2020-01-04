package com.daohang.trainapp.db.models

data class Province(val name: String, val code: String, val city: Array<City>)

data class City(val name: String, val code: String, val area: Array<Area>)

data class Area(val name: String, val code: String)