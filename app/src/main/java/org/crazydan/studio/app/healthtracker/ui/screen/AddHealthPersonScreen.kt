package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.ui.component.DateInputPicker
import org.crazydan.studio.app.healthtracker.ui.component.TimeInputPicker
import org.crazydan.studio.app.healthtracker.util.toEpochMilli
import java.time.LocalDate
import java.time.LocalTime

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHealthPersonScreen(
    onSave: (HealthPerson) -> Unit,
    onCancel: () -> Unit
) {
    var label by remember { mutableStateOf("") }
    var familyName by remember { mutableStateOf("") }
    var givenName by remember { mutableStateOf("") }

    var birthDate: LocalDate? by remember { mutableStateOf(null) }
    var birthTime: LocalTime? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加人员信息") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            Button(
                onClick = {
                    if (familyName.isNotBlank() && givenName.isNotEmpty() && birthDate != null && birthTime != null) {
                        onSave(
                            HealthPerson(
                                label = label,
                                familyName = familyName, givenName = givenName,
                                birthday = toEpochMilli(birthDate!!, birthTime!!)
                            )
                        )
                    }
                }
            ) {
                Icon(Icons.Default.Save, contentDescription = "保存")
                Spacer(modifier = Modifier.padding(4.dp))
                Text("保存")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("称呼") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = familyName,
                    onValueChange = { familyName = it },
                    label = { Text("姓") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = givenName,
                    onValueChange = { givenName = it },
                    label = { Text("名") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                DateInputPicker(
                    label = { Text("出生日期") },
                    onValueChange = { birthDate = it },
                )

                Spacer(modifier = Modifier.height(16.dp))
                TimeInputPicker(
                    label = { Text("出生时间") },
                    onValueChange = { birthTime = it },
                )
            }
        }
    }
}