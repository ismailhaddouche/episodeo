package com.haddouche.episodeo.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.haddouche.episodeo.models.UserList

@Composable
fun AddToListDialog(
    existingLists: List<UserList>,
    onDismissRequest: () -> Unit,
    onCreateNewList: (String) -> Unit,
    onAddToExistingList: (String) -> Unit
) {
    var newListName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Añadir a una lista") },
        text = {
            Column {
                // --- CAMPO PARA CREAR LISTA NUEVA ---
                OutlinedTextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    label = { Text("Nombre de la nueva lista") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- LISTA DE LISTAS EXISTENTES ---
                Text("O selecciona una lista existente:")
                LazyColumn {
                    items(existingLists) { lista ->
                        Text(
                            text = lista.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAddToExistingList(lista.name) }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            // El botón de confirmar ahora solo sirve para crear una lista nueva
            Button(
                onClick = { onCreateNewList(newListName) },
                enabled = newListName.isNotBlank()
            ) {
                Text("Crear y Añadir")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}
