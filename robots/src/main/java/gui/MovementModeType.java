package gui;

public enum MovementModeType {
    MANUAL("Ручной"),
    PATROL_SQUARE("Патруль (Квадрат)"),
    PATROL_EIGHT("Патруль (Восьмёрка)"),
    CUSTOM_MOUSE("Пользовательский (мышь)"),
    LOAD_FROM_FILE("Загрузить маршрут");

    private final String displayName;

    MovementModeType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
