import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class baseTest {

    private Object objeto;

    @BeforeEach
    void setUp() {
        // inicialización común antes de cada test
        // objeto = new NombreDeLaClase(...);
    }

    @AfterEach
    void tearDown() {
        // limpieza si hace falta (normalmente vacío)
    }

    @Test
    void testAlgo() {
        // given
        // when
        // then
    }
}
