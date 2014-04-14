# ai2048

AI for 2048 game

I'm not very good at this game so I decided to make a program to play for me - and also because that's where the fun is.

## Installation

Download from http://github.com/ssoudan/ai2048

## Usage

Start the game:

    $ lein run

"Because it is not done if it is not tested" (Hey Romaric),
to run the tests:

    $ lein test

Run the coverage:

    $ lein cloverage

## Options

None so far.

## Examples

None so far, the output is not very pretty.

### Bugs

We don't do that anymore. That's deprecated! 

Well, if you are lucky enough to reach 2048, the program won't stop until it has played its hardcoded number of turns (4000?).
But who care? it's a victory.

Not implemented features:
- [ ] real score 
- [ ] integrate with the real game (unless you can share your score on facebook, that doesn't really count)
- [ ] follow the rule that prevent you to move in a direction that would cause no change in the board
- [ ] cost function can probably be optimized
- [ ] ranking function too
- [ ] can we prune some part of the tree of moves before evaluating them?
- [ ] tree construction does not take into account that the new elements can be 4s.

### Any Other Sections
### That You Think
### Might be Useful

## More seriously 

Here is the thing to read: http://en.wikipedia.org/wiki/Minimax 

I have adapted this a bit since the other player is not rational and the expected payoff of a move is not always limited to the leftover of the best move of other player.

And, in case, the source code contains some text/comment to help reading/fixing/tweaking it.
Have fun!

## License

Copyright © 2014 Sebastien Soudan

Distributed under the Eclipse Public License, the same as Clojure.
