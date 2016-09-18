/*
 * @formatter:off
 * 
 * TODO: Handle groups of stones properly during capture test.
 * TODO: Handle groups of stones properly after capture test.
 * TODO: Implement a legality test for the Ko rule.
 * 
 * @formatter:on
 */

import java.awt.Color;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GoGame extends Thread implements Serializable, Cloneable
{
    public static enum ACTION_TYPE
    {
        MOVE, PASS, RESIGN
    }
    
    public final static Integer[] DEFAULT_SIZES    = {
        19, 13, 9
                                                   };
    private final static long     serialVersionUID = 1L;
    
    // Score heuristic function.
    public static double h(final GoGame game, final GoPlayer player)
    {
        return GoGame.h(game.getGameBoard(), player);
    }
    
    // Score heuristic function.
    public static double h(final GoPoint[][] gameBoard, final GoPlayer player)
    {
        double score = player.getPlayerScore();
        
        // Loop through every instance of a GoPoint object within the GameBoard array.
        for (int y = 0; y < gameBoard.length; y++)
        {
            for (GoPoint[] element: gameBoard)
            {
                element[y].determineOwnership(gameBoard, player);
                
                // Each GoPoint that is owned by a player increments that player's score by 1.0.
                if (element[y].getOwnershipState() == player.getPlayerColor())
                {
                    score += 1.0;
                }
            }
        }
        
        return score;
    }
    
    public static void handleCaptures(final GoPoint[][] gameBoard, final GoPlayer player)
    {
        // Loop through every instance of a GoPoint object within the 2D gameBoard array.
        for (int y = 0; y < gameBoard.length; y++)
        {
            for (GoPoint[] element: gameBoard)
            {
                // Create a temporary reference to the GoPoint being evaluated called 'thisPoint'.
                GoPoint thisPoint = element[y];
                
                // GoPoints surrounded on all non-diagonal sides by the opposing player are captured.
                int numAdjPoints = thisPoint.getNumberOfAdjacentPoints(gameBoard);
                int numAdjOppStones = thisPoint.getNumberOfAdjacentOpposingStones(gameBoard, player);
                
                // Test for capture.
                if (numAdjOppStones == numAdjPoints)
                {
                    thisPoint.setOccupationState(Color.GRAY);
                    
                    // Remove this point from any groups to which it belongs.
                    if (thisPoint.getGroup() != null)
                    {
                        Set<GoPoint> group = thisPoint.getGroup();
                        
                        group.remove(thisPoint);
                        thisPoint.setGroup(null);
                        
                        // TODO: Handle groups of stones properly after capture test.
                    }
                }
                else
                {
                    // TODO: Handle groups of stones properly during capture test.
                }
            }
        }
    }
    
    private GoPlayer      blackPlayer              = null;
    private GoPlayer      computerPlayer           = null;
    private GoPlayer      currentPlayer            = null;
    private GoPoint[][]   gameBoard                = null;
    private boolean       hasCurrentPlayerActed    = false;
    private boolean       hasCurrentPlayerResigned = false;
    private GoPlayer      humanPlayer              = null;
    private boolean       isGameEven               = false;
    private List<GoMove>  moveList                 = null;
    private ChineseGoDemo parent                   = null;
    private GoPlayer      whitePlayer              = null;
    private GoPlayer      winningPlayer            = null;
    
    public GoGame(final boolean isHumanBlack, final ChineseGoDemo parent)
    {
        this(isHumanBlack, GoGame.DEFAULT_SIZES[0], parent);
    }
    
    public GoGame(final boolean isHumanBlack, final Integer boardSize, final boolean isGameEven, final ChineseGoDemo parent)
    {
        // Initialize critical variables.
        super();
        this.setGameBoard(new GoPoint[boardSize][boardSize]);
        this.setGameEven(isGameEven);
        this.setParent(parent);
        
        // Loop through every instance of a GoPoint object within the 2D getGameBoard() array.
        for (int y = 0; y < this.getGameBoard().length; y++)
        {
            for (int x = 0; x < this.getGameBoard().length; x++)
            {
                // Set X and Y coordinates of the GoPoint.
                // The columns are the X coordinates.
                // The rows are the Y coordinates.
                this.getGameBoard()[x][y] = new GoPoint(x, y);
            }
        }
        
        // Initialize White's score counter.
        // In even (non-handicap) games, Black gives White a komi (compensation) of 7.5 points in exchange for the advantage of moving first.
        // In handicap games, Black gives White a komi of only 0.5 points in order to avoid draws.
        double pointsWhite = 0.5;
        
        if (!this.isGameEven())
        {
            pointsWhite += 7.0;
        }
        
        // Set up initial GoPlayer objects.
        this.setBlackPlayer(new GoPlayer(Color.BLACK, this, 0.0));
        this.setWhitePlayer(new GoPlayer(Color.WHITE, this, pointsWhite));
        
        // Figure out which player is black and which player is white.
        if (isHumanBlack)
        {
            this.setHumanPlayer(this.getBlackPlayer());
            this.setComputerPlayer(this.getWhitePlayer());
        }
        else
        {
            this.setHumanPlayer(this.getWhitePlayer());
            this.setComputerPlayer(this.getBlackPlayer());
        }
        
        this.setMoveList(new LinkedList<GoMove>());
        this.start();
    }
    
    public GoGame(final boolean isHumanBlack, final Integer boardSize, final ChineseGoDemo parent)
    {
        this(isHumanBlack, boardSize, true, parent);
    }
    
    public GoGame(final ChineseGoDemo parent)
    {
        this(true, parent);
    }
    
    public void determineWinner()
    {
        // Calculate player scores.
        double blackScore = GoGame.h(this, this.getBlackPlayer());
        double whiteScore = GoGame.h(this, this.getWhitePlayer());
        this.getBlackPlayer().setPlayerScore(blackScore);
        this.getWhitePlayer().setPlayerScore(whiteScore);
        
        // If one player has more points than the other, then there is a winning player. Return a reference to that player.
        // If both players have the same number of points, then there is not a winning player (the game is a draw). Return null.
        if (this.getBlackPlayer().getPlayerScore() > this.getWhitePlayer().getPlayerScore())
        {
            this.setWinningPlayer((this.getBlackPlayer()));
        }
        else if (this.getBlackPlayer().getPlayerScore() < this.getWhitePlayer().getPlayerScore())
        {
            this.setWinningPlayer((this.getWhitePlayer()));
        }
    }
    
    public final GoPlayer getBlackPlayer()
    {
        return this.blackPlayer;
    }
    
    public final GoPlayer getComputerPlayer()
    {
        return this.computerPlayer;
    }
    
    public final GoPlayer getCurrentPlayer()
    {
        return this.currentPlayer;
    }
    
    public final GoPoint[][] getGameBoard()
    {
        return this.gameBoard;
    }
    
    public final GoPlayer getHumanPlayer()
    {
        return this.humanPlayer;
    }
    
    public List<GoPoint> getLegalMoves(final GoPlayer player)
    {
        // Create an empty list to hold all the legal moves for the given player.
        List<GoPoint> legalMoves = new LinkedList<GoPoint>();
        
        // Loop through every instance of GoPoint stored within the getGameBoard() array.
        for (int y = 0; y < this.getGameBoard().length; y++)
        {
            for (int x = 0; x < this.getGameBoard().length; x++)
            {
                // Create a temporary reference to the GoPoint being evaluated called 'thisPoint'.
                GoPoint thisPoint = this.getGameBoard()[x][y];
                
                if (thisPoint.isLegalMove(this.getGameBoard(), player))
                {
                    // Start with the assumption that this move does not violate the Ko rule.
                    boolean moveViolatesKoRule = false;
                    
                    // TODO: Implement a legality test for the Ko rule.
                    
                    // Test for legality.
                    if (!moveViolatesKoRule)
                    {
                        // Add 'thisPoint' to the list of legal moves for the given player.
                        legalMoves.add(thisPoint);
                    }
                    
                }
            }
        }
        
        // Return the list of legal moves, which may or may not be empty.
        return legalMoves;
    }
    
    public final List<GoMove> getMoveList()
    {
        return this.moveList;
    }
    
    public final ChineseGoDemo getParent()
    {
        return this.parent;
    }
    
    public final String getPlayerString(final GoPlayer player)
    {
        if (this.getHumanPlayer() == player)
        {
            return "Human [" + player + "]";
        }
        else if (this.getComputerPlayer() == player)
        {
            return "Computer [" + player + "]";
        }
        else
        {
            return "[" + player + "]";
        }
    }
    
    public final GoPlayer getWhitePlayer()
    {
        return this.whitePlayer;
    }
    
    public final GoPlayer getWinningPlayer()
    {
        return this.winningPlayer;
    }
    
    public void handleAction(final GoGame.ACTION_TYPE actionType, final GoPoint point)
    {
        if (actionType == GoGame.ACTION_TYPE.RESIGN)
        {
            this.setCurrentPlayer(null);
            this.setCurrentPlayerResigned(true);
            this.setCurrentPlayerActed(true);
        }
        else
        {
            this.handleMoveOrPass(point);
        }
    }
    
    protected void handleMoveOrPass(final GoPoint point)
    {
        if (point != null)
        {
            if (this.getLegalMoves(this.getCurrentPlayer()).contains(point))
            {
                this.setCurrentPlayerActed(true);
                GoMove thisMove = new GoMove(this.getCurrentPlayer(), point);
                this.getMoveList().add(thisMove);
                point.setOccupationState(this.getCurrentPlayer().getPlayerColor());
                GoPoint.determineGroup(this.getGameBoard(), point);
                GoGame.handleCaptures(this.getGameBoard(), this.getBlackPlayer());
                GoGame.handleCaptures(this.getGameBoard(), this.getWhitePlayer());
            }
        }
        else
        {
            this.setCurrentPlayerActed(true);
            GoMove thisMove = new GoMove(this.getCurrentPlayer(), point);
            this.getMoveList().add(thisMove);
        }
    }
    
    public final boolean hasCurrentPlayerActed()
    {
        return this.hasCurrentPlayerActed;
    }
    
    public final boolean hasCurrentPlayerResigned()
    {
        return (this.hasCurrentPlayerResigned && (this.getCurrentPlayer() == null));
    }
    
    public final boolean isComputerTurn()
    {
        // Returns true if the current player is the computer player.
        return (this.getCurrentPlayer() == this.getComputerPlayer());
    }
    
    public final boolean isGameEven()
    {
        return this.isGameEven;
    }
    
    public boolean isGameOver()
    {
        int indexOfLastMove = this.getMoveList().size() - 1;
        boolean isGameOver = false;
        boolean humanHasNoLegalMovesLeft = this.getLegalMoves(this.getHumanPlayer()).isEmpty();
        boolean computerHasNoLegalMovesLeft = this.getLegalMoves(this.getComputerPlayer()).isEmpty();
        
        // Determine if the current player has used his turn to resign.
        if (this.hasCurrentPlayerResigned())
        {
            // If the current player has resigned, then end the game.
            isGameOver = this.hasCurrentPlayerResigned();
        }
        else
        {
            if (this.getMoveList().size() > 0)
            {
                GoMove lastMove = this.getMoveList().get(indexOfLastMove);
                
                // White must get the last turn of the game, even if that turn is simply used to pass.
                if (lastMove.getPlayer().getPlayerColor() == Color.WHITE)
                {
                    // Not considering resignations, the game ends if one of the following conditions are true:
                    // (A) Neither player has any legal moves available.
                    isGameOver = ((humanHasNoLegalMovesLeft) && (computerHasNoLegalMovesLeft));
                    
                    // Only run this check if the previous check was false.
                    if (!isGameOver)
                    {
                        // (B) Both players use their most recent move to pass.
                        // Only check this if more than one move has been recorded to avoid an index out of bounds exception.
                        if (this.getMoveList().size() > 1)
                        {
                            GoMove nextLast = this.getMoveList().get(indexOfLastMove - 1);
                            isGameOver = ((lastMove.getPoint() == null) && (nextLast.getPoint() == null));
                        }
                    }
                }
            }
        }
        
        return isGameOver;
    }
    
    public final boolean isHumanTurn()
    {
        // Returns true if the current player is the human player.
        return (this.getCurrentPlayer() == this.getHumanPlayer());
    }
    
    @Override
    public void run()
    {
        while (this.getWinningPlayer() == null)
        {
            // Set up initial game conditions.
            // Black goes first.
            GoPlayer opposingPlayer = this.getWhitePlayer();
            this.setCurrentPlayer(this.getBlackPlayer());
            this.setCurrentPlayerActed(false);
            this.setCurrentPlayerResigned(false);
            
            new GoAI(this);
            
            // This loop continues to execute until the game is over, and makes at least one pass.
            do
            {
                // Wait until the current player has acted.
                while (!this.hasCurrentPlayerActed())
                {
                    try
                    {
                        TimeUnit.MILLISECONDS.sleep(5);
                    }
                    catch (final InterruptedException e)
                    {
                        ChineseGoDemo.printException(this.getParent().getWindow(), e);
                    }
                }
                
                // Switch from the current player to the opposing player.
                if (this.getCurrentPlayer() == this.getBlackPlayer())
                {
                    opposingPlayer = this.getBlackPlayer();
                    this.setCurrentPlayer(this.getWhitePlayer());
                    this.getParent().getWindow().reDrawGUI();
                }
                else if (this.getCurrentPlayer() == this.getWhitePlayer())
                {
                    opposingPlayer = this.getWhitePlayer();
                    this.setCurrentPlayer(this.getBlackPlayer());
                    this.getParent().getWindow().reDrawGUI();
                }
                
                // Reset the wait flag.
                this.setCurrentPlayerActed(false);
            }
            while (!this.isGameOver());
            
            // Determine the winner of the game.
            if (this.getWinningPlayer() == null)
            {
                // If the current player resigned, the opposing player is automatically the winner.
                // Don't bother to calculate scores.
                if (this.hasCurrentPlayerResigned() && (this.getCurrentPlayer() == null))
                {
                    this.setWinningPlayer(opposingPlayer);
                }
            }
            else
            {
                this.determineWinner();
            }
        }
        
        // Print a message saying who won the game.
        StringBuilder winMessageBuilder = new StringBuilder();
        winMessageBuilder.append("Winning Player: ");
        winMessageBuilder.append(this.getPlayerString(this.getWinningPlayer()));
        winMessageBuilder.append("\n\n");
        winMessageBuilder.append("Black's score: " + this.getBlackPlayer().getPlayerScore() + "\n");
        winMessageBuilder.append("White's score: " + this.getWhitePlayer().getPlayerScore() + "\n");
        winMessageBuilder.append("\n");
        
        if (this.getWinningPlayer() == this.getHumanPlayer())
        {
            winMessageBuilder.append("Congratulations on your victory!");
        }
        else if (this.getWinningPlayer() == this.getComputerPlayer())
        {
            winMessageBuilder.append("Condolences for your loss!");
        }
        
        winMessageBuilder.append("\n\n");
        winMessageBuilder.append("Please play again soon!");
        ChineseGoDemo.printWinMessage(this.getParent().getWindow(), winMessageBuilder.toString());
    }
    
    protected final void setBlackPlayer(final GoPlayer blackPlayer)
    {
        this.blackPlayer = blackPlayer;
    }
    
    protected final void setComputerPlayer(final GoPlayer computerPlayer)
    {
        this.computerPlayer = computerPlayer;
    }
    
    protected final void setCurrentPlayer(final GoPlayer currentPlayer)
    {
        this.currentPlayer = currentPlayer;
    }
    
    protected final void setCurrentPlayerActed(final boolean hasCurrentPlayerActed)
    {
        this.hasCurrentPlayerActed = hasCurrentPlayerActed;
    }
    
    protected final void setCurrentPlayerResigned(final boolean hasCurrentPlayerResigned)
    {
        this.hasCurrentPlayerResigned = hasCurrentPlayerResigned;
    }
    
    protected final void setGameBoard(final GoPoint[][] gameBoard)
    {
        this.gameBoard = gameBoard;
    }
    
    protected final void setGameEven(final boolean isGameEven)
    {
        this.isGameEven = isGameEven;
    }
    
    protected final void setHumanPlayer(final GoPlayer humanPlayer)
    {
        this.humanPlayer = humanPlayer;
    }
    
    protected final void setMoveList(final List<GoMove> moveList)
    {
        this.moveList = moveList;
    }
    
    public final void setParent(final ChineseGoDemo parent)
    {
        this.parent = parent;
    }
    
    protected final void setWhitePlayer(final GoPlayer whitePlayer)
    {
        this.whitePlayer = whitePlayer;
    }
    
    protected final void setWinningPlayer(final GoPlayer winningPlayer)
    {
        this.winningPlayer = winningPlayer;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("GoGame [");
        
        if (this.getBlackPlayer() != null)
        {
            builder.append("BlackPlayer=");
            builder.append(this.getBlackPlayer());
            builder.append(", ");
        }
        
        if (this.getComputerPlayer() != null)
        {
            builder.append("ComputerPlayer=");
            builder.append(this.getComputerPlayer());
            builder.append(", ");
        }
        
        if (this.getCurrentPlayer() != null)
        {
            builder.append("CurrentPlayer=");
            builder.append(this.getCurrentPlayer());
            builder.append(", ");
        }
        
        if (this.getGameBoard() != null)
        {
            builder.append("GameBoard=");
            builder.append(Arrays.toString(this.getGameBoard()));
            builder.append(", ");
        }
        
        if (this.getHumanPlayer() != null)
        {
            builder.append("HumanPlayer=");
            builder.append(this.getHumanPlayer());
            builder.append(", ");
        }
        
        if (this.getMoveList() != null)
        {
            builder.append("MoveList=");
            builder.append(this.getMoveList());
            builder.append(", ");
        }
        
        if (this.getParent() != null)
        {
            builder.append("Parent=");
            builder.append(this.getParent());
            builder.append(", ");
        }
        
        if (this.getWhitePlayer() != null)
        {
            builder.append("WhitePlayer=");
            builder.append(this.getWhitePlayer());
            builder.append(", ");
        }
        
        if (this.getWinningPlayer() != null)
        {
            builder.append("WinningPlayer=");
            builder.append(this.getWinningPlayer());
            builder.append(", ");
        }
        
        builder.append("hasCurrentPlayerActed=");
        builder.append(this.hasCurrentPlayerActed());
        builder.append(", hasCurrentPlayerResigned=");
        builder.append(this.hasCurrentPlayerResigned());
        builder.append(", isComputerTurn=");
        builder.append(this.isComputerTurn());
        builder.append(", isGameEven=");
        builder.append(this.isGameEven());
        builder.append(", isGameOver=");
        builder.append(this.isGameOver());
        builder.append(", isHumanTurn=");
        builder.append(this.isHumanTurn());
        builder.append("]");
        return builder.toString();
    }
}