/*import Connection.util.Connectionutil;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TCPServer {
    public static void main(String[] args) {
        int port = 2525; // 服务器监听的端口号
        String username = ""; // 存储用户名
        String password= ""; // 存储密码
        String amountExtract="";
        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket clientSocket = serverSocket.accept();
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received：" + inputLine);

                if (inputLine.startsWith("HELO ")) {
                    Connection connection = Connectionutil.getConn();
                    String[] parts = inputLine.split(" ");
                    username = parts[1];
                    String sql = "select UserID from bank where UserID ='" + username + "'";
                    try {
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next()) {
                            System.out.println("User Success!");
                            out.println("500 AUTH REQUIRED!");
                        } else {
                            System.out.println("User Failed!");
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                } else if (inputLine.startsWith("PASS ")) {
                    Connection connection = Connectionutil.getConn();
                    String[] parts = inputLine.split(" ");
                    password = parts[1];
                    String sql = "select password from bank where password ='" + password + "' and UserID ='"+username+"'";
                    try {
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next()) {
                            System.out.println("Password is OK!");
                            out.println("525 OK!");
                        } else {
                            System.out.println("Password is Wrong!");
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                } else if (inputLine.equals("BALA")) {
                    Connection connection = Connectionutil.getConn();
                    String sql = "select amount from bank where UserID ='"+username+"'";
                    try {
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next()) {
                            double amount =resultSet.getDouble("amount");
                            out.println("AMNT:"+amount);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else if (inputLine.startsWith("WDRA ")) {
                    Connection connection = Connectionutil.getConn();
                    String[] parts = inputLine.split(" ");
                    amountExtract = parts[1];
                    double amountToExtract=Double.parseDouble(amountExtract);
                    String sql = "select amount from bank where UserID ='"+username+"'";
                    try {
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        if(resultSet.next()) {
                            double amountTemp=resultSet.getDouble("amount");
                            if(amountTemp>=amountToExtract){
                                out.println("525 OK!");
                                String sql1="update bank set amount =amount -'"+amountToExtract+"'where UserID='"+username+"'";
                                int resultSet1 = statement.executeUpdate(sql1);
                            }
                            else{
                                out.println("401 ERROR!");
                            }
                        }


                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else if (inputLine.equals("BYE")) {
                    out.println("BYE");
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/
import Connection.util.Connectionutil;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TCPServer extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public TCPServer(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void run() {
        int port = 2525; // 服务器监听的端口号
        String username = ""; // 存储用户名
        String password = ""; // 存储密码
        String amountExtract = "";

        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received：" + inputLine);

                if (inputLine.startsWith("HELO ")) {
                    Connection connection = Connectionutil.getConn();
                    String[] parts = inputLine.split(" ");
                    if (parts.length > 1) {
                        username = parts[1];
                    }
                    String sql = "select UserID from bank where UserID ='" + username + "'";
                    try {
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next()) {
                            System.out.println("User Success!");
                            out.println("500 AUTH REQUIRED!");
                            LocalDateTime currentTime = LocalDateTime.now();
                            String data = currentTime+":用户"+username+"账号验证成功！\n";
                            File file = new File("message.txt");
                            FileWriter fileWritter = new FileWriter(file.getName(),true);
                            fileWritter.write(data);
                            fileWritter.close();
                        } else {
                            System.out.println("User Failed!");
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else if (inputLine.startsWith("PASS ")) {
                    Connection connection = Connectionutil.getConn();
                    String[] parts = inputLine.split(" ");
                        if (parts.length > 1) { // 检查数组是否至少有两个元素
                            password = parts[1];
                        }

                    String sql = "select password from bank where password ='" + password + "' and UserID ='"+username+"'";
                    try {
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next()) {
                            System.out.println("Password is OK!");
                            out.println("525 OK!");
                            LocalDateTime currentTime = LocalDateTime.now();
                            String data = currentTime+":用户"+username+"口令通过验证！\n";
                            File file = new File("message.txt");
                            FileWriter fileWritter = new FileWriter(file.getName(),true);
                            fileWritter.write(data);
                            fileWritter.close();
                        } else {
                            System.out.println("Password is Wrong!");
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else if (inputLine.equals("BALA")) {
                    Connection connection = Connectionutil.getConn();
                    String sql = "select amount from bank where UserID ='"+username+"'";
                    try {
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next()) {
                            double amount =resultSet.getDouble("amount");
                            out.println("AMNT:"+amount);
                            LocalDateTime currentTime = LocalDateTime.now();
                            String data = currentTime+":用户"+username+"进行账户余额查询\n";
                            File file = new File("message.txt");
                            FileWriter fileWritter = new FileWriter(file.getName(),true);
                            fileWritter.write(data);
                            fileWritter.close();
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else if (inputLine.startsWith("WDRA ")) {
                    Connection connection = Connectionutil.getConn();
                    String[] parts = inputLine.split(" ");
                    amountExtract = parts[1];
                    double amountToExtract=0;
                    try{
                     amountToExtract=Double.parseDouble(amountExtract);
                    }
                    catch(NumberFormatException e){
                        out.println("401 ERROR!");
                    }
                    String sql = "select amount from bank where UserID ='"+username+"'";
                    try {
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        if(resultSet.next()) {
                            double amountTemp=resultSet.getDouble("amount");
                            if(amountTemp>=amountToExtract){
                                out.println("525 OK!");
                                LocalDateTime currentTime = LocalDateTime.now();
                                String data = currentTime+":用户"+username+"取出"+amountTemp+"\n";
                                File file = new File("message.txt");
                                FileWriter fileWritter = new FileWriter(file.getName(),true);
                                fileWritter.write(data);
                                fileWritter.close();
                                String sql1="update bank set amount =amount -'"+amountToExtract+"'where UserID='"+username+"'";
                                int resultSet1 = statement.executeUpdate(sql1);
                            }
                            else{
                                out.println("401 ERROR!");
                            }
                        }


                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else if (inputLine.equals("BYE")) {
                    out.println("BYE");
                    break;}


            }
            // 关闭资源
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(2525);
            System.out.println("Server is listening on port " + 2525);
        } catch (IOException e) {
            System.out.println("Could not start server on port " + 2525);
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                Socket client = serverSocket.accept();
                TCPServer handler = new TCPServer(client);
                handler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}