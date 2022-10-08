package uk.co.baconi.oauth.api.common.crypto

import org.bouncycastle.crypto.generators.OpenBSDBCrypt

actual object CheckPassword {
    actual val checkHashedPassword: CheckHashedPassword = OpenBSDBCrypt::checkPassword
}