import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import api.util.Mathematics;

public class GoAI extends Thread implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;
    private GoGame            currentGame      = null;
    
    public GoAI(final GoGame currentGame)
    {
        super();
        this.setCurrentGame(currentGame);
        
        try
        {
            TimeUnit.MILLISECONDS.sleep(400);
        }
        catch (final InterruptedException e)
        {
            ChineseGoDemo.printException(this.getCurrentGame().getParent().getWindow(), e);
        }
        
        this.start();
    }
    
    protected GoPoint decideMove()
    {
        GoPoint point = null;
        GoPoint[][] gameBoard = this.getCurrentGame().getGameBoard();
        GoPlayer player1 = this.getCurrentGame().getComputerPlayer();
        GoPlayer player2 = this.getCurrentGame().getHumanPlayer();
        
        // Get a list of all the AI's potential legal moves to choose from.
        List<GoPoint> legalMoves = this.getCurrentGame().getLegalMoves(player1);
        
        // Only go through the algorithm if there are legal moves available.
        if (!legalMoves.isEmpty())
        {
            // Sort the legal moves in descending order of desirability using the heuristic function.
            legalMoves.sort(new GoPoint.DesirabilityComparator(gameBoard, player1, player2));
            // Make a copy of the list.
            List<GoPoint> desirableMoves = new LinkedList<GoPoint>(legalMoves);
            // Remove all the obviously bad moves from the copy of the list.
            desirableMoves.removeIf(new GoPoint.DesirabilityPredicate(gameBoard, player1, player2));
            // Pick a move at random from the upper half of the copy of the list.
            point = desirableMoves.get(Mathematics.getRandomInteger(0, desirableMoves.size() / 2, true));
            // As a last resort, pick a move at random from the upper half of the original list.
            if (point == null)
            {
                point = legalMoves.get(Mathematics.getRandomInteger(0, desirableMoves.size() / 2, true));
            }
        }
        
        return point;
    }
    
    public final GoGame getCurrentGame()
    {
        return this.currentGame;
    }
    
    protected void makeMove()
    {
        GoPoint point = this.decideMove();
        
        if (point != null)
        {
            this.getCurrentGame().handleAction(GoGame.ACTION_TYPE.MOVE, point);
        }
        else
        {
            this.getCurrentGame().handleAction(GoGame.ACTION_TYPE.PASS, point);
        }
    }
    
    @Override
    public void run()
    {
        while (!this.getCurrentGame().isGameOver())
        {
            try
            {
                TimeUnit.MILLISECONDS.sleep(100);
                
                if (this.getCurrentGame().isComputerTurn())
                {
                    this.makeMove();
                }
                
                TimeUnit.MILLISECONDS.sleep(100);
            }
            catch (final InterruptedException e)
            {
                ChineseGoDemo.printException(this.getCurrentGame().getParent().getWindow(), e);
            }
        }
    }
    
    protected final void setCurrentGame(final GoGame currentGame)
    {
        this.currentGame = currentGame;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("GoAI [");
        
        if (this.getCurrentGame() != null)
        {
            builder.append("CurrentGame=");
            builder.append(this.getCurrentGame());
        }
        
        builder.append("]");
        return builder.toString();
    }
}