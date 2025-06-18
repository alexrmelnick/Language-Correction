package util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import StateMachine.State;

public class StringToList {

    public static List<State> split(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }
        return Arrays.stream(input.split("\\."))
                     //.map(State::fromString)
                     .map(State::mapToEnum)
                     .collect(Collectors.toList());
    }
}
