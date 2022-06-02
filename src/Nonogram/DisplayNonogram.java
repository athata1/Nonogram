package Nonogram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class DisplayNonogram extends JPanel implements MouseListener {
    private final String[][] board;
    private final int BOXSIZE;
    private final int[][] rowRules;
    private final int[][] colRules;
    public DisplayNonogram(String[][] board,int size, int[][] rowRules, int[][] colRules)
    {
        addMouseListener(this);
        setPreferredSize( new Dimension( 700, 700));
        this.board = board;
        BOXSIZE = size;
        this.rowRules = rowRules;
        this.colRules = colRules;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        //Draw Column Rules next to board
        for (int c = 0; c < board[0].length; c++)
        {
            g.drawRect(c*BOXSIZE + BOXSIZE*20/3,1,BOXSIZE,BOXSIZE*20/3);

            String[] column = new String[board.length];
            for (int r = 0; r < board.length; r++)
            {
                column[r] = board[r][c];
            }

            if (NonogramSolver.ruleMatches(column,colRules[c]))
            {
                g.setColor(new Color(181,232,232));
                g.fillRect(c*BOXSIZE + BOXSIZE*20/3,1,BOXSIZE,BOXSIZE*20/3);
                g.setColor(Color.BLACK);
            }

        }

        //Draw Row Rules next to board
        for (int r = 0; r < board.length; r++)
        {
            g.drawRect(1,r*BOXSIZE + BOXSIZE*20/3,BOXSIZE*20/3,BOXSIZE);
            if (NonogramSolver.ruleMatches(board[r],rowRules[r]))
            {
                g.setColor(new Color(181,232,232));
                g.fillRect(1,r*BOXSIZE + BOXSIZE*20/3,BOXSIZE*20/3,BOXSIZE);
                g.setColor(Color.BLACK);
            }
        }

        int val1 = BOXSIZE*20/3/(board.length/2+(board.length)%2);
        Font fr = new Font("Courier New", Font.BOLD,val1);
        g.setFont(fr);
        for (int r = 0; r < colRules.length; r++)
        {
            for (int c = 0; c < colRules[r].length; c++)
            {
                centerText(r*BOXSIZE + BOXSIZE*20/3,BOXSIZE*20/3-(colRules[r].length - 1 - c)*val1-val1,fr,BOXSIZE,val1,colRules[r][c] + "",g);
            }
        }

        int val2 = BOXSIZE*20/3/(board[0].length/2+(board[0].length)%2);
        fr = new Font("Courier New", Font.BOLD,val2);
        for (int r = 0; r < rowRules.length; r++)
        {
            for (int c = 0; c < rowRules[r].length; c++)
            {
                centerText(BOXSIZE*20/3-(rowRules[r].length - 1 - c)*val2-val2,r*BOXSIZE + BOXSIZE*20/3,fr,val2,BOXSIZE,rowRules[r][c] + "",g);
            }
        }


        //Draw Rules
        g.translate(BOXSIZE*20/3,BOXSIZE*20/3);
        g.setColor(new Color(255, 253, 208));
        g.fillRect(0,0,BOXSIZE*board[0].length,BOXSIZE*board.length);
        g.setColor(Color.BLACK);
        for (int r = 0; r < board.length; r++)
        {
            for (int c = 0; c < board[r].length; c++)
            {
                if (board[r][c].equals("S"))
                    g.fillRect(c*BOXSIZE,r*BOXSIZE,BOXSIZE,BOXSIZE);
                if (board[r][c].equals("X"))
                {
                    g.drawLine(c * BOXSIZE+BOXSIZE/6, r * BOXSIZE+BOXSIZE/6,c*BOXSIZE+BOXSIZE-BOXSIZE/6,r*BOXSIZE+BOXSIZE-BOXSIZE/6);
                    g.drawLine(c*BOXSIZE+BOXSIZE-BOXSIZE/6,r*BOXSIZE+BOXSIZE/6,c*BOXSIZE+BOXSIZE/6,r*BOXSIZE+BOXSIZE-BOXSIZE/6);
                }
            }
        }

        Font f = new Font("Courier New", Font.BOLD, BOXSIZE*2/3);
        g.setFont(f);
        for (int r = 0; r < board.length; r++)
        {
            centerText(board[0].length*BOXSIZE,r*BOXSIZE,f,BOXSIZE,BOXSIZE,(r+1) + "",g);
        }

        for (int c = 0; c < board[0].length; c++)
        {
            centerText(c*BOXSIZE,board.length*BOXSIZE,f,BOXSIZE,BOXSIZE,(c+1) + "",g);
        }

        g.setColor(Color.GRAY);
        for (int r = 0; r <= board.length; r++)
        {
            g.drawLine(0,r*BOXSIZE,board[0].length*BOXSIZE,r*BOXSIZE);
        }
        for (int c = 0; c <= board[0].length; c++)
        {
            g.drawLine(c*BOXSIZE,0,c*BOXSIZE,board.length*BOXSIZE);
        }
    }

    public void centerText(int x, int y, Font font, int width, int height, String s, Graphics g)
    {
        g.setFont(font);
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D r2D = font.getStringBounds(s, frc);
        double rWidth = r2D.getWidth();
        double rHeight = r2D.getHeight();
        double rX = r2D.getX();
        double rY = r2D.getY();

        int a = (int)Math.round((1.0 * width / 2) - (rWidth / 2) - rX);
        int c = (int)Math.round((1.0 * height / 2) - (rHeight / 2) - rY);
        g.drawString(s, x + a, y + c);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        int r = (e.getY() - BOXSIZE*20/3)/BOXSIZE;
        int c = (e.getX() - BOXSIZE*20/3)/BOXSIZE;
        if (r <= board.length - 1 && c <= board[0].length - 1)
        {
            if (board[r][c].equals(""))
            {
                if (e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK)
                    board[r][c] = "S";
                else if (e.getModifiersEx() == InputEvent.BUTTON3_DOWN_MASK)
                    board[r][c] = "X";
            }
            else
                board[r][c] = "";
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}