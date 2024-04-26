package com.example.mytempapplication.medicationinformation

import kotlinx.serialization.Serializable

@Serializable
data class APIMedicineResponse(
    val name: String,
    val hasPart: List<APIMedicineInformation>
)
@Serializable
data class APIMedicineInformation(
    val description: String,
    val headline: String,
    val hasPart: List<NestedHasPart>,
)
@Serializable
data class NestedHasPart(
    val headline: String,
    val text: String
)