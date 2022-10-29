package ru.rtuitlab.itlab.data.repository

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
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
import ru.rtuitlab.itlab.data.local.events.EventsDao
import ru.rtuitlab.itlab.data.local.events.models.*
import ru.rtuitlab.itlab.data.local.events.models.salary.EventSalaryEntity
import ru.rtuitlab.itlab.data.remote.api.events.models.EventDetailDto
import ru.rtuitlab.itlab.data.remote.api.events.models.EventPlaceSalary
import ru.rtuitlab.itlab.data.remote.api.events.models.EventShiftSalary
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class EventsRepositoryImplTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var testDispatcher: TestDispatcher

    @Inject
    lateinit var eventsRepo: EventsRepositoryImpl

    @Inject
    lateinit var db: AppDatabase

    lateinit var dao: EventsDao

    val eventEntities = Generator.eventEntities.toList()
    val eventTypes = Generator.eventTypes.toList()

    @Before
    fun setUp() {
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)

        dao = db.eventsDao

        runBlocking {
            dao.insertEventTypes(eventTypes)
            dao.insertEvents(eventEntities)
            db.usersDao.insertAll(
                Generator.users
            )
        }
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
    }

    @Test
    fun updateEvents() = runTest {
        val result = eventsRepo.updateEvents() as Resource.Success
        assertThat(
            result.data.map {
                EventWithType(
                    event = it.toEventEntity(),
                    type = it.eventType
                )
            }
        ).containsExactlyElementsIn(eventsRepo.getEvents().first())
    }

    @Test
    fun updateUserEvents() = runTest {
        val result = eventsRepo.updateUserEvents(2.toString()) as Resource.Success
        assertThat(
            dao.getUserEvents().first().map { it.toUserEventModel() }
        ).containsAtLeastElementsIn(
            result.data
        )
    }

    @Test
    fun fetchEvent() = runTest {
        val result = eventsRepo.fetchEvent("0") as Resource.Success

        val expected = EventWithShiftsAndSalary(
            event = result.data.toEventDetailEntity(),
            shifts = result.data.extractShiftEntities().mapIndexed { i, shift ->
                ShiftWithPlacesAndSalary(
                    shift = shift,
                    places = result.data.shifts[i].places.map { place ->
                        PlaceWithUsersAndSalary(
                            place = place.toPlaceEntity(shift.id),
                            usersWithRoles = (place.participants + place.invited + place.wishers + place.unknowns).map {
                                UserWithRole(
                                    userRole = it.toUserRole(
                                        type = UserParticipationType.PARTICIPANT,
                                        placeId = place.id
                                    ),
                                    role = it.eventRole
                                )
                            },
                            salary = EventPlaceSalary(
                                placeId = place.id,
                                count = 500,
                                description = ""
                            )
                        )
                    },
                    salary = EventShiftSalary(
                        shiftId = shift.id,
                        count = 1000,
                        description = "whatever"
                    )
                )
            },
            salary = EventSalaryEntity(
                eventId = "0",
                createdAt = "2022-07-25T11:07:44Z",
                authorId = "1",
                modificationDate = "2022-07-25T11:07:44Z",
                count = 2000,
                description = "Держи свои копкейки"
            )
        )

        val actual = eventsRepo.getEventDetail("0").first()

        assertWithMessage(
            "Expected: \n$expected \n\n Actual: \n${actual.toString()}"
        ).that(actual).isEqualTo(expected)
    }

    @Test
    fun updateEventSalary() = runTest {
        eventsRepo.fetchEvent("2")
        val result = eventsRepo.updateEventSalary("2") as Resource.Success
        val event = eventsRepo.getEventDetail("2").first()!!
        assertThat(
            event.salary
        ).isEqualTo(
            result.data.toEventSalaryEntity()
        )
    }

    @Test
    fun updateEventRoles() = runTest {
        val result = eventsRepo.updateEventRoles() as Resource.Success

        assertThat(
            dao.getEventRoles()
        ).containsExactlyElementsIn(
            result.data
        )
    }

    @Test
    fun updateAllDetails() = runTest {
        var details: List<EventDetailDto> = ArrayList()
        eventsRepo.updateEvents().handle(
            onSuccess = {
                details = it.map {
                    (eventsRepo.fetchEvent(it.id) as Resource.Success).data
                }
            }
        )

        assertThat(
            dao.getEventDetail("3").first()!!.event
        ).isIn(details.map { it.toEventDetailEntity() })
    }

    @Test
    fun updateInvitations() = runTest {
        eventsRepo.updateEvents().handle(
            onSuccess = {
                it.forEach {
                    eventsRepo.fetchEvent(it.id)
                }
            }
        )
        val result = eventsRepo.updateInvitations() as Resource.Success

        assertThat(
            dao.getInvitations().first()
        ).containsExactlyElementsIn(
            result.data.map {
                it.toInvitationEntity()
            }
        )
    }
}