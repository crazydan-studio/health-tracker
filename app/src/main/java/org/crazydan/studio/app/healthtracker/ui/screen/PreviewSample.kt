package org.crazydan.studio.app.healthtracker.ui.screen

import org.crazydan.studio.app.healthtracker.model.HealthLimit
import org.crazydan.studio.app.healthtracker.model.HealthMeasure
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import java.sql.Timestamp

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-04
 */
class PreviewSample {

    fun createHealthPerson(): HealthPerson {
        return HealthPerson(
            id = 0,
            label = "老五",
            familyName = "王",
            givenName = "五",
            birthday = Timestamp.valueOf("1988-08-10 08:10:00.000").time,
        )
    }

    fun createHealthType(): HealthType {
        return HealthType(
            id = 0,
            personId = 0,
            name = "血糖",
            unit = "mmol/L",
            limit = HealthLimit(
                lower = 3.6f,
            ),
            measures = listOf(
                HealthMeasure(
                    code = "canhou",
                    name = "餐后 2h",
                    limit = HealthLimit(
                        lower = 3.2f,
                        upper = 10f,
                    )
                ),
                HealthMeasure(
                    code = "kongfu",
                    name = "空腹 8h",
                    limit = HealthLimit(
                        lower = 3.2f,
                        upper = 7f,
                    )
                ),
            ),
        )
    }

    fun createHealthRecord(): HealthRecord {
        return HealthRecord(
            id = 0,
            typeId = 0,
            personId = 0,
            value = 12.1f,
            timestamp = System.currentTimeMillis(),
            measure = "canhou",
            tags = listOf("未打胰岛素", "含面食"),
            createdAt = System.currentTimeMillis(),
        )
    }
}