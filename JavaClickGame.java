import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class AI_ClickGame {

    // ── Inner class: GamePanel ────────────────────────────────────────────────
    static class GamePanel extends JPanel implements MouseListener {
 
        private long startTime;//stores the time when the game has began
        private ArrayList<Rectangle> currentSquares;//stores all the generated squares (from which can be popped when clicked)
        private JLabel timerLabel; // JLabel used to display the time
 
        /** The contructor of the class.
            Registers the mouse listener, generates random squares, starts the timer, and sets the background.

            @param timerLabel JLabel will be taken from outside this class to display the time when completed
            @param numberOfSquares It tells how many squares to generate
        */
        GamePanel(JLabel timerLabel, int numberOfSquares) {
            this.currentSquares = makeRandomSquares(numberOfSquares);
            this.timerLabel = timerLabel;
            this.startTime = System.currentTimeMillis();
            setBackground(new Color(245, 245, 245));
            addMouseListener(this);
        }
 
        /** Declares and Initializes the arraylist by making random squarees. Used only by the constructor of this class

            @param count Tells how many squares to generate
            @return  array list of rectangles with random sizes and positions while not intersecting with one another
         */
        private ArrayList<Rectangle> makeRandomSquares(int count) {
            ArrayList<Rectangle> squares = new ArrayList<>();
            Random rand = new Random();
            int panelWidth  = 700;
            int panelHeight = 580;
            int minSize     = 40;
            int maxSize     = 120;
            int maxAttempts = 200;
 
            while (squares.size() < count && maxAttempts > 0) {
                int size = minSize + rand.nextInt(maxSize - minSize + 1);
                int x    = rand.nextInt(panelWidth  - size);
                int y    = rand.nextInt(panelHeight - size);
                Rectangle candidate = new Rectangle(x, y, size, size);
 
                boolean overlaps = false;
                for (Rectangle existing : squares) {
                    if (candidate.intersects(existing)) { overlaps = true; break; }
                }
                if (!overlaps) squares.add(candidate);
                maxAttempts--;
            }
            return squares;
        }
 
        /** Draws all remaining squares as filled rectangles with a black border.
            From the superclass 'JPanel'.

            @param g The graphics context used for ainting and rendering
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            for (Rectangle sq : currentSquares) {
                g2.setColor(new Color(70, 130, 180));
                g2.fillRect(sq.x, sq.y, sq.width, sq.height);
                g2.setColor(Color.BLACK);
                g2.drawRect(sq.x, sq.y, sq.width, sq.height);
            }
        }
 
        /**
         * Checks if click lands on a square, removes it, and displays
         * elapsed time when all squares are cleared. From interface 'MouseListener'

           @param e icontains details about the mouse click like the position where the click occured
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            Rectangle hit = null;
            for (Rectangle sq : currentSquares)
                if (sq.contains(e.getPoint())) { hit = sq; break; }
            if (hit != null) {
                currentSquares.remove(hit);
                repaint();
                if (currentSquares.isEmpty()) {
                    double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
                    timerLabel.setText("Done! Time: " + elapsed + " seconds");
                }
            }
        }
 
        @Override public void mousePressed(MouseEvent e)  {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e)  {}
        @Override public void mouseExited(MouseEvent e)   {}
    }

    //All of these below are global scope as there are multiple functions that need to interact with them
    
    JFrame mainFrame;// the frame where we  get information about how many squares to draw
    JPanel inputPanel;// It stores the components used for 'mainFram' above
    JPanel gameContainer;// stores the gamePanel object
    GamePanel gamePanel;// stores all of the squares and the gameLabel
    JTextField numOfSquaresInput;//the number of squares will be entered here
    JLabel errorLabel;//When Invalid input is typed, this label show why it is invalid
    JLabel gameLabel = new JLabel("Click/Select all the squares.", SwingConstants.CENTER);//label telling user what to do
    JButton confirmInput;// butoon used to confirm user's choice on the number of squares

    /*
        Constructor of the outer class, which will call the methods below to start the game.
        Then, makes the mainFrame visible
    */
    AI_ClickGame(){
        buildFrame();
        buildInputPanel();
        wireListeners();
        mainFrame.add(inputPanel);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    /*
        Creates the mainFrame with a title and a set size.
    */
    void buildFrame(){
        mainFrame = new JFrame("Click Game");
        mainFrame.setSize(new Dimension(700, 700));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /*
        Adds content like JLabels, JTextFields, and JButtons into the inputPanel.
    */
    void buildInputPanel(){
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JLabel jl1 = new JLabel("Click Speed Game");
        jl1.setFont(new Font("Arial", Font.BOLD, 28));
        jl1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jl2 = new JLabel("Enter a number between 4 and 8 (inclusive)");
        jl2.setFont(new Font("Arial", Font.PLAIN, 18));
        jl2.setAlignmentX(Component.CENTER_ALIGNMENT);

        numOfSquaresInput = new JTextField(15);
        numOfSquaresInput.setFont(new Font("Arial", Font.PLAIN, 22));
        numOfSquaresInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        numOfSquaresInput.setMaximumSize(numOfSquaresInput.getPreferredSize());

        confirmInput = new JButton("Begin");
        confirmInput.setFont(new Font("Arial", Font.PLAIN, 18));
        confirmInput.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputPanel.add(Box.createVerticalGlue());
        inputPanel.add(jl1);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(jl2);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(numOfSquaresInput);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(confirmInput);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(errorLabel);
        inputPanel.add(Box.createVerticalGlue());
    }

    /*
        Adds the content like the gamePanel and the resetButton to the gameContainer
    */
    void buildGamePanel(int squareCount){
        gameContainer = new JPanel(new BorderLayout());

        gameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gameLabel.setOpaque(true);
        gameLabel.setBackground(Color.WHITE);
        gameLabel.setPreferredSize(new Dimension(700, 45));

        gamePanel = new GamePanel(gameLabel, squareCount);

        JPanel bottomPanel = new JPanel();
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.PLAIN, 18));
        resetButton.addActionListener(e -> {
            numOfSquaresInput.setText("");
            errorLabel.setText(" ");
            gameLabel.setText("Click/Select all the squares.");
            mainFrame.setContentPane(inputPanel);
            mainFrame.invalidate();
            mainFrame.revalidate();
        });
        bottomPanel.add(resetButton);

        gameContainer.add(gameLabel,   BorderLayout.NORTH);
        gameContainer.add(gamePanel,   BorderLayout.CENTER);
        gameContainer.add(bottomPanel, BorderLayout.SOUTH);
    }

    /*
        Tells confirmInput button what to do upon being clicked
    */
    void wireListeners(){
        confirmInput.addActionListener( e -> {
            String text = numOfSquaresInput.getText().trim();

            try {
                int squareCount = Integer.parseInt(text);
                if(squareCount < 4 || squareCount > 8){
                    errorLabel.setText("Not in the range between 4 and 8! Please try again.");
                    return;
                }
                buildGamePanel(squareCount);
                gameLabel.setText("Click/Select all the squares.");
                mainFrame.setContentPane(gameContainer);
                mainFrame.invalidate();
                mainFrame.revalidate();
                gamePanel.repaint();
            }
            catch(NumberFormatException err){
                errorLabel.setText("Invalid Input! Please try again.");
            }
        });
    }
    // ── Main ──────────────────────────────────────────────────────────────────

    /*
        The main method. It calls the constructor. Thus, being the entry point
    */
    public static void main(String[] args) {
        new AI_ClickGame();
    }
}
/*
 * Program description:
 * This program is a two-phase square-clicking speed game in a single Java Swing file.
 * Phase 1 shows an input screen where the user types a number (4–8) into a JTextField
 * and clicks Begin (ActionListener) to proceed. Invalid input is shown on a red JLabel.
 * Phase 2 swaps to a GamePanel (JPanel subclass) that draws the N largest squares from
 * a set of 8 static squares of varying sizes using Graphics2D (fillRect + drawRect).
 * The timer starts as soon as Begin is clicked. A MouseListener detects square hits and
 * removes them; when all are cleared the elapsed time is shown in a JLabel. A Reset
 * JButton (ActionListener) returns to Phase 1.
*/
