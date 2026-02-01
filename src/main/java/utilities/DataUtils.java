package utilities;

import java.util.List;
import java.util.function.Function;

import mvc.model.entries.ItemIdStack;

public class DataUtils {

    public static <T> List<T> naiveDeepCopy(List<T> original, Function<T, T> copier) {
        return original.stream().map(copier).toList();
    }


    
}
