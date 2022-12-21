package dev.toma.questing.utils;

public enum Alignment {

    NONE(AlignmentFunction.noCenter(), AlignmentFunction.noCenter()),
    HORIZONTAL(RenderUtils::getCenter, AlignmentFunction.noCenter()),
    VERTICAL(AlignmentFunction.noCenter(), RenderUtils::getCenter),
    CENTER(RenderUtils::getCenter, RenderUtils::getCenter);

    private final AlignmentFunction horizontal;
    private final AlignmentFunction vertical;

    Alignment(AlignmentFunction horizontal, AlignmentFunction vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public float getHorizontalPosition(float start, float size, float objectSize) {
        return horizontal.computePosition(start, size, objectSize);
    }

    public float getVerticalPosition(float start, float size, float objectSize) {
        return vertical.computePosition(start, size, objectSize);
    }

    @FunctionalInterface
    private interface AlignmentFunction {

        float computePosition(float start, float size, float objSize);

        static AlignmentFunction noCenter() {
            return (start, size, objSize) -> start;
        }
    }
}
