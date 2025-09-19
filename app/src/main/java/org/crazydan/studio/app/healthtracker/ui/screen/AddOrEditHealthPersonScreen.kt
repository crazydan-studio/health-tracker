package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.ui.Message
import org.crazydan.studio.app.healthtracker.ui.component.AddOrEditHealthDataScreen
import org.crazydan.studio.app.healthtracker.ui.component.DateInputPicker
import org.crazydan.studio.app.healthtracker.ui.component.TimeInputPicker
import org.crazydan.studio.app.healthtracker.util.epochMillisToLocalDateTime
import org.crazydan.studio.app.healthtracker.util.toEpochMillis

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditHealthPersonScreen(
    editPerson: HealthPerson? = null,
) {
    val inAddMode = remember(editPerson) { editPerson == null }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(inAddMode) {
        if (inAddMode) {
            focusRequester.requestFocus()
        } else {
            focusRequester.freeFocus()
        }
    }

    var label by remember(editPerson) { mutableStateOf(editPerson?.label ?: "") }
    var familyName by remember(editPerson) { mutableStateOf(editPerson?.familyName ?: "") }
    var givenName by remember(editPerson) { mutableStateOf(editPerson?.givenName ?: "") }

    val birthday = epochMillisToLocalDateTime(editPerson?.birthday)
    var birthDate by remember(editPerson) { mutableStateOf(birthday?.toLocalDate()) }
    var birthTime by remember(editPerson) { mutableStateOf(birthday?.toLocalTime()) }

    AddOrEditHealthDataScreen(
        title = {
            Text(
                (if (inAddMode) "添加" else "编辑") + "人员信息"
            )
        },
        onNavigateBack = { Message.NavBack() },
        canSave = {
            familyName.isNotEmpty() && givenName.isNotEmpty() && birthDate != null
        },
        onSave = {
            val person = HealthPerson(
                id = editPerson?.id ?: 0,
                label = label,
                familyName = familyName,
                givenName = givenName,
                birthday = toEpochMillis(birthDate!!, birthTime)
            )

            Message.SaveHealthPerson(person)
        },
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = { label = it.trim() },
            label = { Text("称呼") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = familyName,
            onValueChange = { familyName = it.trim() },
            label = { Text("姓") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = givenName,
            onValueChange = { givenName = it.trim() },
            label = { Text("名") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        DateInputPicker(
            modifier = Modifier.fillMaxWidth(),
            value = birthDate,
            label = { Text("出生日期") },
            onValueChange = { birthDate = it },
        )

        Spacer(modifier = Modifier.height(16.dp))
        TimeInputPicker(
            modifier = Modifier.fillMaxWidth(),
            value = birthTime,
            label = { Text("出生时间 (可选)") },
            onValueChange = { birthTime = it },
        )
    }
}