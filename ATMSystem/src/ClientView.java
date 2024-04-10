import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
public class ClientView extends JFrame {
    private JButton balanceButton;
    private JButton withdrawButton;
    private JButton quitButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientView(Socket socket, PrintWriter out, BufferedReader in){
        this.socket = socket;
        this.out = out;
        this.in = in;

        setTitle("ATM");
        setSize(400,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建一个带有背景图片的 JLabel
        ImageIcon backgroundImage = new ImageIcon("src\\images\\clientBackground.png"); // 请将"path/to/your/image.jpg"替换为你的图片路径
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, 400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.add(backgroundLabel);

        balanceButton = new JButton("余额查询");
        balanceButton.setBounds(150, 50, 100, 30);
        backgroundLabel.add(balanceButton);
        balanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    out.println("BALA");
                    String response = in.readLine();
                    if (response.startsWith("AMNT:")) {
                        String amount = response.substring(5);
                        JOptionPane.showMessageDialog(ClientView.this, "您的余额：" + amount + "元");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        withdrawButton = new JButton("取款");
        withdrawButton.setBounds(150, 130, 100, 30);
        backgroundLabel.add(withdrawButton);
        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String amountStr = JOptionPane.showInputDialog(ClientView.this, "请输入取款额度:");
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (amount > 0) {
                        out.println("WDRA " + amount);
                        String response = in.readLine();
                        if (response.equals("525 OK!")) {
                            JOptionPane.showMessageDialog(ClientView.this, "取款成功！");
                        } else if (response.equals("401 ERROR!")) {
                            JOptionPane.showMessageDialog(ClientView.this, "余额不足！");
                        }
                    } else {
                        JOptionPane.showMessageDialog(ClientView.this, "请输入正确的取款额度！");
                    }
                } catch (NumberFormatException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        quitButton = new JButton("退出");
        quitButton.setBounds(150, 210, 100, 30);
        backgroundLabel.add(quitButton);
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(ClientView.this, "确定退出ATM系统吗？");
                if (option == JOptionPane.OK_OPTION) {
                    out.println("BYE");
                    try {
                        socket.close();
                        out.close();
                        in.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    dispose(); // 关闭窗口
                }
            }
        });

        add(panel);
    }

}
