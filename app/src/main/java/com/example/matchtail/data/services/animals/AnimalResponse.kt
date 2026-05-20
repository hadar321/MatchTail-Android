package com.example.matchtail.data.services.animals

typealias BreedName = String

typealias BreedInfo = Map<BreedName, List<BreedName>>

class AnimalResponse(
    val message: BreedInfo?,
    val status: String?
)