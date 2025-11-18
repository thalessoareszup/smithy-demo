package com.stackspot.labs.incidenthub

import com.stackspot.labs.model.*
import com.stackspot.labs.service.CreateIncidentOperation
import com.stackspot.labs.service.GetIncidentOperation
import com.stackspot.labs.service.IncidentHub
import com.stackspot.labs.service.ListIncidentsOperation
import software.amazon.smithy.java.server.RequestContext
import software.amazon.smithy.java.server.Server
import java.net.URI
import java.util.concurrent.ExecutionException
import kotlin.random.Random

fun main() {
    val server = Server.builder()
        .endpoints(URI.create("http://0.0.0.0:8080"))
        .addService(
            IncidentHub.builder()
                .addCreateIncidentOperation(CreateIncident())
                .addGetIncidentOperation(GetIncident())
                .addListIncidentsOperation(ListIncidents())
                .build()

        )
        .build()

    server.start()
    try {
        Thread.currentThread().join()
    } catch (e: InterruptedException) {
        try {
            server.shutdown().get()
        } catch (ex: InterruptedException) {
            throw RuntimeException(ex)
        } catch (ex: ExecutionException) {
            throw RuntimeException(ex)
        }
    }
}

val theBestKeyValueStoreOfAllTime: MutableMap<String, Incident> = mutableMapOf()

fun generateIncidentId(maxAttempts: Int = 10): String {
    repeat(maxAttempts) {
        val letters = (1..3)
            .map { ('A'..'Z').random() }
            .joinToString("")
        val digits = (1..4)
            .map { Random.nextInt(0, 10) }
            .joinToString("")
        val id = "$letters-$digits"
        if (!theBestKeyValueStoreOfAllTime.containsKey(id)) {
            return id
        }
    }
    throw IllegalStateException("Could not generate a unique incident ID after $maxAttempts attempts.")
}

data class Incident(
    val id: String,
    val description: String,
    val status: IncidentStatus
)

class CreateIncident : CreateIncidentOperation {
    override fun createIncident(
        input: CreateIncidentInput, ctx: RequestContext?
    ): CreateIncidentOutput {
        val id = generateIncidentId()
        val incident = Incident(id, input.description(), IncidentStatus.IN_PROGRESS)
        theBestKeyValueStoreOfAllTime[id] = incident
        return CreateIncidentOutput.builder()
            .id(id)
            .description(incident.description)
            .status(IncidentStatus.IN_PROGRESS)
            .build()
    }
}

class GetIncident : GetIncidentOperation {
    override fun getIncident(input: GetIncidentInput, ctx: RequestContext?): GetIncidentOutput {
        val incident =
            theBestKeyValueStoreOfAllTime[input.id()] ?: throw IncidentNotFound.builder().id(input.id()).build()
        return GetIncidentOutput.builder()
            .id(incident.id)
            .description(incident.description)
            .build()
    }
}

class ListIncidents : ListIncidentsOperation {
    override fun listIncidents(input: ListIncidentsInput, ctx: RequestContext?): ListIncidentsOutput {
        val incidents = theBestKeyValueStoreOfAllTime.values
        return ListIncidentsOutput.builder().incidents(
            incidents.map { IncidentSummary.builder()
                .id(it.id)
                .description(it.description)
                .status(it.status)
                .build()
            }
        ).build()
    }
}
