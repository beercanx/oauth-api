package uk.co.baconi.oauth.api.customer

enum class CustomerState {

    // Normal state of a customer.
    Active,

    // Flagged as requiring administration review.
    Suspended,

    // Administration has decided to prevent login, prior to being deleted.
    Closed,

    // Administration has decided customer must change their password, prior to being active again.
    ChangePassword
}