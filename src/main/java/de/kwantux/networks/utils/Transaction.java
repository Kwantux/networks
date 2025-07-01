package de.kwantux.networks.utils;

import de.kwantux.networks.component.module.BaseModule;

public record Transaction(BaseModule source, BaseModule target, PositionedItemStack stack) {
    public boolean isValid() {
        return source != null && target != null && stack != null && stack.inventory().equals(source.inventory());
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return stack.equals(that.stack);
    }

    public int hashCode() {
        return stack.hashCode();
    }

    public String toString() {
        return "Transaction{" +
                "source=" + source.origin()  +
                ", target=" + target.origin() +
                ", stack=" + stack.getI18NDisplayName() +
                '}';
    }
}
