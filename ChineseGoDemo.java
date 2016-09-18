import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import api.gui.swing.ApplicationWindow;
import api.util.EventHandler;
import api.util.Support;

public class ChineseGoDemo implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;
    
    public final static void main(final String[] args)
    {
        new ChineseGoDemo(args);
    }
    
    public static void printException(final Component parent, final Exception e)
    {
        Support.displayException(parent, e, false);
    }
    
    public static void printWinMessage(final Component parent, final String winMessage)
    {
        JOptionPane.showMessageDialog(parent, winMessage, "Game Over", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }
    
    private JLabel            currentPlayer = null;
    private GoGame            goGame        = null;
    private GoGui             goGui         = null;
    private boolean           isDebugging   = false;
    private boolean           isHumanBlack  = false;
    
    private Integer           size          = GoGame.DEFAULT_SIZES[0];
    
    private ApplicationWindow window        = null;
    
    public ChineseGoDemo(final String[] args)
    {
        this.setDebugging(Support.promptDebugMode(this.getWindow()));
        this.setSize(Support.getIntegerSelectionString(this.getWindow(), "Size of board?", "Choose Board Size", GoGame.DEFAULT_SIZES));
        this.setHumanBlack(Support.getChoiceInput(this.getWindow(),
            "Do you want to play as Black? Selecting \"No\" will give you White.",
            "Choose Player Color"));
        this.setGoGame(new GoGame(this.isHumanBlack(), this.getSize(), true, this));
        this.setGoGui(new GoGui(this.getGoGame(), this));
        this.setCurrentPlayer(new JLabel());
        
        // @formatter:off
        // Define a self-contained ActionListener event handler.
        EventHandler<ChineseGoDemo> myActionPerformed = new EventHandler<ChineseGoDemo>(this)
            {
            private final static long serialVersionUID = 1L;
            
            @Override
            public final void run(final AWTEvent event)
            {
                ActionEvent actionEvent = (ActionEvent)event;
                ChineseGoDemo parent = this.getParent();
                
                /*
                 * JDK 7 allows string objects as the expression in a switch statement. This generally produces more efficient byte code compared to a
                 * chain of if statements. http://docs.oracle.com/javase/7/docs/technotes/guides/language/strings-switch.html
                 */
                if (parent.getGoGame().isHumanTurn())
                {
                    switch (actionEvent.getActionCommand())
                    {
                        case "Pass":

                            parent.pass();
                            break;

                        case "Resign":

                            parent.resign();
                            break;

                        case "Resume Game":

                            parent.openOrSaveFile(parent.getWindow(), true, parent.isDebugging());
                            break;

                        case "Save Game":

                            parent.openOrSaveFile(parent.getWindow(), false, parent.isDebugging());
                            break;

                        default:

                            break;
                    }
                }
            }
            };
            
            // Define a self-contained interface construction event handler.
            EventHandler<ChineseGoDemo> myDrawGUI = new EventHandler<ChineseGoDemo>(this)
                {
                private final static long serialVersionUID = 1L;
                
                @Override
                public final void run(final ApplicationWindow window)
                {
                    ChineseGoDemo parent = this.getParent();
                    Container contentPane = window.getContentPane();
                    JMenuBar menuBar = new JMenuBar();
                    JMenu fileMenu = new JMenu("File");
                    JMenuItem resumeOption = new JMenuItem("Resume Game");
                    JMenuItem saveOption = new JMenuItem("Save Game");
                    JPanel labelPanel = new JPanel();
                    JPanel goGuiPanel = new JPanel();
                    JPanel buttonPanel = new JPanel();
                    JButton pass = new JButton("Pass");
                    JButton resign = new JButton("Resign");
                    
                    String currentPlayerString = parent.getGoGame().getPlayerString(parent.getGoGame().getCurrentPlayer());
                    parent.getCurrentPlayer().setText("Current Player: " + currentPlayerString);
                    
                    menuBar.setFont(Support.DEFAULT_TEXT_FONT);
                    fileMenu.setFont(Support.DEFAULT_TEXT_FONT);
                    resumeOption.setFont(Support.DEFAULT_TEXT_FONT);
                    saveOption.setFont(Support.DEFAULT_TEXT_FONT);
                    contentPane.setLayout(new BorderLayout());
                    resumeOption.addActionListener(window);
                    fileMenu.add(resumeOption);
                    saveOption.addActionListener(window);
                    fileMenu.add(saveOption);
                    menuBar.add(fileMenu);
                    window.setJMenuBar(menuBar);
                    fileMenu.setMnemonic('F');
                    resumeOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.Event.CTRL_MASK));
                    resumeOption.setMnemonic('O');
                    saveOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
                    saveOption.setMnemonic('S');
                    labelPanel.setLayout(new FlowLayout());
                    labelPanel.add(parent.getCurrentPlayer());
                    goGuiPanel.setLayout(new FlowLayout());
                    goGuiPanel.add(parent.getGoGui());
                    buttonPanel.setLayout(new FlowLayout());
                    buttonPanel.add(pass);
                    buttonPanel.add(resign);
                    contentPane.setLayout(new BorderLayout());
                    contentPane.add(labelPanel, BorderLayout.NORTH);
                    contentPane.add(goGuiPanel, BorderLayout.CENTER);
                    contentPane.add(buttonPanel, BorderLayout.SOUTH);
                    pass.addActionListener(window);
                    pass.setFont(Support.DEFAULT_TEXT_FONT);
                    resign.addActionListener(window);
                    resign.setFont(Support.DEFAULT_TEXT_FONT);
                }
                };
                
                this.setWindow(new ApplicationWindow(null,
                    "Chinese Go Demo",
                    new Dimension(1000, 750),
                    this.isDebugging(),
                    false,
                    myActionPerformed,
                    myDrawGUI));
                this.getWindow().pack();
                // @formatter:on
    }
    
    public final JLabel getCurrentPlayer()
    {
        return this.currentPlayer;
    }
    
    public final GoGame getGoGame()
    {
        return this.goGame;
    }
    
    public final GoGui getGoGui()
    {
        return this.goGui;
    }
    
    public final Integer getSize()
    {
        return this.size;
    }
    
    public final ApplicationWindow getWindow()
    {
        return this.window;
    }
    
    public final boolean isDebugging()
    {
        return this.isDebugging;
    }
    
    public final boolean isHumanBlack()
    {
        return this.isHumanBlack;
    }
    
    public void openOrSaveFile(final Component parent, final boolean isOpen, final boolean isDebugging)
    {
        Object stream = null;
        String filePath = Support.getFilePath(parent, isOpen, isDebugging);
        
        if ((filePath == null) || filePath.isEmpty())
        {
            return;
        }
        
        try
        {
            if (isOpen)
            {
                // Use binary file manipulation to import a file containing a GoGui object.
                stream = new ObjectInputStream(new FileInputStream(filePath));
                this.setGoGame((GoGame)((ObjectInputStream)stream).readObject());
                this.getGoGame().setParent(this);
                // Redraw the GUI once a new GoGame has been loaded.
                this.setGoGui(new GoGui(this.getGoGame(), this));
                this.getWindow().reDrawGUI();
            }
            else
            {
                // Use binary file manipulation to export a file containing a GoGui object.
                stream = new ObjectOutputStream(new FileOutputStream(filePath));
                ((ObjectOutputStream)stream).writeObject(this.getGoGame());
            }
        }
        catch (final Exception e)
        {
            ChineseGoDemo.printException(this.getWindow(), e);
        }
        finally
        {
            if (stream != null)
            {
                try
                {
                    if (isOpen)
                    {
                        ((ObjectInputStream)stream).close();
                    }
                    else
                    {
                        ((ObjectOutputStream)stream).close();
                    }
                }
                catch (final Exception e)
                {
                    ChineseGoDemo.printException(this.getWindow(), e);
                }
            }
        }
    }
    
    protected void pass()
    {
        this.getGoGui().getGoGame().handleAction(GoGame.ACTION_TYPE.PASS, null);
    }
    
    protected void resign()
    {
        this.getGoGui().getGoGame().handleAction(GoGame.ACTION_TYPE.RESIGN, null);
    }
    
    protected final void setCurrentPlayer(final JLabel currentPlayer)
    {
        this.currentPlayer = currentPlayer;
    }
    
    public final void setDebugging(final boolean isDebugging)
    {
        this.isDebugging = isDebugging;
    }
    
    protected final void setGoGame(final GoGame goGame)
    {
        this.goGame = goGame;
    }
    
    public final void setGoGui(final GoGui goGui)
    {
        this.goGui = goGui;
    }
    
    public final void setHumanBlack(final boolean isHumanBlack)
    {
        this.isHumanBlack = isHumanBlack;
    }
    
    public final void setSize(final Integer size)
    {
        this.size = size;
    }
    
    public final void setWindow(final ApplicationWindow window)
    {
        this.window = window;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ChineseGoDemo [");
        
        if (this.getCurrentPlayer() != null)
        {
            builder.append("CurrentPlayer=");
            builder.append(this.getCurrentPlayer());
            builder.append(", ");
        }
        
        if (this.getGoGame() != null)
        {
            builder.append("GoGame=");
            builder.append(this.getGoGame());
            builder.append(", ");
        }
        
        if (this.getGoGui() != null)
        {
            builder.append("GoGui=");
            builder.append(this.getGoGui());
            builder.append(", ");
        }
        
        if (this.getSize() != null)
        {
            builder.append("Size=");
            builder.append(this.getSize());
            builder.append(", ");
        }
        
        if (this.getWindow() != null)
        {
            builder.append("Window=");
            builder.append(this.getWindow());
            builder.append(", ");
        }
        
        builder.append("isDebugging=");
        builder.append(this.isDebugging());
        builder.append(", isHumanBlack=");
        builder.append(this.isHumanBlack());
        builder.append("]");
        return builder.toString();
    }
}