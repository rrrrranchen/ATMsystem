package Connection.util;

import java.sql.*;

public class Connectionutil {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/user";
    private static final String USER = "root";
    private static final String PASSWORD = "ac040922";

    public static Connection getConn(){
        Connection connection=null;
        try {
            Class.forName(DRIVER);
            connection=DriverManager.getConnection(URL,USER,PASSWORD);
            //System.out.println("连接成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
    public static void close(Connection conn, Statement st, ResultSet rst){
        if(rst!=null){
            try{
                rst.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(conn!=null){
            try{
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(st!=null){
            try{
                st.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
