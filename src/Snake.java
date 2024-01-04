import java.awt.EventQueue;
import javax.swing.JFrame;
/*Seyfullah Seyfi
* 2019123044
* 16.Grup
* Barış Yazman*/
public class Snake extends JFrame {

    public Snake() {
        initUI();
    }

    private void initUI() {
        //Board sınıfını JFrame'e ekler
        add(new Board());
        // Pencerenin boyutunu değiştirmeyi engelle
        setResizable(false);
        // Pencerenin boyutunu içerik boyutuna ayarla
        pack();

        setTitle("Snake");
        // Pencereyi ekranın ortasına yerleştir
        setLocationRelativeTo(null);
        // Pencere kapatıldığında uygulamanın sonra erdirilmesi
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            // Snake sınıfından bir nesne oluşturarak oyun penceresini başlat
            JFrame ex = new Snake();
            // Pencereyi görünür yapıyor
            ex.setVisible(true);
        });
    }

}