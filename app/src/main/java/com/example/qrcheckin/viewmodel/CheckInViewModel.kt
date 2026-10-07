package com.example.qrcheckin.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrcheckin.data.CheckInResult
import com.example.qrcheckin.data.Person
import com.example.qrcheckin.data.PersonRepository
import kotlinx.coroutines.launch

class CheckInViewModel : ViewModel() {

    var selectedPerson by mutableStateOf<Person?>(null)
        private set

    var statusMessage by mutableStateOf("")
        private set

    fun checkIn(id: String) {

        viewModelScope.launch {

            val cleanId = id.trim()

            when (PersonRepository.markScanned(cleanId)) {

                CheckInResult.SUCCESS -> {
                    selectedPerson =
                        PersonRepository.refresh(cleanId)

                    statusMessage = "✅ Aprovado."
                }

                CheckInResult.ALREADY_SCANNED -> {
                    selectedPerson =
                        PersonRepository.refresh(cleanId)

                    statusMessage = "⚠️ Ya está registrado."
                }

                CheckInResult.NOT_FOUND -> {
                    selectedPerson = null
                    statusMessage = "❌ No aprovado, DNI: $id"
                }
            }
        }
    }
}