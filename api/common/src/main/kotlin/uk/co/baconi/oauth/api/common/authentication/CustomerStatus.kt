package uk.co.baconi.oauth.api.common.authentication

data class CustomerStatus(
    val username: String,
    val state: CustomerState,
    // TODO - Move locked or even more of CustomerState into this level.
    //        active: boolean = !locked && !suspended && !closed
    //        locked: boolean
    //        closed: boolean
    //        suspended: boolean
    //        OR
    //        active: boolean = reasons.empty()
    //        inactiveReasons: Set<CustomerSate> = [ Locked, Suspended, Closed ]
    // TODO - Include created_at
    // TODO - Include modified_at
)
