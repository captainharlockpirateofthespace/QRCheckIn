package com.example.qrcheckin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.qrcheckin.csv.CsvImporter
import com.example.qrcheckin.ui.theme.QRCheckInTheme
import com.example.qrcheckin.data.Person
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.qrcheckin.data.PersonRepository
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.draw.clipToBounds
import com.example.qrcheckin.data.CheckInResult
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qrcheckin.data.PersonType
import com.example.qrcheckin.viewmodel.CheckInViewModel
import com.example.qrcheckin.scanner.CameraPreview
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        PersonRepository.initialize(applicationContext)

        setContent {
            QRCheckInTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    QRCheckInScreen(
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }
}

@Composable
fun QRCheckInScreen(
    modifier: Modifier = Modifier
) {

    val viewModel: CheckInViewModel = viewModel()

    val context = LocalContext.current

    var people by remember {
        mutableStateOf<List<Person>>(emptyList())
    }

    var enteredId by remember {
        mutableStateOf("")
    }

    var scannerVisible by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {

            val imported = CsvImporter().import(context, uri)

            scope.launch {

                PersonRepository.setPeople(imported)

                people = PersonRepository.getPeople()

                Toast.makeText(
                    context,
                    "Importadas ${people.size} personas",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "QR Check-In",
            style = MaterialTheme.typography.headlineMedium
        )



        Button(
            onClick = {
                launcher.launch(arrayOf("text/*"))
            }
        ) {
            Text("Importar CSV")
        }

        Text("Importadas: ${people.size} personas")


        Button(
            onClick = {
                scannerVisible = true
            }
        ) {
            Text("Scanear Codigo QR")
        }

        if (scannerVisible) {

            CameraPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clipToBounds(),

                onQrCodeDetected = { value ->

                    scannerVisible = false

                    viewModel.checkIn(value)
                }
            )
        }


        OutlinedTextField(
            value = enteredId,
            onValueChange = { enteredId = it },
            label = { Text("DNI") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.checkIn(enteredId)
            }
        ) {
            Text("Check In")
        }

        Text(
            text = viewModel.statusMessage,
            style = MaterialTheme.typography.titleMedium
        )

        viewModel.selectedPerson?.let { person ->

            Text("Nombre: ${person.name}")
            Text("DNI: ${person.id}")
            Text("Cena: ${if (person.dinner) "Si" else "No"}")
            Text(
                "Tipo: ${
                    person.type.name.lowercase()
                        .replaceFirstChar { it.uppercase() }
                }"
            )
            if(person.type != PersonType.INDIVIDUAL) {
                Text("Miembros: ${person.members}")
            }
        }

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            items(people) { person ->

                Text(
                    text = "${person.id} - ${person.name}",
                    modifier = Modifier.padding(vertical = 4.dp)
                )

            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun QRCheckInScreenPreview() {
    QRCheckInTheme {
        QRCheckInScreen()
    }
}