package demo02.demo04;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * 模拟客户端线程
 * 1.客户端通过创建Socket(服务器端IP,端口号)套接字和服务器端建立连接
 * 2.获取控制台输入流对象
 * 3.获取服务器端输出流对象
 * 4.获取服务器端输入流对象
 */
public class ClientThread {

    public static void main(String[] args) {
        Socket socket = null;
        DataOutputStream dataOutputStream = null;
        Scanner scanner = new Scanner(System.in);//控制台输入流
        BufferedReader bufferedReader = null;

        try {
            socket = new Socket("127.0.0.1", 8888);//创建客户端线程

            dataOutputStream = new DataOutputStream(socket.getOutputStream());//获取服务端输入流对象
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));//获取服务端的输出流对象
            System.out.println("please your order（指令和参数之间使用\",\"隔开,例子：Get,demo.txt）：");
            String order = scanner.next();//读控制台输出的内容
            dataOutputStream.writeUTF(order);//将内容写入到服务器端

            String res = null;
            StringBuilder sBuilder = new StringBuilder();
            while ((res = bufferedReader.readLine()) != null) {//读服务器端返回的信息
                sBuilder.append(res);
            }
            System.out.println(sBuilder.toString());
        } catch (IOException io) {
            io.printStackTrace();
        } finally {//关闭流对象和Socket对象
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}

