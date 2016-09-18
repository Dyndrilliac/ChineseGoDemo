import java.awt.Color;
import java.io.Serializable;

import api.util.Support;

public class GoPlayer implements Serializable, Cloneable
{
    private final static long serialVersionUID = 1L;
    private GoGame            parent           = null;
    private Color             playerColor      = null;
    private double            playerScore      = 0.0;
    
    public GoPlayer()
    {
        this(null);
    }
    
    public GoPlayer(final Color playerColor)
    {
        this(playerColor, null);
    }
    
    public GoPlayer(final Color playerColor, final GoGame parent)
    {
        this(playerColor, parent, 0.0);
    }
    
    public GoPlayer(final Color playerColor, final GoGame parent, final double score)
    {
        super();
        this.setPlayerColor(playerColor);
        this.setParent(parent);
        this.setPlayerScore(score);
    }
    
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (obj == null)
        {
            return false;
        }
        
        if (!(obj instanceof GoPlayer))
        {
            return false;
        }
        
        GoPlayer other = (GoPlayer)obj;
        
        if (this.parent == null)
        {
            if (other.parent != null)
            {
                return false;
            }
        }
        else if (!this.parent.equals(other.parent))
        {
            return false;
        }
        
        if (this.playerColor == null)
        {
            if (other.playerColor != null)
            {
                return false;
            }
        }
        else if (!this.playerColor.equals(other.playerColor))
        {
            return false;
        }
        
        if (Double.doubleToLongBits(this.playerScore) != Double.doubleToLongBits(other.playerScore))
        {
            return false;
        }
        
        return true;
    }
    
    public final GoGame getParent()
    {
        return this.parent;
    }
    
    public final Color getPlayerColor()
    {
        return this.playerColor;
    }
    
    public final double getPlayerScore()
    {
        return this.playerScore;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.parent == null) ? 0 : this.parent.hashCode());
        result = (prime * result) + ((this.playerColor == null) ? 0 : this.playerColor.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.playerScore);
        result = (prime * result) + (int)(temp ^ (temp >>> 32));
        return result;
    }
    
    protected final void setParent(final GoGame parent)
    {
        this.parent = parent;
    }
    
    protected final void setPlayerColor(final Color playerColor)
    {
        this.playerColor = playerColor;
    }
    
    public final void setPlayerScore(final double playerScore)
    {
        this.playerScore = playerScore;
    }
    
    @Override
    public String toString()
    {
        return Support.getColorString(this.getPlayerColor());
    }
}