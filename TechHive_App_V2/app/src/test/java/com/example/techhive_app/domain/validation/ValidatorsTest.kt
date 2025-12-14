package com.example.techhive_app.domain.validation

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ValidatorsTest {

    // Test de validación de email vacío: 1.Verifica que se retorne mensaje de error
    //                                    2.No se mockea nada porque es lógica pura
    //                                    3.Se espera un mensaje de validación

    @Test
    fun validateEmail_ok_devuelve_null() {
        val error = validateEmail("personal@gmail.com")
        assertNull(error)
    }

    @Test
    fun validateEmail_vacio_devuelve_error() {
        val error = validateEmail("")
        assertEquals("El email es obligatorio", error)
    }

    @Test
    fun validateEmail_formato_invalido_devuelve_error() {
        val error = validateEmail("correo-malo")
        assertEquals("Formato de email inválido", error)
    }

    @Test
    fun validateNameLettersOnly_ok_devuelve_null() {
        val error = validateNameLettersOnly("Samuel Fuenzalida", "Nombre")
        assertNull(error)
    }

    @Test
    fun validateNameLettersOnly_con_numeros_devuelve_error() {
        val error = validateNameLettersOnly("Samuel123", "Nombre")
        assertEquals("Solo letras y espacios", error)
    }

    @Test
    fun validateRut_vacio_devuelve_error() {
        val error = validateRut("")
        assertEquals("El RUT es obligatorio", error)
    }

    @Test
    fun validateRut_formato_invalido_devuelve_error() {
        val error = validateRut("12.34")
        assertEquals("Formato de RUT inválido", error)
    }

    @Test
    fun validateAddress_vacio_devuelve_error() {
        val error = validateAddress("")
        assertEquals("La dirección es obligatoria", error)
    }

    @Test
    fun validateAddress_corta_devuelve_error() {
        val error = validateAddress("abc")
        assertEquals("La dirección es muy corta", error)
    }

    @Test
    fun validatePhoneDigitsOnly_ok_devuelve_null() {
        val error = validatePhoneDigitsOnly("912345678")
        assertNull(error)
    }

    @Test
    fun validatePhoneDigitsOnly_no_numeros_devuelve_error() {
        val error = validatePhoneDigitsOnly("91234abcd")
        assertEquals("Solo números", error)
    }

    @Test
    fun validatePhoneDigitsOnly_largo_invalido_devuelve_error() {
        val error = validatePhoneDigitsOnly("123")
        assertEquals("Debe tener 9 dígitos", error)
    }

    @Test
    fun validateStrongPassword_ok_devuelve_null() {
        val error = validateStrongPassword("Aa1!aaaa")
        assertNull(error)
    }

    @Test
    fun validateStrongPassword_corta_devuelve_error() {
        val error = validateStrongPassword("Aa1!")
        assertEquals("Mínimo 8 caracteres", error)
    }

    @Test
    fun validateStrongPassword_sin_mayuscula_devuelve_error() {
        val error = validateStrongPassword("aa1!aaaa")
        assertEquals("Debe incluir una mayúscula", error)
    }

    @Test
    fun validateStrongPassword_sin_minuscula_devuelve_error() {
        val error = validateStrongPassword("AA1!AAAA")
        assertEquals("Debe incluir una minúscula", error)
    }

    @Test
    fun validateStrongPassword_sin_numero_devuelve_error() {
        val error = validateStrongPassword("Aa!aaaaa")
        assertEquals("Debe incluir un número", error)
    }

    @Test
    fun validateStrongPassword_sin_simbolo_devuelve_error() {
        val error = validateStrongPassword("Aa1aaaaa")
        assertEquals("Debe incluir un símbolo", error)
    }

    @Test
    fun validateStrongPassword_con_espacios_devuelve_error() {
        val error = validateStrongPassword("Aa1! aaaa")
        assertEquals("No debe contener espacios", error)
    }

    @Test
    fun validateConfirm_vacio_devuelve_error() {
        val error = validateConfirm("Aa1!aaaa", "")
        assertEquals("Confirma tu contraseña", error)
    }

    @Test
    fun validateConfirm_distinta_devuelve_error() {
        val error = validateConfirm("Aa1!aaaa", "Aa1!bbbb")
        assertEquals("Las contraseñas no coinciden", error)
    }

    @Test
    fun validateConfirm_igual_devuelve_null() {
        val error = validateConfirm("Aa1!aaaa", "Aa1!aaaa")
        assertNull(error)
    }
}
