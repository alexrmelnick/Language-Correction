package StateMachine;

public enum  State {
        START,      // Initial state
        PRONOUN,    // State for processing a pronoun token
        ARTICLE,    // State for article
        VERB,       // State for processing a verb token
        IS,
        ADJECTIVE,   // State for processing an adjective token
        NOUN,   // State for processing a noun
        ADVERB,
        DOT,
        CONJ,
        END,
        COMMA,
        THAT,
        IF,
        OF,
        CAN,
        NOT,
        DOES,
        NAN;   // State for processing a noun

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }

        public static State fromString(String text) {
            for (State state : State.values()) {
                if (state.toString().equalsIgnoreCase(text)) {
                    return state;
                }
            }
            return NAN;
        }

        public State next() {
            State[] colors = State.values();
            return colors[(this.ordinal()+1)%colors.length];
          }
        

        static public State first() {
            return State.values()[0];
        }

        public static char mapToChar(State element) {
            return (char) ('a' + element.ordinal());
        }
    
        // Function to perform reverse mapping from characters 'a' to 'z' to enum elements
        public static State mapToEnum(char character) {
            int index = character - 'a';
            if (index >= 0 && index < values().length) {
                return values()[index];
            } else {
                throw new IllegalArgumentException("Character is not in range 'a' to 'z'");
            }
        }

        // Function to perform reverse mapping from characters 'a' to 'z' to enum elements
        public static State mapToEnum(String character) {
            int index = character.charAt(0) - 'a';
            if (index >= 0 && index < values().length) {
                return values()[index];
            } else {
                throw new IllegalArgumentException("Character is not in range 'a' to 'z'");
            }
        }
        
}
