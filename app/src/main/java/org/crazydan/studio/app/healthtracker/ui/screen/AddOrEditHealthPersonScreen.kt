package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.ui.Event
import org.crazydan.studio.app.healthtracker.ui.EventDispatch
import org.crazydan.studio.app.healthtracker.ui.component.AddOrEditHealthDataScreen
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
    editPerson: HealthPerson? = null,
    eventDispatch: EventDispatch,
) {
    var label by remember { mutableStateOf("") }
    var familyName by remember { mutableStateOf("") }
    var givenName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<LocalDate?>(null) }
    var birthTime by remember { mutableStateOf<LocalTime?>(null) }

    editPerson?.let { person ->
        label = person.label ?: ""
        familyName = person.familyName
        givenName = person.givenName

        val birthday = epochMillisToLocalDateTime(person.birthday)
        birthDate = birthday.toLocalDate()
        birthTime = birthday.toLocalTime()
    }

    AddOrEditHealthDataScreen(
        title = {
            Text(
                (if (editPerson == null) "添加" else "编辑") + "人员信息"
            )
        },
        onNavigateBack = { eventDispatch(Event.NavBack()) },
        canSave = {
            familyName.isNotBlank() && givenName.isNotBlank() && birthDate != null
        },
        onSave = {
            val person = HealthPerson(
                id = editPerson?.id ?: 0,
                label = label.trim(),
                familyName = familyName.trim(), givenName = givenName.trim(),
                birthday = toEpochMillis(birthDate!!, birthTime)
            )

            if (person.id == 0L) {
                eventDispatch(Event.SaveHealthPerson(person))
            } else {
                eventDispatch(Event.UpdateHealthPerson(person))
            }
        },
    ) {
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
            label = { Text("出生时间 (可选)") },
            onValueChange = { birthTime = it },
        )
    }
}