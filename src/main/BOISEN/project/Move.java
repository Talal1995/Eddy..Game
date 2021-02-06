package project;

public enum Move {

        UP         { public Move  opposite(){return Move.DOWN;        };},
        RIGHT     { public Move  opposite(){return Move.LEFT;        };},
        DOWN     { public Move opposite(){return Move.UP;        };},
        LEFT     { public Move opposite(){return Move.RIGHT;        };},
        NEUTRAL    { public Move opposite(){return Move.NEUTRAL;    };};

        public abstract Move opposite();

};


