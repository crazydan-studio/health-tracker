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
import org.crazydan.studio.app.healthtracker.model.HealthRecordFilter
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.getPersonLabel
import org.crazydan.studio.app.healthtracker.ui.Message
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataCard
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataCardActions
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataListScreen
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataLoadingScreen
import org.crazydan.studio.app.healthtracker.util.toEpochMillis
import java.time.LocalDate

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTypesScreen(
    healthPerson: HealthPerson?,
    healthTypes: List<HealthType>,
    deletedTypeAmount: Long,
) {
    if (healthPerson == null) {
        HealthDataLoadingScreen()
        return
    }

    HealthDataListScreen(
        deletedAmount = deletedTypeAmount,
        dataList = healthTypes,
        title = {
            Text(
                stringResource(
                    R.string.title_health_types,
                    getPersonLabel(healthPerson),
                )
            )
        },
        onAddData = {
            Message.WillAddHealthTypeOfPerson(healthPerson.id)
        },
        onViewDeleted = {
            Message.ViewDeletedHealthTypesOfPerson(healthPerson.id)
        },
        onNavigateBack = { Message.NavBack() },
    ) { type ->
        HealthTypeCard(
            type = type,
            actions = HealthDataCardActions(
                onEdit = {
                    Message.WillEditHealthType(
                        type.id,
                        type.personId
                    )
                },
                onDelete = {
                    Message.DeleteHealthType(type.id)
                },
                onView = {
                    val now = LocalDate.now()
                    Message.ViewHealthRecordsOfType(
                        type.id,
                        type.personId,
                        filter = HealthRecordFilter(
                            startDate = toEpochMillis(now.minusDays(7)),
                            endDate = toEpochMillis(now, untilToDayEnd = true),
                        ),
                    )
                },
            ),
        )
    }
}

@Composable
fun HealthTypeCard(
    type: HealthType,
    actions: HealthDataCardActions,
) {
    HealthDataCard(actions) {
        Text(text = type.name, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.label_health_type_field_unit)
                    + ": ${type.unit}"
        )

        if (type.limit.lower != null || type.limit.upper != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.label_health_type_field_limit)
                        + ": ${type.limit} ${type.unit}"
            )
        }

        if (type.measures.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.label_health_type_field_measures)
                        + ":",
                style = MaterialTheme.typography.labelMedium
            )

            type.measures.forEach { measure ->
                Text(text = "  ${measure.name}: ${measure.limit} ${type.unit}")
            }
        }
    }
}

@Preview
@Composable
private fun HealthTypeCardPreview() {
    HealthTypeCard(
        type = PreviewSample().createHealthType(),
        actions = HealthDataCardActions(
            onEdit = { Message.NavBack() },
            onDelete = { Message.NavBack() },
        ),
    )
}