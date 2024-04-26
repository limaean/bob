package wordle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    @Test
    void play() {
        Wordle game = new Wordle();
        String target = game.getRandomTargetWord();
        game.play(target);
    }
}