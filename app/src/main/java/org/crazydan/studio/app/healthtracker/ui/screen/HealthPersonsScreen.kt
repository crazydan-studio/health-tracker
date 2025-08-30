package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import org.crazydan.studio.app.healthtracker.R
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataCard
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataListScreen
import org.crazydan.studio.app.healthtracker.util.calculateAge
import org.crazydan.studio.app.healthtracker.util.getFullName
import java.sql.Timestamp

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthPersonsScreen(
    healthPersons: StateFlow<List<HealthPerson>>,
    onAddPerson: () -> Unit,
    onDeletePerson: (HealthPerson) -> Unit,
    onUndeletePerson: (HealthPerson) -> Unit,
    onEditPerson: (HealthPerson) -> Unit,
    onViewTypes: (HealthPerson) -> Unit
) {
    // 使用 collectAsState() 将 StateFlow 转换为 Compose 状态
    val persons by healthPersons.collectAsState()

    HealthDataListScreen(
        title = {
            Text(
                stringResource(R.string.app_name)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPerson) {
                Icon(Icons.Default.Add, contentDescription = "添加人员")
            }
        },
        deletedMessage = { person ->
            "已删除人员【${getPersonFullName(person)}】"
        },
        onUndelete = onUndeletePerson,
    ) { padding, afterDeleted ->
        if (persons.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("暂无人员信息，请点击右下角按钮添加")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(persons) { person ->
                    HealthPersonItem(
                        person = person,
                        onDeletePerson = {
                            onDeletePerson(person)
                            afterDeleted(person)
                        },
                        onEditPerson = { onEditPerson(person) },
                        onViewTypes = { onViewTypes(person) },
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthPersonItem(
    person: HealthPerson,
    onDeletePerson: () -> Unit,
    onEditPerson: () -> Unit,
    onViewTypes: () -> Unit,
) {
    HealthDataCard(
        onEdit = onEditPerson,
        onDelete = onDeletePerson,
        onView = onViewTypes,
    ) {
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
private fun HealthPersonItemPreview() {
    HealthPersonItem(
        person = HealthPerson(
            id = 0,
            label = "老五",
            familyName = "王",
            givenName = "五",
            birthday = Timestamp.valueOf("1988-08-10 08:10:00.000").time,
        ),
        onDeletePerson = {},
        onEditPerson = {},
        onViewTypes = {},
    )
}