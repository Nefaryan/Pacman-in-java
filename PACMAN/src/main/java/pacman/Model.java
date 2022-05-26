package pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


    public class Model extends JPanel implements ActionListener {

        private Dimension d;
        private final Font smallFont = new Font("Arial", Font.ITALIC, 14);
        private boolean inGame = false;
        private boolean dying = false;

        private final int Block_Size = 24;
        private final int Numbers_Of_Blocks = 15;
        private final int Screen_Size = Numbers_Of_Blocks * Block_Size;
        private final int Max_Ghost = 12;
        private final int Speed_Of_pacman = 6;


        private int Numbers_of_Ghosty = 6;
        private int lives, score;
        private int[] dx, dy;
        private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

        private Image heart, ghost;
        private Image up, down, left, right;

        private int pacman_x, pacman_y, pacmand_x, pacmand_y;
        private int req_dx, req_dy;


        /**
         * Il valore 0 rappresenta i muri blu
         * Il valore 1 rappresenta il bordi sinitro
         * Il valore 2 rappresenta il bordi superiore
         * Il valore 4 rappresenta il bordo destro
         * Il valore 8 rappresenta il bordo inferiore
         * il valore 16 rappresenta invece i pallini bianchi
         * Gli aglomerati di 0 formano i muri
         * Sonnamdo gli altri numeri tra di loro si decide dove posizzionare i pallini
         */
        private final short levelData[] = {
                19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
                17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
                25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
                0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
                19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
                17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
                17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
                17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
                17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
                17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
                21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20,
                17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
                17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
                17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
                25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
        };

        private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
        private final int maxSpeed = 6;

        private int currentSpeed = 3;
        private short[] screenData;
        private Timer timer;

        public Model() {

            loadImages();
            initVariables();
            addKeyListener(new TAdapter());
            setFocusable(true);
            initGame();
        }

        /**
         * Qui vengono craicate le immagini
         */
        private void loadImages() {
            down = new ImageIcon("C:\\Users\\giuse\\Desktop\\Develhope\\Nuovi Esercizi Java\\PACMAN\\project_PACMAN\\down.gif").getImage();
            up = new ImageIcon("C:\\Users\\giuse\\Desktop\\Develhope\\Nuovi Esercizi Java\\PACMAN\\project_PACMAN\\up.gif").getImage();
            left = new ImageIcon("C:\\Users\\giuse\\Desktop\\Develhope\\Nuovi Esercizi Java\\PACMAN\\project_PACMAN\\left.gif").getImage();
            right = new ImageIcon("C:\\Users\\giuse\\Desktop\\Develhope\\Nuovi Esercizi Java\\PACMAN\\project_PACMAN\\right.gif").getImage();
            ghost = new ImageIcon("C:\\Users\\giuse\\Desktop\\Develhope\\Nuovi Esercizi Java\\PACMAN\\project_PACMAN\\ghost.gif").getImage();
            heart = new ImageIcon("C:\\Users\\giuse\\Desktop\\Develhope\\Nuovi Esercizi Java\\PACMAN\\project_PACMAN\\heart.png").getImage();

        }

        /**
         * Inizializza le variabili princiapli
         */
        private void initVariables() {

            screenData = new short[Numbers_Of_Blocks * Numbers_Of_Blocks];
            d = new Dimension(400, 400);
            ghost_x = new int[Max_Ghost];
            ghost_dx = new int[Max_Ghost];
            ghost_y = new int[Max_Ghost];
            ghost_dy = new int[Max_Ghost];
            ghostSpeed = new int[Max_Ghost];
            dx = new int[4];
            dy = new int[4];

            timer = new Timer(40, this);
            timer.start();
        }

        private void playGame(Graphics2D g2d) {

            if (dying) {

                death();

            } else {

                movePacman();
                drawPacman(g2d);
                moveGhosts(g2d);
                checkMaze();
            }
        }

        private void showIntroScreen(Graphics2D g2d) {

            String start = "Press SPACE to start";
            g2d.setColor(Color.yellow);
            g2d.drawString(start, (Screen_Size)/4, 150);
        }

        private void drawScore(Graphics2D g) {
            g.setFont(smallFont);
            g.setColor(new Color(5, 181, 79));
            String s = "Score: " + score;
            g.drawString(s, Screen_Size / 2 + 96, Screen_Size + 16);

            for (int i = 0; i < lives; i++) {
                g.drawImage(heart, i * 28 + 8, Screen_Size + 1, this);
            }
        }

        /**
         * Crea e Controlla che i muri siano coretti
         */
        private void checkMaze() {

            int i = 0;
            boolean finished = true;

            while (i < Numbers_Of_Blocks * Numbers_Of_Blocks && finished) {

                if ((screenData[i]) != 0) {
                    finished = false;
                }

                i++;
            }

            if (finished) {

                score += 50;

                if (Numbers_of_Ghosty < Max_Ghost) {
                    Numbers_of_Ghosty++;
                }

                if (currentSpeed < maxSpeed) {
                    currentSpeed++;
                }

                initLevel();
            }
        }

        private void death() {

            lives--;

            if (lives == 0) {
                inGame = false;
            }

            continueLevel();
        }

        /**
         * Questo metodo muove i fantasmini
         * @param g2d grafica
         */
        private void moveGhosts(Graphics2D g2d) {

            int pos;
            int count;

            for (int i = 0; i < Numbers_of_Ghosty; i++) {
                if (ghost_x[i] % Block_Size == 0 && ghost_y[i] % Block_Size == 0) {
                    pos = ghost_x[i] / Block_Size + Numbers_Of_Blocks * (int) (ghost_y[i] / Block_Size);

                    count = 0;

                    if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                        dx[count] = -1;
                        dy[count] = 0;
                        count++;
                    }

                    if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                        dx[count] = 0;
                        dy[count] = -1;
                        count++;
                    }

                    if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                        dx[count] = 1;
                        dy[count] = 0;
                        count++;
                    }

                    if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                        dx[count] = 0;
                        dy[count] = 1;
                        count++;
                    }

                    if (count == 0) {

                        if ((screenData[pos] & 15) == 15) {
                            ghost_dx[i] = 0;
                            ghost_dy[i] = 0;
                        } else {
                            ghost_dx[i] = -ghost_dx[i];
                            ghost_dy[i] = -ghost_dy[i];
                        }

                    } else {

                        count = (int) (Math.random() * count);

                        if (count > 3) {
                            count = 3;
                        }

                        ghost_dx[i] = dx[count];
                        ghost_dy[i] = dy[count];
                    }

                }

                ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
                ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
                drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

                if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                        && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                        && inGame) {

                    dying = true;
                }
            }
        }

        /**
         * Questo metodo disgna i fantasmini
         * @param g2d grafica dei fatasmini
         * @param x valore
         * @param y valore
         */
        private void drawGhost(Graphics2D g2d, int x, int y) {
            g2d.drawImage(ghost, x, y, this);
        }

        /**
         * Questo metodo permette il movimento di pacman
         */
        private void movePacman() {

            int pos;
            short ch;

            if (pacman_x % Block_Size == 0 && pacman_y % Block_Size == 0) {
                pos = pacman_x / Block_Size + Numbers_Of_Blocks * (int) (pacman_y / Block_Size);
                ch = screenData[pos];

                if ((ch & 16) != 0) {
                    screenData[pos] = (short) (ch & 15);
                    score++;
                }

                if (req_dx != 0 || req_dy != 0) {
                    if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                            || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                            || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                            || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                        pacmand_x = req_dx;
                        pacmand_y = req_dy;
                    }
                }

                // Check for standstill
                if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                        || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                        || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                        || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                    pacmand_x = 0;
                    pacmand_y = 0;
                }
            }
            pacman_x = pacman_x + Speed_Of_pacman * pacmand_x;
            pacman_y = pacman_y + Speed_Of_pacman * pacmand_y;
        }

        private void drawPacman(Graphics2D g2d) {

            if (req_dx == -1) {
                g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this);
            } else if (req_dx == 1) {
                g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this);
            } else if (req_dy == -1) {
                g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this);
            } else {
                g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this);
            }
        }

        private void drawMaze(Graphics2D g2d) {

            short i = 0;
            int x, y;

            for (y = 0; y < Screen_Size; y += Block_Size) {
                for (x = 0; x < Screen_Size; x += Block_Size) {

                    g2d.setColor(new Color(0,72,251));
                    g2d.setStroke(new BasicStroke(5));

                    if ((levelData[i] == 0)) {
                        g2d.fillRect(x, y, Block_Size, Block_Size);
                    }

                    if ((screenData[i] & 1) != 0) {
                        g2d.drawLine(x, y, x, y + Block_Size - 1);
                    }

                    if ((screenData[i] & 2) != 0) {
                        g2d.drawLine(x, y, x + Block_Size - 1, y);
                    }

                    if ((screenData[i] & 4) != 0) {
                        g2d.drawLine(x + Block_Size - 1, y, x + Block_Size - 1,
                                y + Block_Size - 1);
                    }

                    if ((screenData[i] & 8) != 0) {
                        g2d.drawLine(x, y + Block_Size - 1, x + Block_Size - 1,
                                y + Block_Size - 1);
                    }

                    if ((screenData[i] & 16) != 0) {
                        g2d.setColor(new Color(255,255,255));
                        g2d.fillOval(x + 10, y + 10, 6, 6);
                    }

                    i++;
                }
            }
        }

        private void initGame() {

            lives = 3;
            score = 0;
            initLevel();
            Numbers_of_Ghosty = 6;
            currentSpeed = 3;
        }

        private void initLevel() {

            int i;
            for (i = 0; i < Numbers_Of_Blocks * Numbers_Of_Blocks; i++) {
                screenData[i] = levelData[i];
            }

            continueLevel();
        }

        private void continueLevel() {

            int dx = 1;
            int random;

            for (int i = 0; i < Numbers_of_Ghosty; i++) {

                ghost_y[i] = 4 * Block_Size; //start position
                ghost_x[i] = 4 * Block_Size;
                ghost_dy[i] = 0;
                ghost_dx[i] = dx;
                dx = -dx;
                random = (int) (Math.random() * (currentSpeed + 1));

                if (random > currentSpeed) {
                    random = currentSpeed;
                }

                ghostSpeed[i] = validSpeeds[random];
            }

            pacman_x = 7 * Block_Size;  //start position
            pacman_y = 11 * Block_Size;
            pacmand_x = 0;	//reset direction move
            pacmand_y = 0;
            req_dx = 0;		// reset direction controls
            req_dy = 0;
            dying = false;
        }


        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, d.width, d.height);

            drawMaze(g2d);
            drawScore(g2d);

            if (inGame) {
                playGame(g2d);
            } else {
                showIntroScreen(g2d);
            }

            Toolkit.getDefaultToolkit().sync();
            g2d.dispose();
        }


        /**
         * Questa classe e stat creata per poter gestire il movimento con
         * le frecce direzionali
         */
        class TAdapter extends KeyAdapter {

            @Override
            public void keyPressed(KeyEvent e) {

                int key = e.getKeyCode();

                if (inGame) {
                    if (key == KeyEvent.VK_LEFT) {
                        req_dx = -1;
                        req_dy = 0;
                    } else if (key == KeyEvent.VK_RIGHT) {
                        req_dx = 1;
                        req_dy = 0;
                    } else if (key == KeyEvent.VK_UP) {
                        req_dx = 0;
                        req_dy = -1;
                    } else if (key == KeyEvent.VK_DOWN) {
                        req_dx = 0;
                        req_dy = 1;
                    } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                        inGame = false;
                    }
                } else {
                    if (key == KeyEvent.VK_SPACE) {
                        inGame = true;
                        initGame();
                    }
                }
            }
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            repaint();
        }

    }








