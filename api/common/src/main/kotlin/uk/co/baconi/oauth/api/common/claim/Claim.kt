package uk.co.baconi.oauth.api.common.claim

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
 */
@Serializable
enum class Claim(internal val value: String) {

    /**
     * Identifier for the End-User at the Issuer.
     */
    @SerialName("sub") Subject("sub"),

    /**
     * End-User's full name in displayable form including all name parts, possibly including titles and suffixes,
     * ordered according to the End-User's locale and preferences.
     */
    //@SerialName("name") Name("name"),

    /**
     * Given name(s) or first name(s) of the End-User. Note that in some cultures, people can have multiple given names;
     * all can be present, with the names being separated by space characters.
     */
    //@SerialName("given_name") GivenName("give_name"),

    /**
     * Surname(s) or last name(s) of the End-User. Note that in some cultures, people can have multiple family names or
     * no family name; all can be present, with the names being separated by space characters.
     */
    //@SerialName("family_name") FamilyName("family_name"),

    /**
     * Middle name(s) of the End-User. Note that in some cultures, people can have multiple middle names; all can be
     * present, with the names being separated by space characters. Also note that in some cultures, middle names are
     * not used.
     */
    //@SerialName("middle_name") MiddleName("middle_name"),

    /**
     * Casual name of the End-User that may or may not be the same as the given_name. For instance, a nickname value of
     * Mike might be returned alongside a given_name value of Michael.
     */
    //@SerialName("nickname") Nickname("nickname"),

    /**
     * Shorthand name by which the End-User wishes to be referred to at the RP, such as janedoe or j.doe. This value
     * MAY be any valid JSON string including special characters such as @, /, or whitespace. The RP MUST NOT rely upon
     * this value being unique.
     */
    //@SerialName("preferred_username") PreferredUserName("preferred_username"),

    /**
     * URL of the End-User's profile page. The contents of this Web page SHOULD be about the End-User.
     */
    //@SerialName("profile") ProfileUrl("profile"),

    /**
     * URL of the End-User's profile picture. This URL MUST refer to an image file (for example, a PNG, JPEG, or GIF
     * image file), rather than to a Web page containing an image. Note that this URL SHOULD specifically reference
     * a profile photo of the End-User suitable for displaying when describing the End-User, rather than an arbitrary
     * photo taken by the End-User.
     */
    //@SerialName("picture") PictureUrl("picture"),

    /**
     * End-User's preferred e-mail address. Its value MUST conform to the RFC 5322
     * [https://www.rfc-editor.org/rfc/rfc5322.txt] addr-spec syntax. The RP MUST NOT rely upon this value being unique.
     */
    //@SerialName("email") Email("email"),

    /**
     * True if the End-User's e-mail address has been verified; otherwise false. When this Claim Value is true, this
     * means that the OP took affirmative steps to ensure that this e-mail address was controlled by the End-User at
     * the time the verification was performed. The means by which an e-mail address is verified is context-specific,
     * and dependent upon the trust framework or contractual agreements within which the parties are operating.
     */
    //@SerialName("email_verified") EmailVerified("email_verified"),

    /**
     * End-User's birthday, represented as an ISO 8601:2004 [ISO8601‑2004] YYYY-MM-DD format. The year MAY be 0000,
     * indicating that it is omitted. To represent only the year, YYYY format is allowed. Note that depending on the
     * underlying platform's date related function, providing just year can result in varying month and day, so the
     * implementers need to take this factor into account to correctly process the dates.
     */
    //@SerialName("birthdate") Birthdate("birthdate"),

    /**
     * String from zoneinfo [http://www.twinsun.com/tz/tz-link.htm] time zone database representing the End-User's
     * time zone. For example, Europe/Paris or America/Los_Angeles.
     */
    //@SerialName("zoneinfo") ZoneInfo("zoneinfo"),

    /**
     * End-User's locale, represented as a RFC5646 [https://tools.ietf.org/html/rfc5646] language tag. This is typically
     * an ISO 639-1 Alpha-2 [ISO639‑1] language code in lowercase and an ISO 3166-1 Alpha-2 [ISO3166‑1] country code in
     * uppercase, separated by a dash. For example, en-US or fr-CA. As a compatibility note, some implementations have
     * used an underscore as the separator rather than a dash, for example, en_US; Relying Parties MAY choose to accept
     * this locale syntax as well.
     */
    //@SerialName("locale") Locale("locale"),

    /**
     * End-User's preferred telephone number. E.164 [https://www.itu.int/rec/T-REC-E.164-201011-I/en] is RECOMMENDED as
     * the format of this Claim, for example, +1 (425) 555-1212 or +56 (2) 687 2400. If the phone number contains an
     * extension, it is RECOMMENDED that the extension be represented using the RFC 3966
     * [https://www.rfc-editor.org/rfc/rfc3966.txt] extension syntax, for example, +1 (604) 555-1234;ext=5678.
     */
    //@SerialName("phone_number") PhoneNumber("phone_number"),

    /**
     * True if the End-User's phone number has been verified; otherwise false. When this Claim Value is true, this means
     * that the OP took affirmative steps to ensure that this phone number was controlled by the End-User at the time
     * the verification was performed. The means by which a phone number is verified is context-specific, and dependent
     * upon the trust framework or contractual agreements within which the parties are operating. When true, the
     * phone_number Claim MUST be in E.164 format and any extensions MUST be represented in RFC 3966 format.
     */
    //@SerialName("phone_number_verified") PhoneNumberVerified("phone_number_verified"),

    /**
     * End-User's preferred postal address. The value of the address member is a JSON structure containing some or all
     * of the members defined in Section 5.1.1 [https://openid.net/specs/openid-connect-core-1_0.html#AddressClaim]
     */
    //@SerialName("address") Address("address"),

    /**
     * Time the End-User's information was last updated. Its value is a JSON number representing the number of seconds
     * from 1970-01-01T0:0:0Z as measured in UTC until the date/time.
     */
    @SerialName("updated_at") UpdatedAt("updated_at"),

    ;

    companion object {
        fun fromValue(value: String): Claim = checkNotNull(fromValueOrNull(value)) { "No such Claim [$value]" }
        fun fromValueOrNull(value: String): Claim? = values().firstOrNull { claim -> claim.value == value }
    }
}