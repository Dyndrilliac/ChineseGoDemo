/*
 * @formatter:off
 * 
 * TODO: Handle groups of stones properly during ownership test.
 * TODO: Handle groups of stones properly during legality test.
 * 
 * @formatter:on
 */

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import api.util.ObjectCloner;
import api.util.Support;

public class GoPoint extends Point2D.Double
{
    public static class DesirabilityComparator implements Comparator<GoPoint>
    {
        private GoPoint[][] gameBoard = null;
        private GoPlayer    player1   = null;
        private GoPlayer    player2   = null;
        
        public DesirabilityComparator(final GoPoint[][] gameBoard, final GoPlayer player1, final GoPlayer player2)
        {
            super();
            this.setGameBoard(gameBoard);
            this.setPlayer1(player1);
            this.setPlayer2(player2);
        }
        
        @Override
        public final int compare(final GoPoint p1, final GoPoint p2)
        {
            int p1Desirability = p1.getDesirability(this.getGameBoard(), this.getPlayer1(), this.getPlayer2());
            int p2Desirability = p2.getDesirability(this.getGameBoard(), this.getPlayer1(), this.getPlayer2());
            
            if (p1Desirability == p2Desirability)
            {
                return 0;
            }
            else if (p1Desirability < p2Desirability)
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
        
        public final GoPoint[][] getGameBoard()
        {
            return this.gameBoard;
        }
        
        public final GoPlayer getPlayer1()
        {
            return this.player1;
        }
        
        public final GoPlayer getPlayer2()
        {
            return this.player2;
        }
        
        protected final void setGameBoard(final GoPoint[][] gameBoard)
        {
            this.gameBoard = gameBoard;
        }
        
        protected final void setPlayer1(final GoPlayer player1)
        {
            this.player1 = player1;
        }
        
        protected final void setPlayer2(final GoPlayer player2)
        {
            this.player2 = player2;
        }
        
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("DesirabilityComparator [");
            
            if (this.getGameBoard() != null)
            {
                builder.append("GameBoard=");
                builder.append(Arrays.toString(this.getGameBoard()));
                builder.append(", ");
            }
            
            if (this.getPlayer1() != null)
            {
                builder.append("Player1=");
                builder.append(this.getPlayer1());
                builder.append(", ");
            }
            
            if (this.getPlayer2() != null)
            {
                builder.append("Player2=");
                builder.append(this.getPlayer2());
            }
            
            builder.append("]");
            return builder.toString();
        }
    }
    
    public static class DesirabilityPredicate implements Predicate<GoPoint>
    {
        private GoPoint[][] gameBoard = null;
        private GoPlayer    player1   = null;
        private GoPlayer    player2   = null;
        
        public DesirabilityPredicate(final GoPoint[][] gameBoard, final GoPlayer player1, final GoPlayer player2)
        {
            super();
            this.setGameBoard(gameBoard);
            this.setPlayer1(player1);
            this.setPlayer2(player2);
        }
        
        public final GoPoint[][] getGameBoard()
        {
            return this.gameBoard;
        }
        
        public final GoPlayer getPlayer1()
        {
            return this.player1;
        }
        
        public final GoPlayer getPlayer2()
        {
            return this.player2;
        }
        
        protected final void setGameBoard(final GoPoint[][] gameBoard)
        {
            this.gameBoard = gameBoard;
        }
        
        protected final void setPlayer1(final GoPlayer player1)
        {
            this.player1 = player1;
        }
        
        protected final void setPlayer2(final GoPlayer player2)
        {
            this.player2 = player2;
        }
        
        @Override
        public boolean test(final GoPoint point)
        {
            int desirability = point.getDesirability(this.getGameBoard(), this.getPlayer1(), this.getPlayer2());
            
            if (desirability < 1)
            {
                return true;
            }
            
            return false;
        }
        
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("DesirabilityPredicate [");
            
            if (this.getGameBoard() != null)
            {
                builder.append("GameBoard=");
                builder.append(Arrays.toString(this.getGameBoard()));
                builder.append(", ");
            }
            
            if (this.getPlayer1() != null)
            {
                builder.append("Player1=");
                builder.append(this.getPlayer1());
                builder.append(", ");
            }
            
            if (this.getPlayer2() != null)
            {
                builder.append("Player2=");
                builder.append(this.getPlayer2());
            }
            
            builder.append("]");
            return builder.toString();
        }
    }
    
    private final static long serialVersionUID = 1L;
    
    public static void determineGroup(final GoPoint[][] gameBoard, final GoPoint point)
    {
        List<GoPoint> adjacencyList = point.getAdjacencyList(gameBoard);
        
        for (GoPoint p: adjacencyList)
        {
            if (point.getOccupationState() == p.getOccupationState())
            {
                if ((point.getGroup() != null) && (p.getGroup() != null))
                {
                    point.getGroup().addAll(p.getGroup());
                    p.setGroup(point.getGroup());
                }
                else if ((point.getGroup() != null) && (p.getGroup() == null))
                {
                    point.getGroup().add(p);
                    p.setGroup(point.getGroup());
                }
                else if ((point.getGroup() == null) && (p.getGroup() != null))
                {
                    point.setGroup(new HashSet<GoPoint>(p.getGroup()));
                    p.setGroup(point.getGroup());
                }
                else
                {
                    point.setGroup(new HashSet<GoPoint>());
                    point.getGroup().add(p);
                    p.setGroup(point.getGroup());
                }
            }
        }
    }
    
    private Set<GoPoint> group           = null;
    private Color        occupationState = Color.GRAY;
    private Color        ownershipState  = Color.GRAY;
    
    public GoPoint()
    {
        this(0, 0);
    }
    
    public GoPoint(final double x, final double y)
    {
        this(x, y, Color.GRAY);
    }
    
    public GoPoint(final double x, final double y, final Color occupation)
    {
        this(x, y, occupation, Color.GRAY);
    }
    
    public GoPoint(final double x, final double y, final Color occupation, final Color ownership)
    {
        super(x, y);
        this.setOccupationState(occupation);
        this.setOwnershipState(ownership);
    }
    
    public void determineOwnership(final GoPoint[][] gameBoard, final GoPlayer player)
    {
        if (this.getOccupationState() != Color.GRAY)
        {
            if (this.getOccupationState() == player.getPlayerColor())
            {
                this.setOwnershipState(this.getOccupationState());
            }
        }
        else
        {
            // GoPoints surrounded on all non-diagonal sides by a given player are owned.
            int numAdjPoints = this.getNumberOfAdjacentPoints(gameBoard);
            int numAdjAllStones = this.getNumberOfAdjacentAlliedStones(gameBoard, player);
            
            if (numAdjAllStones == numAdjPoints)
            {
                this.setOwnershipState(player.getPlayerColor());
            }
            else
            {
                // TODO: Handle groups of stones properly during ownership test.
            }
        }
    }
    
    protected int employHeuristics(final GoPoint[][] gameBoard, final GoPlayer player1, final GoPlayer player2)
    {
        GoPoint point = null;
        int heuristicValue = 0;
        GoPoint[][] oldBoard = null;
        GoPoint[][] newBoard = null;
        
        try
        {
            oldBoard = (GoPoint[][])ObjectCloner.deepCopy(gameBoard);
            newBoard = (GoPoint[][])ObjectCloner.deepCopy(gameBoard);
        }
        catch (final Exception e)
        {
            ChineseGoDemo.printException(null, e);
        }
        
        if ((oldBoard != null) && (newBoard != null))
        {
            point = newBoard[this.getX_Int()][this.getY_Int()];
            
            if (point != null)
            {
                point.setOccupationState(player1.getPlayerColor());
                GoPoint.determineGroup(gameBoard, point);
                GoGame.handleCaptures(newBoard, player1);
                GoGame.handleCaptures(newBoard, player2);
                double oldScore = GoGame.h(oldBoard, player1);
                double newScore = GoGame.h(newBoard, player1);
                
                if (newScore > oldScore)
                {
                    double difference = newScore - oldScore;
                    heuristicValue += ((int)difference);
                }
                else if (newScore < oldScore)
                {
                    heuristicValue -= 2;
                }
                else
                {
                    heuristicValue--;
                }
            }
        }
        
        return heuristicValue;
    }
    
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (!super.equals(obj))
        {
            return false;
        }
        
        if (!(obj instanceof GoPoint))
        {
            return false;
        }
        
        GoPoint other = (GoPoint)obj;
        
        if (this.occupationState == null)
        {
            if (other.occupationState != null)
            {
                return false;
            }
        }
        else if (!this.occupationState.equals(other.occupationState))
        {
            return false;
        }
        
        if (this.ownershipState == null)
        {
            if (other.ownershipState != null)
            {
                return false;
            }
        }
        else if (!this.ownershipState.equals(other.ownershipState))
        {
            return false;
        }
        
        return true;
    }
    
    public List<GoPoint> getAdjacencyList(final GoPoint[][] gameBoard)
    {
        List<GoPoint> adjacencyList = new LinkedList<GoPoint>();
        
        if (this.isCornerPoint(gameBoard))
        {
            if (this.isCornerPoint_BottomLeft(gameBoard))
            {
                adjacencyList.add(gameBoard[this.getX_Int()][this.getY_Int() - 1]); // Above
                adjacencyList.add(gameBoard[this.getX_Int() + 1][this.getY_Int()]); // Right
            }
            else if (this.isCornerPoint_BottomRight(gameBoard))
            {
                adjacencyList.add(gameBoard[this.getX_Int()][this.getY_Int() - 1]); // Above
                adjacencyList.add(gameBoard[this.getX_Int() - 1][this.getY_Int()]); // Left
            }
            else if (this.isCornerPoint_TopLeft(gameBoard))
            {
                adjacencyList.add(gameBoard[this.getX_Int()][this.getY_Int() + 1]); // Below
                adjacencyList.add(gameBoard[this.getX_Int() + 1][this.getY_Int()]); // Right
            }
            else if (this.isCornerPoint_TopRight(gameBoard))
            {
                adjacencyList.add(gameBoard[this.getX_Int()][this.getY_Int() + 1]); // Below
                adjacencyList.add(gameBoard[this.getX_Int() - 1][this.getY_Int()]); // Left
            }
        }
        else if (this.isEdgePoint(gameBoard))
        {
            if (this.isEdgePoint_Bottom(gameBoard))
            {
                adjacencyList.add(gameBoard[this.getX_Int()][this.getY_Int() - 1]); // Above
                adjacencyList.add(gameBoard[this.getX_Int() + 1][this.getY_Int()]); // Right
                adjacencyList.add(gameBoard[this.getX_Int() - 1][this.getY_Int()]); // Left
            }
            else if (this.isEdgePoint_Left(gameBoard))
            {
                adjacencyList.add(gameBoard[this.getX_Int()][this.getY_Int() + 1]); // Below
                adjacencyList.add(gameBoard[this.getX_Int()][this.getY_Int() - 1]); // Above
                adjacencyList.add(gameBoard[this.getX_Int() + 1][this.getY_Int()]); // Right
            }
            else if (this.isEdgePoint_Right(gameBoard))
            {
                adjacencyList.add(gameBoard[this.getX_Int()][this.getY_Int() + 1]); // Below
                adjacencyList.add(gameBoard[this.getX_Int()][this.getY_Int() - 1]); // Above
                adjacencyList.add(gameBoard[this.getX_Int() - 1][this.getY_Int()]); // Left
            }
            else if (this.isEdgePoint_Top(gameBoard))
            {
                adjacencyList.add(gameBoard[this.getX_Int()][this.getY_Int() + 1]); // Below
                adjacencyList.add(gameBoard[this.getX_Int() + 1][this.getY_Int()]); // Right
                adjacencyList.add(gameBoard[this.getX_Int() - 1][this.getY_Int()]); // Left
            }
        }
        else
        {
            adjacencyList.add(gameBoard[this.getX_Int()][this.getY_Int() + 1]); // Below
            adjacencyList.add(gameBoard[this.getX_Int()][this.getY_Int() - 1]); // Above
            adjacencyList.add(gameBoard[this.getX_Int() + 1][this.getY_Int()]); // Right
            adjacencyList.add(gameBoard[this.getX_Int() - 1][this.getY_Int()]); // Left
        }
        
        return adjacencyList;
    }
    
    public final int getDesirability(final GoPoint[][] gameBoard, final GoPlayer player1, final GoPlayer player2)
    {
        int desirability = 0;
        
        int numAdjPoints = this.getNumberOfAdjacentPoints(gameBoard);
        int numAdjOppStones = this.getNumberOfAdjacentOpposingStones(gameBoard, player1);
        int numAdjAllStones = this.getNumberOfAdjacentAlliedStones(gameBoard, player1);
        
        if (numAdjOppStones < (numAdjPoints - 2))
        {
            desirability += 2;
        }
        else if (numAdjOppStones < (numAdjPoints - 1))
        {
            desirability++;
        }
        else
        {
            if (numAdjAllStones >= 2)
            {
                desirability += 2;
            }
            if (numAdjAllStones >= 1)
            {
                desirability++;
            }
            else
            {
                desirability--;
            }
        }
        
        if (this.isStarPoint(gameBoard))
        {
            desirability += 2;
        }
        
        if (this.isCornerPoint(gameBoard))
        {
            desirability -= 2;
        }
        else if (this.isEdgePoint(gameBoard))
        {
            desirability--;
        }
        
        desirability += this.employHeuristics(gameBoard, player1, player2);
        
        return desirability;
    }
    
    public final Set<GoPoint> getGroup()
    {
        return this.group;
    }
    
    public int getNumberOfAdjacentAlliedStones(final GoPoint[][] gameBoard, final GoPlayer player)
    {
        int getNumberOfAdjacentAlliedStones = 0;
        List<GoPoint> adjacencyList = this.getAdjacencyList(gameBoard);
        
        for (GoPoint p: adjacencyList)
        {
            if (p.getOccupationState() == player.getPlayerColor())
            {
                getNumberOfAdjacentAlliedStones++;
            }
        }
        
        return getNumberOfAdjacentAlliedStones;
    }
    
    public int getNumberOfAdjacentOpposingStones(final GoPoint[][] gameBoard, final GoPlayer player)
    {
        int numberOfAdjacentOpposingStones = 0;
        List<GoPoint> adjacencyList = this.getAdjacencyList(gameBoard);
        
        for (GoPoint p: adjacencyList)
        {
            if (p.isOccupiedByOpposingPlayer(player))
            {
                numberOfAdjacentOpposingStones++;
            }
        }
        
        return numberOfAdjacentOpposingStones;
    }
    
    public final int getNumberOfAdjacentPoints(final GoPoint[][] gameBoard)
    {
        // Most GoPoints have four adjacent GoPoints.
        int numberOfAdjacentPoints = 4;
        
        // The corner GoPoints have two adjacent GoPoints.
        if (this.isCornerPoint(gameBoard))
        {
            numberOfAdjacentPoints = 2;
        }
        
        // The edge GoPoints have three adjacent GoPoints.
        if (this.isEdgePoint(gameBoard))
        {
            numberOfAdjacentPoints = 3;
        }
        
        return numberOfAdjacentPoints;
    }
    
    public final Color getOccupationState()
    {
        return this.occupationState;
    }
    
    public final Color getOwnershipState()
    {
        return this.ownershipState;
    }
    
    public final int getX_Int()
    {
        return (int)this.getX();
    }
    
    public final int getY_Int()
    {
        return (int)this.getY();
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((this.occupationState == null) ? 0 : this.occupationState.hashCode());
        result = (prime * result) + ((this.ownershipState == null) ? 0 : this.ownershipState.hashCode());
        return result;
    }
    
    public boolean isAdjacentTo(final GoPoint[][] gameBoard, final GoPoint somePoint)
    {
        boolean isAdjacentTo = false;
        
        if (this.getAdjacencyList(gameBoard).contains(somePoint))
        {
            isAdjacentTo = true;
        }
        
        return isAdjacentTo;
    }
    
    public boolean isCenterPoint(final GoPoint[][] gameBoard)
    {
        boolean isCenterPoint = false;
        
        final int CENTER = gameBoard.length / 2;
        
        if (this.getX_Int() == CENTER)
        {
            if (this.getY_Int() == CENTER)
            {
                isCenterPoint = true;
            }
        }
        
        return isCenterPoint;
    }
    
    public final boolean isCornerPoint(final GoPoint[][] gameBoard)
    {
        boolean isCornerPoint = false;
        
        if (this.isCornerPoint_TopLeft(gameBoard))
        {
            isCornerPoint = true;
        }
        
        if (this.isCornerPoint_TopRight(gameBoard))
        {
            isCornerPoint = true;
        }
        
        if (this.isCornerPoint_BottomLeft(gameBoard))
        {
            isCornerPoint = true;
        }
        
        if (this.isCornerPoint_BottomRight(gameBoard))
        {
            isCornerPoint = true;
        }
        
        return isCornerPoint;
    }
    
    public final boolean isCornerPoint_BottomLeft(final GoPoint[][] gameBoard)
    {
        boolean isCornerPoint_BottomLeft = false;
        
        // Check for (0,18)
        if (this.getX_Int() == 0)
        {
            if (this.getY_Int() == (gameBoard.length - 1))
            {
                isCornerPoint_BottomLeft = true;
            }
        }
        
        return isCornerPoint_BottomLeft;
    }
    
    public final boolean isCornerPoint_BottomRight(final GoPoint[][] gameBoard)
    {
        boolean isCornerPoint_BottomRight = false;
        
        // Check for (18,18)
        if (this.getX_Int() == (gameBoard.length - 1))
        {
            if (this.getY_Int() == (gameBoard.length - 1))
            {
                isCornerPoint_BottomRight = true;
            }
        }
        
        return isCornerPoint_BottomRight;
    }
    
    public final boolean isCornerPoint_TopLeft(final GoPoint[][] gameBoard)
    {
        boolean isCornerPoint_TopLeft = false;
        
        // Check for (0,0)
        if (this.getX_Int() == 0)
        {
            if (this.getY_Int() == 0)
            {
                isCornerPoint_TopLeft = true;
            }
        }
        
        return isCornerPoint_TopLeft;
    }
    
    public final boolean isCornerPoint_TopRight(final GoPoint[][] gameBoard)
    {
        boolean isCornerPoint_TopRight = false;
        
        // Check for (18,0)
        if (this.getX_Int() == (gameBoard.length - 1))
        {
            if (this.getY_Int() == 0)
            {
                isCornerPoint_TopRight = true;
            }
        }
        
        return isCornerPoint_TopRight;
    }
    
    public final boolean isEdgePoint(final GoPoint[][] gameBoard)
    {
        // Edges: (0,1-17), (1-17,0), (18,1-17), (1-17,18)
        boolean isEdgePoint = false;
        
        if (this.isEdgePoint_Top(gameBoard))
        {
            isEdgePoint = true;
        }
        
        if (this.isEdgePoint_Right(gameBoard))
        {
            isEdgePoint = true;
        }
        
        if (this.isEdgePoint_Left(gameBoard))
        {
            isEdgePoint = true;
        }
        
        if (this.isEdgePoint_Bottom(gameBoard))
        {
            isEdgePoint = true;
        }
        
        return isEdgePoint;
    }
    
    public final boolean isEdgePoint_Bottom(final GoPoint[][] gameBoard)
    {
        boolean isEdgePoint_Bottom = false;
        
        // Check for (1-17,18)
        if (this.getY_Int() == (gameBoard.length - 1))
        {
            if ((this.getX_Int() > 0) || (this.getX_Int() < (gameBoard.length - 1)))
            {
                isEdgePoint_Bottom = true;
            }
        }
        
        return isEdgePoint_Bottom;
    }
    
    public final boolean isEdgePoint_Left(final GoPoint[][] gameBoard)
    {
        boolean isEdgePoint_Left = false;
        
        // Check for (0,1-17)
        if (this.getX_Int() == 0)
        {
            if ((this.getY_Int() > 0) || (this.getY_Int() < (gameBoard.length - 1)))
            {
                isEdgePoint_Left = true;
            }
        }
        
        return isEdgePoint_Left;
    }
    
    public final boolean isEdgePoint_Right(final GoPoint[][] gameBoard)
    {
        boolean isEdgePoint_Right = false;
        
        // Check for (18,1-17)
        if (this.getX_Int() == (gameBoard.length - 1))
        {
            if ((this.getY_Int() > 0) || (this.getY_Int() < (gameBoard.length - 1)))
            {
                isEdgePoint_Right = true;
            }
        }
        
        return isEdgePoint_Right;
    }
    
    public final boolean isEdgePoint_Top(final GoPoint[][] gameBoard)
    {
        boolean isEdgePoint_Top = false;
        
        // Check for (1-17,0)
        if (this.getY_Int() == 0)
        {
            if ((this.getX_Int() > 0) || (this.getX_Int() < (gameBoard.length - 1)))
            {
                isEdgePoint_Top = true;
            }
        }
        
        return isEdgePoint_Top;
    }
    
    public boolean isLegalMove(final GoPoint[][] gameBoard, final GoPlayer player)
    {
        // GoPoints that are currently occupied by either player are not legal moves.
        boolean isLegalMove = (this.getOccupationState() == Color.GRAY);
        
        // Only keep testing this point if it passed the first legality test.
        if (isLegalMove)
        {
            // GoPoints surrounded on all non-diagonal sides by the opposing player are not legal moves.
            int numAdjPoints = this.getNumberOfAdjacentPoints(gameBoard);
            int numAdjOppStones = this.getNumberOfAdjacentOpposingStones(gameBoard, player);
            
            // Test for legality.
            if (numAdjOppStones == numAdjPoints)
            {
                isLegalMove = false;
            }
            else
            {
                // TODO: Handle groups of stones properly during legality test.
            }
        }
        
        return isLegalMove;
    }
    
    public boolean isOccupiedByOpposingPlayer(final GoPlayer player)
    {
        boolean isOccupiedByOpposingPlayer = false;
        
        if (this.getOccupationState() != Color.GRAY)
        {
            if (this.getOccupationState() != player.getPlayerColor())
            {
                isOccupiedByOpposingPlayer = true;
            }
        }
        
        return isOccupiedByOpposingPlayer;
    }
    
    public boolean isStarPoint(final GoPoint[][] gameBoard)
    {
        boolean isStarPoint = false;
        
        if (this.isCenterPoint(gameBoard))
        {
            isStarPoint = true;
        }
        else
        {
            if (gameBoard.length == GoGame.DEFAULT_SIZES[0].intValue())
            {
                if ((this.getX_Int() == 3) || (this.getX_Int() == 9) || (this.getX_Int() == 15))
                {
                    if ((this.getY_Int() == 3) || (this.getY_Int() == 9) || (this.getY_Int() == 15))
                    {
                        isStarPoint = true;
                    }
                }
            }
            else if (gameBoard.length == GoGame.DEFAULT_SIZES[1].intValue())
            {
                if ((this.getX_Int() == 3) || (this.getX_Int() == 9))
                {
                    if ((this.getY_Int() == 3) || (this.getY_Int() == 9))
                    {
                        isStarPoint = true;
                    }
                }
            }
            else if (gameBoard.length == GoGame.DEFAULT_SIZES[2].intValue())
            {
                if ((this.getX_Int() == 2) || (this.getX_Int() == 6))
                {
                    if ((this.getY_Int() == 2) || (this.getY_Int() == 6))
                    {
                        isStarPoint = true;
                    }
                }
            }
        }
        
        return isStarPoint;
    }
    
    public final void setGroup(final Set<GoPoint> group)
    {
        this.group = group;
    }
    
    public final void setOccupationState(final Color occupationState)
    {
        this.occupationState = occupationState;
    }
    
    public final void setOwnershipState(final Color ownershipState)
    {
        this.ownershipState = ownershipState;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("GoPoint");
        builder.append("(" + this.getX_Int() + "," + this.getY_Int() + ")[");
        
        if (this.getOccupationState() != null)
        {
            builder.append("OccupationState=");
            builder.append(Support.getColorString(this.getOccupationState()));
        }
        
        builder.append(",");
        
        if (this.getOwnershipState() != null)
        {
            builder.append("OwnershipState=");
            builder.append(Support.getColorString(this.getOwnershipState()));
        }
        
        builder.append("]");
        return builder.toString();
    }
}