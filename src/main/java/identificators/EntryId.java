package identificators;
  

public final class EntryId implements GenericId {

    private final int value;

    public EntryId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("EntryId must be positive");
        }
        this.value = value;
    }

    public int value() {
        return value;
    }

    @Override
    public int compareTo(GenericId other) {
        return Integer.compare(this.value, other.value());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntryId)) return false;
        EntryId entryId = (EntryId) o;
        return value == entryId.value;
    } 
    
    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }


    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
