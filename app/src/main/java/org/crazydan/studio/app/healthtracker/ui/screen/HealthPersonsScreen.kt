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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataCard
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
    onEditPerson: (HealthPerson) -> Unit,
    onViewTypes: (HealthPerson) -> Unit
) {
    // 使用 collectAsState() 将 StateFlow 转换为 Compose 状态
    val persons by healthPersons.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("健康跟踪") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPerson) {
                Icon(Icons.Default.Add, contentDescription = "添加人员")
            }
        }
    ) { padding ->
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
                        onDeletePerson = { },
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
        val fullName = getFullName(person.familyName, person.givenName)
        val label = person.label?.let { "$it (${fullName})" } ?: fullName

        Text(text = label, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "年龄: ${calculateAge(person.birthday)}")
    }
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