package ru.rtuitlab.itlab.data.repository

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.rtuitlab.itlab.Generator
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.local.AppDatabase
import ru.rtuitlab.itlab.data.local.reports.ReportsDao
import ru.rtuitlab.itlab.data.remote.MockReportsApi
import ru.rtuitlab.itlab.data.remote.api.reports.ReportsApi
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalaryRequest
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class ReportsRepositoryImplTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var testDispatcher: TestDispatcher

    @Inject
    lateinit var repo: ReportsRepositoryImpl

    @Inject
    lateinit var api: ReportsApi

    @Inject
    lateinit var db: AppDatabase

    lateinit var dao: ReportsDao

    @Before
    fun setUp() {
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)

        dao = db.reportsDao

        runBlocking {
            db.usersDao.upsertAll(Generator.users)
        }
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
    }

    @Test
    fun updateUserReports() = runTest {
        val result = repo.updateUserReports() as Resource.Success

        assertThat(dao.getReports().first().map {
            it.report
        }).containsExactlyElementsIn(
            result.data.map {
                it.toReportEntity()
            }
        )
    }

    @Test
    fun updateReportSalaries() = runTest {
        repo.updateReports()
        val r = repo.updateReportSalaries("0") as Resource.Success

        assertThat(dao.getReportsSalary()).containsExactlyElementsIn(
            r.data
        )
    }

    @Test
    fun updateReports() = runTest {
        val r = repo.updateReports() as Resource.Success

        assertThat(dao.getReports().first().map {
            it.report
        }).containsExactlyElementsIn(
            r.data.map {
                it.toReportEntity()
            }
        )
    }

    @Test
    fun createReport() = runTest {
        val r = repo.createReport(
            implementerId = "3",
            name = "New report",
            text = "New text"
        ) as Resource.Success

        assertThat(
            dao.getReports().first().map {
                it.report
            }
        ).contains(
            r.data.toReportEntity()
        )
    }

    @Test
    fun updateReport() = runTest {
        (api as MockReportsApi).mutateReport("3")
        val r = repo.updateReport("3") as Resource.Success

        assertThat(dao.getReports().first().map {
            it.report
        })
            .contains(
                r.data.toReportEntity()
            )
    }

    @Test
    fun updateSalaryForUser() = runTest {
        repo.updateReports()
        val r = repo.updateSalaryForUser("2") as Resource.Success

        assertThat(dao.getReportsSalary()).containsExactlyElementsIn(r.data)
    }

    @Test
    fun editReportSalary() = runTest {
        repo.updateReports()
        val r = repo.editReportSalary("2", ReportSalaryRequest(13, "blah")) as Resource.Success

        assertThat(dao.getReportsSalary()).contains(r.data)
    }
}