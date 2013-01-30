package Bots;

import Framework.Space;

public interface Bot {
    public Move determineMove(Space space, int shipId);
}
