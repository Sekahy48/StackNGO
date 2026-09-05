package utilities;

import javafx.scene.image.Image;

/**
 * Gestiona el tema activo (dark/light) y proporciona rutas de iconos temáticos.
 * Los iconos con variante de tema están en images/dark/ e images/light/.
 * Los que no tienen variante se cargan desde images/ directamente.
 */
public class ThemeManager {

    public enum Theme { DARK, LIGHT }

    private static Theme currentTheme = Theme.DARK;

    public static Theme getCurrentTheme() {
        return currentTheme;
    }

    public static void setCurrentTheme(Theme theme) {
        currentTheme = theme;
    }

    public static boolean isDark() {
        return currentTheme == Theme.DARK;
    }

    /**
     * Devuelve la ruta del icono según el tema activo.
     * Ejemplo: getThemedIcon("papelera.png") → "images/dark/papelera.png"
     */
    public static String getThemedIcon(String iconName) {
        String folder = (currentTheme == Theme.DARK) ? "dark" : "light";
        return "images/" + folder + "/" + iconName;
    }

    /**
     * Carga directamente una Image con el icono temático.
     */
    public static Image getThemedImage(String iconName) {
        return new Image(getThemedIcon(iconName));
    }

    /**
     * Devuelve la ruta del CSS según el tema activo.
     */
    public static String getThemeCssPath() {
        return (currentTheme == Theme.DARK) ? "css/dark-theme.css" : "css/light-theme.css";
    }
}
