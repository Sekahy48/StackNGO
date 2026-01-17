package identificators;

public interface GenericId extends Comparable<GenericId> {
    public int value();
    public int compareTo(GenericId other);
    public boolean equals(Object o);
    public int hashCode();
    public String toString();
}
