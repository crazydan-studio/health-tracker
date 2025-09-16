package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.R
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.ui.Message
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataCard
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataCardActions
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataListScreen
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataLoadingScreen
import org.crazydan.studio.app.healthtracker.util.calculateAge
import org.crazydan.studio.app.healthtracker.util.getFullName

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthPersonsScreen(
    healthPersons: List<HealthPerson>?,
    deletedPersonAmount: Long,
) {
    if (healthPersons == null) {
        HealthDataLoadingScreen()
        return
    }

    HealthDataListScreen(
        deletedAmount = deletedPersonAmount,
        dataList = healthPersons,
        title = {
            Text(
                stringResource(R.string.app_name)
            )
        },
        onAddData = {
            Message.WillAddHealthPerson()
        },
        onViewDeleted = {
            Message.ViewDeletedHealthPersons()
        },
    ) { person ->
        HealthPersonCard(
            person = person,
            actions = HealthDataCardActions(
                onEdit = {
                    Message.WillEditHealthPerson(person.id)
                },
                onDelete = {
                    Message.DeleteHealthPerson(person.id)
                },
                onView = {
                    Message.ViewHealthTypesOfPerson(person.id)
                },
            ),
        )
    }
}

@Composable
fun HealthPersonCard(
    person: HealthPerson,
    actions: HealthDataCardActions,
) {
    HealthDataCard(actions) {
        val fullName = getPersonFullName(person)
        val label =
            if (person.label.isNullOrBlank()) fullName
            else "${person.label} (${fullName})"

        Text(text = label, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "年龄: ${calculateAge(person.birthday)}")
    }
}

private fun getPersonFullName(person: HealthPerson): String {
    return getFullName(person.familyName, person.givenName)
}

@Preview
@Composable
private fun HealthPersonCardPreview() {
    HealthPersonCard(
        person = PreviewSample().createHealthPerson(),
        actions = HealthDataCardActions(),
    )
}