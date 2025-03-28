package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "should change ride status to ACCEPTED"
    request {
        method PUT()
        url $(regex('/rides/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/status'))
        body([
                status: "ACCEPTED"
        ])
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status OK()
        body([
                id: $(fromRequest().path(1)),
                status: "ACCEPTED"
        ])
        headers {
            contentType(applicationJson())
        }
    }
    // CORRECTED: Use "label" instead of "given"
    label 'ride_exists_with_status_REQUESTED'
}