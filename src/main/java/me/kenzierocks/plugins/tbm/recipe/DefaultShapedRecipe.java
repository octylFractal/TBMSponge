package me.kenzierocks.plugins.tbm.recipe;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import me.kenzierocks.plugins.tbm.Shortcuts;

public final class DefaultShapedRecipe implements ShapedRecipe.SingleOutput {

    public static interface BoxBuilder<SUBTYPE extends BoxBuilder<SUBTYPE>> {

        SUBTYPE link(char character, ItemStack stack);

        SUBTYPE result(ItemStack stack);

        SUBTYPE duplicate();

        DefaultShapedRecipe build();

    }

    private static abstract class MixinBoxBuilder<SUBTYPE extends MixinBoxBuilder<SUBTYPE>>
            implements BoxBuilder<SUBTYPE> {

        protected final Map<Character, ItemStack> links = new HashMap<>();
        protected final ItemStack[][] layout = createLayout();
        protected ItemStack result;

        {
            checkLayout();
            link(' ', Shortcuts.singleStackOfItem(ItemTypes.NONE));
        }

        protected abstract ItemStack[][] createLayout();

        private void checkLayout() {
            checkNotNull(this.layout, "layout cannot be null");
            checkArgument(this.layout.length != 0, "Cannot have 0 rows");
            int cols = this.layout[0].length;
            checkArgument(cols != 0, "Cannot have 0 columns");
            for (ItemStack[] row : this.layout) {
                checkArgument(cols == row.length, "non-rectangular array");
            }
        }

        protected abstract SUBTYPE $this();

        protected abstract SUBTYPE createNew();

        @Override
        public SUBTYPE link(char character, ItemStack stack) {
            checkNotNull(stack, "linked stack cannot be null");
            this.links.put(character, stack);
            return $this();
        }

        @Override
        public SUBTYPE result(ItemStack result) {
            checkNotNull(result, "result cannot be null");
            this.result = result;
            return $this();
        }

        @Override
        public DefaultShapedRecipe build() {
            // just in case idiots replace it later...
            checkLayout();
            checkNotNull(this.result, "result was not set");
            return new DefaultShapedRecipe(this.layout, this.result);
        }

        @Override
        public SUBTYPE duplicate() {
            SUBTYPE n = createNew();
            n.links.putAll(this.links);
            n.result(this.result);
            for (int i = 0; i < this.layout.length; i++) {
                n.layout[i] = this.layout[i];
            }
            return n;
        }

    }

    public static final class Box2By2Builder
            extends MixinBoxBuilder<Box2By2Builder> {

        public static Box2By2Builder start(ItemStack result) {
            return new Box2By2Builder().result(result);
        }

        private Box2By2Builder() {
        }

        public Box2By2Builder row1(char c1, char c2) {
            ItemStack[] row1 = this.layout[0];
            row1[0] = this.links.get(c1);
            row1[1] = this.links.get(c2);
            return this;
        }

        public Box2By2Builder row2(char c1, char c2) {
            ItemStack[] row2 = this.layout[1];
            row2[0] = this.links.get(c1);
            row2[1] = this.links.get(c2);
            return this;
        }

        @Override
        protected ItemStack[][] createLayout() {
            return new ItemStack[2][2];
        }

        @Override
        protected Box2By2Builder $this() {
            return this;
        }

        @Override
        protected Box2By2Builder createNew() {
            return new Box2By2Builder();
        }

    }

    public static final class Box3By3Builder
            extends MixinBoxBuilder<Box3By3Builder> {

        public static Box3By3Builder start(ItemStack result) {
            return new Box3By3Builder().result(result);
        }

        private Box3By3Builder() {
        }

        public Box3By3Builder row1(char c1, char c2, char c3) {
            ItemStack[] row1 = this.layout[0];
            row1[0] = this.links.get(c1);
            row1[1] = this.links.get(c2);
            row1[2] = this.links.get(c3);
            return this;
        }

        public Box3By3Builder row2(char c1, char c2, char c3) {
            ItemStack[] row2 = this.layout[1];
            row2[0] = this.links.get(c1);
            row2[1] = this.links.get(c2);
            row2[2] = this.links.get(c3);
            return this;
        }

        public Box3By3Builder row3(char c1, char c2, char c3) {
            ItemStack[] row3 = this.layout[2];
            row3[0] = this.links.get(c1);
            row3[1] = this.links.get(c2);
            row3[2] = this.links.get(c3);
            return this;
        }

        @Override
        protected ItemStack[][] createLayout() {
            return new ItemStack[3][3];
        }

        @Override
        protected Box3By3Builder $this() {
            return this;
        }

        @Override
        protected Box3By3Builder createNew() {
            return new Box3By3Builder();
        }

    }

    private final ItemStack[][] layout;
    private final ItemStack result;
    private final transient int rows;
    private final transient int cols;

    private DefaultShapedRecipe(ItemStack[][] layout, ItemStack result) {
        this.layout = layout;
        this.result = result;
        this.rows = this.layout.length;
        this.cols = this.layout[0].length;
    }

    @Override
    public int getRows() {
        return this.rows;
    }

    @Override
    public int getCols() {
        return this.cols;
    }

    @Override
    public ItemStack getStackAt(int r, int c) {
        return this.layout[r][c];
    }

    @Override
    public ItemStack getOutput() {
        return this.result;
    }

}