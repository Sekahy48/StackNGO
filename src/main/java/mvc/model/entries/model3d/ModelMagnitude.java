package mvc.model.entries.model3d;

/**
 * Nombre con el que una magnitud se guarda y viaja: el componente y el campo separados por
 * un punto.
 *
 * <p>Va cualificada porque dos componentes distintos pueden tener un campo que se llame
 * igual, y quien lea el valor necesita saber en cual mirar. La regla vive aqui, en el
 * dominio, porque la comparten el editor —que ofrece las magnitudes— y la exportacion —que
 * las escribe y comprueba que sigan existiendo—, y son partes que no deberian conocerse
 * entre si para ponerse de acuerdo en algo tan basico.</p>
 */
public final class ModelMagnitude {

    public static final String SEPARATOR = ".";

    private ModelMagnitude() {
        // No se instancia. Esto no es un objeto, es una herramienta.
    }

    /**
     * @param componentName nombre del componente
     * @param fieldName nombre del campo dentro de ese componente
     * @return nombre cualificado de la magnitud
     */
    public static String qualify(String componentName, String fieldName) {
        return componentName + SEPARATOR + fieldName;
    }
}
