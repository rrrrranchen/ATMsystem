import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class LoginView extends JFrame {
    private JTextField useridField;
    private JButton useridButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public LoginView(){
        setTitle("ATM登录");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建一个带有背景图片的 JLabel
        ImageIcon backgroundImage = new ImageIcon("src\\images\\background.jpg"); // 请将"path/to/your/image.jpg"替换为你的图片路径
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, 350, 200);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.add(backgroundLabel);

        JLabel userLabel = new JLabel("用户名:");
        userLabel.setBounds(30,20,80,25);
        userLabel.setForeground(Color.BLACK); //设置文字颜色为白色
        backgroundLabel.add(userLabel);
        useridField = new JTextField(20);
        useridField.setBounds(130,20,165,25);
        backgroundLabel.add(useridField);

        useridButton = new JButton("确认");
        useridButton.setBounds(180, 80, 80, 25);
        backgroundLabel.add(useridButton);

        add(panel);
        // 添加按钮点击事件监听器
        useridButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = useridField.getText();
                // 建立与服务器的连接
                try {
                    socket = new Socket("10.234.107.70", 2525); // 替换为你的服务器IP和端口
                    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // 发送用户名到服务器
                    out.println("HELO "+userId);

                    // 接收服务器响应
                    String response = in.readLine();
                    //String response = "500 AUTH REQUIRED!";
                    if (response.equals("500 AUTH REQUIRED!")) {
                        // 打开密码输入窗口
                        showPasswordInputDialog();
                    } else if (response.equals("401 ERROR!")) {
                        // 提示用户名错误，重新输入
                        JOptionPane.showMessageDialog(null, "用户名输入错误，请重新输入。", "错误", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // 处理其他响应，根据需求进行修改
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    private void showPasswordInputDialog() {
        JFrame passwordFrame = new JFrame("密码输入");
        passwordFrame.setSize(300, 150);
        passwordFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(null);

        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setBounds(30, 20, 80, 25);
        passwordPanel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBounds(130, 20, 140, 25);
        passwordPanel.add(passwordField);

        JButton passwordButton = new JButton("确认");
        passwordButton.setBounds(100, 70, 80, 25);
        passwordPanel.add(passwordButton);

        passwordFrame.add(passwordPanel);
        passwordFrame.setVisible(true);

        // 添加按钮点击事件监听器
        passwordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = new String(passwordField.getPassword());
                try {
                    // 发送密码到服务器
                    out.println("PASS "+password);

                    // 接收服务器响应
                    String response = in.readLine();
                    if (response.equals("525 OK!")) {
                        // 密码正确，进入客户端界面
                        // 这里可以调用进入客户端界面的方法
                        ClientView clientView = new ClientView(socket, out, in);
                        clientView.setVisible(true);
                        passwordFrame.dispose();
                        dispose();

                    } else if (response.equals("401 ERROR!")) {
                        // 提示密码错误，重新输入
                        JOptionPane.showMessageDialog(null, "密码错误，请重新输入。", "错误", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // 处理其他响应，根据需求进行修改
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                passwordFrame.dispose(); // 关闭密码输入窗口
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginView().setVisible(true);
            }
        });
    }
}
