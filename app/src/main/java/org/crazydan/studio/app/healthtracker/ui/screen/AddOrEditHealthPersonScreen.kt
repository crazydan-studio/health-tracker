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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.R
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
                if (inAddMode)
                    stringResource(R.string.title_add_health_person)
                else
                    stringResource(R.string.title_edit_health_person),
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
            label = { Text(stringResource(R.string.label_health_person_field_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = familyName,
            onValueChange = { familyName = it.trim() },
            label = { Text(stringResource(R.string.label_health_person_field_family_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = givenName,
            onValueChange = { givenName = it.trim() },
            label = { Text(stringResource(R.string.label_health_person_field_given_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        DateInputPicker(
            modifier = Modifier.fillMaxWidth(),
            value = birthDate,
            label = { Text(stringResource(R.string.label_health_person_field_birth_day)) },
            onValueChange = { birthDate = it },
        )

        Spacer(modifier = Modifier.height(16.dp))
        TimeInputPicker(
            modifier = Modifier.fillMaxWidth(),
            value = birthTime,
            label = { Text(stringResource(R.string.label_health_person_field_birth_time)) },
            onValueChange = { birthTime = it },
        )
    }
}