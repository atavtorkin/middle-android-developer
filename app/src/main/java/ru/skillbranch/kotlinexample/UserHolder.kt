package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.util.regex.Pattern

/**
 * Created on 2019-12-10.
 *
 * @author Alexander Tavtorkin (av.tavtorkin@gmail.com)
 */
object UserHolder {
    private val PHONE = Pattern.compile( // sdd = space, dot, or dash
        "(\\+[0-9]+[\\- \\.]*)?" // +<digits><sdd>*
                + "(\\([0-9]+\\)[\\- \\.]*)?" // (<digits>)<sdd>*
                + "([0-9][0-9\\- \\.]+[0-9])"
    )

    private val map = mutableMapOf<String, User>()

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User {
        if (map.contains(email.toLowerCase())) throw IllegalArgumentException("A user with this email already exists")
        return User.makeUser(fullName, email = email, password = password)
            .also { user -> map[user.login] = user }
    }

    fun loginUser(login: String, password: String): String? {
        var _login = login.trim()

        if (isPhoneNumber(login)) _login = login.replace("[^+\\d]".toRegex(), "")

        return map[_login]?.run {
            if (checkPassword(password)) this.userInfo
            else null
        }
    }

    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        validatePhoneNumber(rawPhone)

        val user = User.makeUser(fullName, phone = rawPhone)

        if (map.contains(user.login)) throw IllegalArgumentException("A user with this phone already exists")
        return user.also { map[user.login] = it }
    }

    fun requestAccessCode(login: String) {
        var _login = login.trim()

        if (isPhoneNumber(login)) _login = login.replace("[^+\\d]".toRegex(), "")
        map[_login]?.run {
            this.requestAccessCode()
        }

    }

    private fun isPhoneNumber(login: String): Boolean {
        return PHONE.matcher(login).matches()
    }

    private fun validatePhoneNumber(rawPhone: String) {
        val phone = rawPhone.trim()
        val matcher = PHONE.matcher(phone)
        if (!(matcher.matches())) throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
    }
}
