import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 600;
    private final int B_HEIGHT = 600;
    //yılan ve elma boyutu için
    private final int DOT_SIZE = 10;
    //oyundaki toplam kare sayısı
    private final int ALL_DOTS = 900;
    //elmanın rastgele konumlanabileceği pozisyonları için
    private final int RAND_POS = 29;
    //timer da kullandığımız oyunun hızı için gecikme değeri
    private final int DELAY = 500;

    //yılanın ve elmanın koordinatları için
    // pozisyonlarını güncelleme ve çizme işlemlerinde kullanılır
    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;
    private int apple_x;
    private int apple_y;

    private int score = 0;
    private int highScore = 0;
    private int  elapsedSeconds = 0; // gecen süre

    private int level = 1;
    private final int LEVEL_UP_SCORE = 2;
    private int LEVEL_SPEED_DECREASE = 30;
    private JButton restartButton = new JButton("Yeniden Başla");
    private JPanel entryPanel;  // Giriş ekranı paneli
    private JButton startButton;
    //yılanın hareket yönleri için
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private boolean gamePaused = false; // Oyunun duraklatılıp duraklatılmadığını kontrol etmek için
    private boolean gameStarted = false;
    private long startTime;

    private Timer timer;
    private Image ball,apple,head, backgroundImage,backgroundImage2,backgroundImage3,backgroundImage4;


    public Board() {

        initBoard();

        restartButton.addActionListener(e -> restartGame());
        add(restartButton);
    }

    private void initBoard() {
        //klavye dinlemek için
        addKeyListener(new TAdapter());

        // Giriş ekranı panelini oluştur
        entryPanel = new JPanel(new BorderLayout());
        entryPanel.setBackground(Color.BLACK);
        setBackground(Color.GRAY);
        setFocusable(true);
        // Başla butonu
        startButton = new JButton("Başla");
        startButton.setFont(new Font("Arial", Font.PLAIN, 24));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(Color.DARK_GRAY);
        //butona basıldığında startGame methodunu çağırır
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        entryPanel.add(startButton, BorderLayout.CENTER);

        // Giriş ekranını ekle
        add(entryPanel);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }
    private void startGame() {
        // Oyun başladığında giriş ekranını gizle
        entryPanel.setVisible(false);

        // Oyunun geri kalanını başlat
        gameStarted = true;
    }
    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();

        ImageIcon iib1 = new ImageIcon("src/resources/background1.png");
        backgroundImage =iib1.getImage();

        ImageIcon iib2 = new ImageIcon("src/resources/background2.png");
        backgroundImage2 =iib2.getImage();

        ImageIcon iib3 = new ImageIcon("src/resources/background3.png");
        backgroundImage3 =iib3.getImage();
        ImageIcon iib4 = new ImageIcon("src/resources/entry.png");
        backgroundImage4 =iib4.getImage();
    }

    private void initGame() {

        dots = 1;
        highScore = 0;
        //yılanın başlangıç konumunu belirlenmesi
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 100;
        }

        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();

        startTime = System.currentTimeMillis(); // Oyun başladığındaki başlangıç zamanı
        restartButton.setVisible(false);

    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
        }
    }

    private void checkLevelUp() {
        int targetScore = level * LEVEL_UP_SCORE;
        if (score >= targetScore) {
            levelUp();
        }
    }
    private void levelUp() {
        level +=1;
        changeSnakeSpeed();

    }

    private void changeSnakeSpeed() {
        //negatif olmaması için math.max(0,..) kullanıldı
        int newDelay = Math.max(0, timer.getDelay() - LEVEL_SPEED_DECREASE);
        timer.setDelay(newDelay);
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameStarted) {
            // Oyun başladığında oyun ekranını çiz
            doDrawing(g);
        } else {
            // Oyun başlamadığında giriş ekranını çiz
            drawEntryScreen(g);
        }

    }
    private void drawEntryScreen(Graphics g) {
        // Giriş ekranını çiz
        g.setColor(Color.LIGHT_GRAY);
        g.drawImage(backgroundImage4, 0, 0, this);
        g.setFont(new Font("Arial", Font.PLAIN, 50));
        String message = "THE SNAKE!";
        g.drawString(message, (B_WIDTH - g.getFontMetrics().stringWidth(message)) / 2, B_HEIGHT / 3);

        startButton.setText("Başla");
        startButton.setVisible(true);
    }
    private void doDrawing(Graphics g) {

        if (inGame) {

            int modResult = level % 3;

            if (modResult == 0) {
                g.drawImage(backgroundImage, 0, 0, this);
            } else if (modResult == 1) {
                g.drawImage(backgroundImage2, 0, 0, this);
            } else if (modResult == 2) {
                g.drawImage(backgroundImage3, 0, 0, this);
            }
            g.drawImage(apple, apple_x, apple_y, this);
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }

        g.setColor(Color.white);
        g.drawString("Skor: " + score, 10, 20); // Skoru ekrana yazdır
        g.drawString("Geçen Süre: " + formatTime(elapsedSeconds), 10, 40); // Geçen süreyi ekrana yazdır
        g.drawString("Seviye: " + level, getWidth() - 130, 20);
        g.drawString("En Yüksek Skor: ", getWidth()-130, 40);
        g.drawString(""+highScore,getWidth()-85,60);
    }
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        // Dakika ve saniyeyi iki haneli olarak formatla
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
    private void gameOver(Graphics g) {

        String msg = "Oyun Bitti!";
        Font small = new Font("Helvetica", Font.BOLD, 15);
        FontMetrics metr = getFontMetrics(small);
        g.drawImage(backgroundImage4, 0, 0, this);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);

        restartButton.setVisible(true);
    }

    private void pauseGame() {
        gamePaused = true;
        timer.stop();
        restartButton.setText("<html>Oyun Duraklatıldı!<br>" +
                "Devam etmek için 'Boşluk'a basınız.<br>Ya da <br>-Yeniden başlat-</html>");
        restartButton.setVisible(true);
        
    }

    private void resumeGame() {
        gamePaused = false;
        timer.start();
        restartButton.setText("Yeniden Başla");
        restartButton.setVisible(false);
    }

    private void restartGame() {
        // Yılanın başlangıç pozisyonunu sıfırla
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }

        // Yılanın boyutunu başlangıç boyutuna ayarla
        dots = 1;

        // Yeniden başlatıldığında elmayı yerleştir
        locateApple();

        // Skoru sıfırla
        score = 0;

        // Geçen süreyi sıfırla
        elapsedSeconds = 0;

        level =1;

        // Oyun durumunu başlat
        inGame = true;

        // Yeniden başla butonunu gizle
        restartButton.setVisible(false);
        // Timer'ı durdur ve başlangıç değerine ayarla
        timer.stop();
        timer.setDelay(DELAY);

        // Timer'ı başlat
        timer.start();
    }
    private void checkApple() {
        //yılanın elmayı yeme durumu
        if ((x[0] == apple_x) && (y[0] == apple_y)) {

            dots++;
            locateApple();
            score++;
            updateHighScore();
        }
    }

    private void move() {
        //yılanın hareketi sağlanıyor
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    private void checkCollision() {

        for (int z = dots; z > 0; z--) {
            //yılanın başından kuyruğuna kadar kendine çarpışmasının kontrolu

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }
        //yılanın ekranın alt kısmına çarpmasının kontrolu
        if (y[0] >= B_HEIGHT) {
            inGame = false;
        }
        //yılanın ekranın üst kısmına çarpmasının kontrolu
        if (y[0] < 0) {
            inGame = false;
        }
        //yılanın ekranın sağ kısmına çarpmasının kontrolu
        if (x[0] >= B_WIDTH) {
            inGame = false;
        }
        //yılanın ekranın sol kısmına çarpmasının kontrolu
        if (x[0] < 0) {
            inGame = false;
        }
        //eğer kenarlara veya kendisine çarpma durumu olursa timer durdurulur.
        if (!inGame) {
            timer.stop();
        }
    }

    private void locateApple() {
        //elmanın rastgele yerleştirilmesi x ve y konumlarının belirlenmesi
        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkCollision();
            move();
            // Geçen süreyi hesapla ve güncelle
            long currentTime = System.currentTimeMillis();
            elapsedSeconds = (int) ((currentTime - startTime) / 1000);
            checkLevelUp();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE) {
                if (gameStarted) {
                    if (!gamePaused) {
                        pauseGame();
                    } else {
                        resumeGame();
                    }
                }
            }
            //sola basıldığında sağa doğru gitmiyorsa sola döner
            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }
            //sağa basıldığında sola doğru gitmiyorsa sağa döner
            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }
            //yukarı basıldığında aşağı doğru gitmiyorsa yukarı döner
            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
            //aşağı basıldığında yukarı doğru gitmiyorsa aşağı döner
            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}