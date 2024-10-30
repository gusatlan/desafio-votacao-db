package br.com.cooperativa.votacao.util

import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import java.util.*

private fun invalidCpfList(): Collection<String> {
    return listOf(
        "00000000000",
        "11111111111",
        "22222222222",
        "33333333333",
        "44444444444",
        "55555555555",
        "66666666666",
        "77777777777",
        "88888888888",
        "99999999999"
    )
}

fun validateCpf(text: String): Boolean {
    val number = cleanCode(text)
    val numbers = Arrays.stream(number.toCharArray().toTypedArray()).map(Char::toString).map(String::toInt).toList()
    var valid = number.length == 11 && !invalidCpfList().contains(text)

    if (valid) {
        val digit1 = numbers[9]
        val digit2 = numbers[10]
        var multiplier = 11
        var sum1 = 0
        var sum2 = 0

        for ((index, value) in numbers.withIndex()) {
            if (index <= 8) {
                sum1 += value * (multiplier - 1)
            }

            if (index <= 9) {
                sum2 += value * multiplier
            }

            multiplier--

            if (multiplier < 2) {
                break
            }
        }

        var remainder1 = 11 - sum1 % 11
        var remainder2 = 11 - sum2 % 11

        if (remainder1 == 10) {
            remainder1 = 0
        }

        if (remainder2 == 10) {
            remainder2 = 0
        }

        valid = remainder1 == digit1 && remainder2 == digit2
    }

    return valid
}

fun <T: Any> validate(obj: T): Collection<ConstraintViolation<T>> {
    return try {
        Validation.buildDefaultValidatorFactory().use {
            logger.info("validate() validating $obj")
            it.validator.validate(obj).also { violations ->
                logger.info("validate(): validated $obj with violations: [${violations.joinToString()}]")
            }
        }
    } catch (e:Exception) {
        logger.error("validate(): Error on validate", e)
        throw e
    }
}

fun <T: Any> validateList(obj: T): Collection<String> {
    return validate(obj)
        .stream()
        .map {
            it.message
        }
        .distinct()
        .toList()
}
