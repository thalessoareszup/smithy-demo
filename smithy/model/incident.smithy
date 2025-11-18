$version: "2"

namespace com.stackspot.labs

use aws.protocols#restJson1

@title("Incident Hub")
@restJson1
@cors
service IncidentHub {
    version: "2025-11-10"
    resources: [
        Incident
    ]
}

enum IncidentStatus {
    IN_PROGRESS
    COMPLETED
}

resource Incident {
    identifiers: {
        id: IncidentId
    }
    properties: {
        description: String
        status: IncidentStatus
    }
    read: GetIncident
    create: CreateIncident
    list: ListIncidents
}

@idempotent
@http(method: "POST", uri: "/incident")
operation CreateIncident {
    input := for Incident {
        @required
        $description
    }

    output: IncidentSummary
}

@readonly
@http(method: "GET", uri: "/incident/{id}")
operation GetIncident {
    input := for Incident {
        @httpLabel
        @required
        $id
    }

    output: IncidentSummary

    errors: [
        IncidentNotFound
    ]
}

@readonly
@http(method: "GET", uri: "/incident")
operation ListIncidents {
    output := {
        @required
        incidents: IncidentList
    }
}

structure IncidentSummary for Incident {
    @required
    $id

    @required
    $description

    @required
    $status
}

list IncidentList {
    member: IncidentSummary
}

@httpError(404)
@error("client")
structure IncidentNotFound {
    message: String
    id: IncidentId
}

@length(min: 1, max: 8)
@pattern("^[A-Z]{3}-\\d{4}$")
string IncidentId
