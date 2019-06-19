package amazons;

import static amazons.Move.mv;

/** A Player that takes input as text commands from the standard input.
 *  @author Yuan Xie.
 */
class TextPlayer extends Player {

    /** A new TextPlayer with no piece or controller (intended to produce
     *  a template). */
    TextPlayer() {
        this(null, null);
    }

    /** A new TextPlayer playing PIECE under control of CONTROLLER. */
    private TextPlayer(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new TextPlayer(piece, controller);
    }

    @Override
    String myMove() {
        while (true) {
            String line = _controller.readLine();
            if (line == null) {
                return "quit";
            } else if (Move.isGrammaticalMove(line)) {
                Move c = mv(line);
                if (c != null && _controller.board().isLegal(c.from())
                        && _controller.board().isUnblockedMove(c.from(),
                        c.to(), null)
                        && _controller.board().isUnblockedMove(c.to(),
                        c.spear(), c.from())) {
                    return line;
                } else {
                    _controller.reportError("Invalid move. "
                            + "Please try again.");
                    continue;
                }
            } else {
                return line;
            }
        }
    }
}
