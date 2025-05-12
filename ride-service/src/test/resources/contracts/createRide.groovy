package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should create a ride"
    request {
        method 'POST'
        url '/rides'
        body([
                "driverId" : $(anyUuid()),
                "pickup"   : $(anyNonBlankString())
        ])
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 201
        body([
                "rideId" : $(anyUuid()),
                "status" : "created"
        ])
        headers {
            contentType(applicationJson())
        }
    }
}


Contract.make {
    name "should create a ride when valid data and sufficient balance"
    request {
        method POST()
        url "/rides"
        body([
                passengerId: $(anyUuid()),
                originLatitude: 40.7128,
                originLongitude: -74.0060,
                destinationLatitude: 34.0522,
                destinationLongitude: -118.2437,
                carCategory: "ECONOMY",
                seatsCount: 2,
                paymentMethod: "CARD",
                promoCode: "TEST123"
        ])
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status CREATED()
        body([
                id: $(anyUuid()),
                passengerId: $(fromRequest().body("passengerId")),
                originAddress: "Valid Origin",
                destinationAddress: "Valid Destination",
                status: "REQUESTED",
                cost: 29.99,
                distance: 15.5,
                carCategory: "ECONOMY",
                seatsCount: 2,
                paymentMethod: "CARD",
                promoCode: "TEST123",
                createdAt: $(anyDateTime())
        ])
        headers {
            contentType(applicationJson())
        }
    }
    priority 1
    label("passenger_has_sufficient_balance")
}