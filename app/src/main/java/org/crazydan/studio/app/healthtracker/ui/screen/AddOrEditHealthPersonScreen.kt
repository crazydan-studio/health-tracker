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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.ui.component.DateInputPicker
import org.crazydan.studio.app.healthtracker.ui.component.TimeInputPicker
import org.crazydan.studio.app.healthtracker.util.epochMillisToLocalDateTime
import org.crazydan.studio.app.healthtracker.util.toEpochMillis
import java.time.LocalDate
import java.time.LocalTime

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditHealthPersonScreen(
    editPerson: StateFlow<HealthPerson?>? = null,
    onSave: (HealthPerson) -> Unit,
    onCancel: () -> Unit
) {
    val currentEditPerson = editPerson?.collectAsState()?.value

    var label by remember { mutableStateOf(currentEditPerson?.label ?: "") }
    var familyName by remember { mutableStateOf(currentEditPerson?.familyName ?: "") }
    var givenName by remember { mutableStateOf(currentEditPerson?.givenName ?: "") }

    val birthday =
        if (currentEditPerson == null) null
        else epochMillisToLocalDateTime(currentEditPerson.birthday)
    var birthDate: LocalDate? by remember {
        mutableStateOf(birthday?.toLocalDate())
    }
    var birthTime: LocalTime? by remember {
        mutableStateOf(birthday?.toLocalTime())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        (if (editPerson == null) "添加" else "编辑") + "人员信息"
                    )
                },
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
                    if (familyName.isNotBlank() && givenName.isNotBlank() && birthDate != null && birthTime != null) {
                        onSave(
                            HealthPerson(
                                id = currentEditPerson?.id ?: 0,
                                label = label.trim(),
                                familyName = familyName.trim(), givenName = givenName.trim(),
                                birthday = toEpochMillis(birthDate!!, birthTime!!)
                            )
                        )
                    }
                },
                enabled = familyName.isNotBlank() && givenName.isNotBlank() && birthDate != null && birthTime != null
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
                    value = birthDate,
                    label = { Text("出生日期") },
                    onValueChange = { birthDate = it },
                )

                Spacer(modifier = Modifier.height(16.dp))
                TimeInputPicker(
                    value = birthTime,
                    label = { Text("出生时间") },
                    onValueChange = { birthTime = it },
                )
            }
        }
    }
}