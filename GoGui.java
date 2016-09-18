import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.Arrays;

import javax.swing.JPanel;

import api.util.Support;

public class GoGui extends JPanel
{
    protected final static class GoCell extends JPanel
    {
        private static final long serialVersionUID = 1L;
        private GoCellContent     goCellContent    = null;
        private GoGui             goGui            = null;
        private GoPoint           goPoint          = null;
        
        public GoCell(final GoPoint goPoint, final GoGui goGui)
        {
            super();
            this.setBackground(GoGui.DEFAULT_BG_COLOR);
            this.setGoGui(goGui);
            this.setGoPoint(goPoint);
            this.setGoCellContent(new GoCellContent(this));
            this.add(this.getGoCellContent());
            this.addMouseListener(this.getGoCellContent());
        }
        
        public final GoCellContent getGoCellContent()
        {
            return this.goCellContent;
        }
        
        public final GoGui getGoGui()
        {
            return this.goGui;
        }
        
        public final GoPoint getGoPoint()
        {
            return this.goPoint;
        }
        
        @Override
        public Dimension getPreferredSize()
        {
            return (new Dimension((this.getParent().getWidth() / 10), (this.getParent().getHeight() / 10)));
        }
        
        protected final void setGoCellContent(final GoCellContent goCellContent)
        {
            this.goCellContent = goCellContent;
        }
        
        protected final void setGoGui(final GoGui goGui)
        {
            this.goGui = goGui;
        }
        
        protected final void setGoPoint(final GoPoint goPoint)
        {
            this.goPoint = goPoint;
        }
        
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("GoCell [");
            
            if (this.getGoCellContent() != null)
            {
                builder.append("GoCellContent=");
                builder.append(this.getGoCellContent());
                builder.append(", ");
            }
            
            if (this.getGoGui() != null)
            {
                builder.append("GoGui=");
                builder.append(this.getGoGui());
                builder.append(", ");
            }
            
            if (this.getGoPoint() != null)
            {
                builder.append("GoPoint=");
                builder.append(this.getGoPoint());
                builder.append(", ");
            }
            
            if (this.getPreferredSize() != null)
            {
                builder.append("PreferredSize=");
                builder.append(this.getPreferredSize());
            }
            
            builder.append("]");
            return builder.toString();
        }
    }
    
    protected final static class GoCellContent extends JPanel implements MouseListener
    {
        private static final long serialVersionUID = 1L;
        private GoCell            goCell           = null;
        
        public GoCellContent(final GoCell goCell)
        {
            super();
            this.setBackground(GoGui.DEFAULT_BG_COLOR);
            this.setGoCell(goCell);
        }
        
        public final GoCell getGoCell()
        {
            return this.goCell;
        }
        
        public final Color getOccupationState()
        {
            return this.getGoCell().getGoPoint().getOccupationState();
        }
        
        @Override
        public Dimension getPreferredSize()
        {
            return (new Dimension((this.getParent().getWidth() / 2), (this.getParent().getHeight() / 2)));
        }
        
        @Override
        public void mouseClicked(final MouseEvent e)
        {
            GoGame game = this.getGoCell().getGoGui().getGoGame();
            ChineseGoDemo window = this.getGoCell().getGoGui().getWindow();
            
            if (e.getButton() == MouseEvent.BUTTON1) // Is it a left click?
            {
                GoPoint point = this.getGoCell().getGoPoint();
                
                if (window.isDebugging()) // Print debugging info if applicable.
                {
                    System.out.println("Clicked on " + point);
                }
                
                if (this.getGoCell().getGoGui().getGoGame().isHumanTurn()) // Is it the human player's turn?
                {
                    game.handleAction(GoGame.ACTION_TYPE.MOVE, point);
                }
            }
        }
        
        @Override
        public void mouseEntered(final MouseEvent e)
        {
            // Do nothing.
        }
        
        @Override
        public void mouseExited(final MouseEvent e)
        {
            // Do nothing.
        }
        
        @Override
        public void mousePressed(final MouseEvent e)
        {
            // Do nothing.
        }
        
        @Override
        public void mouseReleased(final MouseEvent e)
        {
            // Do nothing.
        }
        
        @Override
        protected void paintComponent(final Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D)g;
            RectangularShape point = null;
            double radius = Math.max((double)this.getWidth(), (double)this.getHeight()) / 2.0;
            
            if (this.getGoCell().getGoPoint().isStarPoint(this.getGoCell().getGoGui().getGoGame().getGameBoard()))
            {
                point = new Ellipse2D.Double(this.getX(), this.getY(), radius, radius);
                
            }
            else
            {
                point = new Rectangle2D.Double(this.getX(), this.getY(), radius, radius);
            }
            
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.setColor(this.getOccupationState());
            g2D.fill(point);
        }
        
        protected final void setGoCell(final GoCell goCell)
        {
            this.goCell = goCell;
        }
        
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("GoCellContent [");
            
            if (this.getGoCell() != null)
            {
                builder.append("GoCell=");
                builder.append(this.getGoCell());
                builder.append(", ");
            }
            
            if (this.getOccupationState() != null)
            {
                builder.append("OccupationState=");
                builder.append(Support.getColorString(this.getOccupationState()));
                builder.append(", ");
            }
            
            if (this.getPreferredSize() != null)
            {
                builder.append("PreferredSize=");
                builder.append(this.getPreferredSize());
            }
            
            builder.append("]");
            return builder.toString();
        }
    }
    
    protected final static class GoGrid extends JPanel
    {
        private static final long serialVersionUID = 1L;
        private GoGui             goGui            = null;
        
        GoGrid(final GoGui goGui)
        {
            super();
            this.setGoGui(goGui);
            this.setBackground(GoGui.DEFAULT_BG_COLOR);
            this.setLayout(new GridLayout(goGui.getGoGame().getGameBoard().length, goGui.getGoGame().getGameBoard().length, 0, 0));
            this.removeAll();
            
            for (int y = 0; y < goGui.getGoGame().getGameBoard().length; y++)
            {
                for (int x = 0; x < goGui.getGoGame().getGameBoard().length; x++)
                {
                    GoCell goCell = new GoCell(goGui.getGoGame().getGameBoard()[x][y], goGui);
                    this.add(goCell);
                }
            }
        }
        
        public final GoPoint[][] getGameBoard()
        {
            return this.getGoGui().getGoGame().getGameBoard();
        }
        
        public final GoGui getGoGui()
        {
            return this.goGui;
        }
        
        @Override
        public Dimension getPreferredSize()
        {
            return (new Dimension(550, 550));
        }
        
        protected final void setGoGui(final GoGui goGui)
        {
            this.goGui = goGui;
        }
        
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("GoGrid [");
            
            if (this.getGameBoard() != null)
            {
                builder.append("GameBoard=");
                builder.append(Arrays.toString(this.getGameBoard()));
                builder.append(", ");
            }
            
            if (this.getGoGui() != null)
            {
                builder.append("GoGui=");
                builder.append(this.getGoGui());
                builder.append(", ");
            }
            
            if (this.getPreferredSize() != null)
            {
                builder.append("PreferredSize=");
                builder.append(this.getPreferredSize());
            }
            
            builder.append("]");
            return builder.toString();
        }
    }
    
    private static final Color DEFAULT_BG_COLOR = Color.BLUE;
    private static final long  serialVersionUID = 1L;
    private GoGame             goGame           = null;
    private ChineseGoDemo      window           = null;
    
    public GoGui(final GoGame game, final ChineseGoDemo window)
    {
        super();
        this.setBackground(GoGui.DEFAULT_BG_COLOR);
        this.setGoGame(game);
        this.setWindow(window);
        this.add(new GoGrid(this));
    }
    
    public final GoGame getGoGame()
    {
        return this.goGame;
    }
    
    public final ChineseGoDemo getWindow()
    {
        return this.window;
    }
    
    protected final void setGoGame(final GoGame goGame)
    {
        this.goGame = goGame;
    }
    
    protected final void setWindow(final ChineseGoDemo window)
    {
        this.window = window;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("GoGui [");
        
        if (this.getGoGame() != null)
        {
            builder.append("GoGame=");
            builder.append(this.getGoGame());
            builder.append(", ");
        }
        
        if (this.getWindow() != null)
        {
            builder.append("Window=");
            builder.append(this.getWindow());
        }
        
        builder.append("]");
        return builder.toString();
    }
}