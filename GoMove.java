import java.io.Serializable;

public class GoMove implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;
    private GoPlayer          player;
    private GoPoint           point;
    
    public GoMove(final GoPlayer player, final GoPoint point)
    {
        super();
        this.setPlayer(player);
        this.setPoint(point);
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
        
        if (!(obj instanceof GoMove))
        {
            return false;
        }
        
        GoMove other = (GoMove)obj;
        
        if (this.player == null)
        {
            if (other.player != null)
            {
                return false;
            }
        }
        else if (!this.player.equals(other.player))
        {
            return false;
        }
        
        if (this.point == null)
        {
            if (other.point != null)
            {
                return false;
            }
        }
        else if (!this.point.equals(other.point))
        {
            return false;
        }
        
        return true;
    }
    
    public final GoPlayer getPlayer()
    {
        return this.player;
    }
    
    public final GoPoint getPoint()
    {
        return this.point;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.player == null) ? 0 : this.player.hashCode());
        result = (prime * result) + ((this.point == null) ? 0 : this.point.hashCode());
        return result;
    }
    
    protected final void setPlayer(final GoPlayer player)
    {
        this.player = player;
    }
    
    protected final void setPoint(final GoPoint point)
    {
        this.point = point;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("GoMove [");
        
        if (this.getPlayer() != null)
        {
            builder.append("Player=");
            builder.append(this.getPlayer());
            builder.append(", ");
        }
        
        if (this.getPoint() != null)
        {
            builder.append("Point=");
            builder.append(this.getPoint());
        }
        
        builder.append("]");
        return builder.toString();
    }
}