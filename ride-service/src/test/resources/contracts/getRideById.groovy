package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should create a ride"
    request {
        method 'POST'
        url '/rides'
        body([
                "driverId" : $(regex('\\d+')),
                "pickup"   : $(anyNonBlankString())
        ])
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 201
        body([
                "rideId" : $(regex('\\d+')),
                "status" : "created"
        ])
        headers {
            contentType(applicationJson())
        }
    }
}


Contract.make {
    name "should get ride by ID"
    request {
        method GET()
        url $(regex('/rides/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}'))
    }
    response {
        status OK()
        body([
                id: $(fromRequest().path(1)),
                passengerId: $(anyUuid()),
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
    priority 2
    label("ride_exists")
}