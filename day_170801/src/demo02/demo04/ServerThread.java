package demo02.demo04;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerThread {

    public static void main(String[] args) {
        try {
            //服务器端
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("服务器启动-------");
            while (true) {
                //接收客户端的请求
                Socket clientSocket = serverSocket.accept();

                //将客户端的请求交给其他线程处理
                ThreadClient threadClient = new ThreadClient(clientSocket);
                //启动线程
                threadClient.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
//处理客户端请求的线程
class ThreadClient extends Thread {
    private Socket clientSocket = null;//要执行的Socket对象

    /**
     * 初始化要处理的客户端请求
     * @param clientSocket 要处理的客户端请求
     */
    public ThreadClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }//ThreadClient--end

    @Override
    public void run() {
        DataInputStream clientStream = null;
        try {
            clientStream = new DataInputStream(this.clientSocket.getInputStream());
            //客户端发送的指令
            String[] clientOrder = clientStream.readUTF().split(",");//指令和指令参数之间使用","隔开
            //指令要执行的操作
            clientOrder(clientOrder);
        } catch (IOException i) {
            i.printStackTrace();
        } finally {
            if (clientStream != null) {
                try {
                    clientStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (this.clientSocket != null) {
                try {
                    this.clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }//run--end

    /**
     *判断是哪种类型的指令
     * @param clientOrder 指令集合
     */
    private void clientOrder(String[] clientOrder) {
        //读文件
        if ("Get".equals(clientOrder[0])) {
            String filePath = clientOrder[1];
            readClientFile(filePath);
        } else if ("Time".equals(clientOrder[0])) {//获取当前日期
            getCurrentTime();
        } else {//系统中不存在此指令
            errorOrder();
        }
    }//clientOrder--end

    /**
     * 根据文件路径读取文件
     * @param filePath 文件路径
     */
    private void readClientFile(String filePath) {
        StringBuilder sBuilder = new StringBuilder();
        File file = new File(filePath);
        //判读文件是否存在
        if (file.exists()) {
            FileInputStream fileInputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;

            try {
                fileInputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(fileInputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                String oneLine = null;
                //读取文件中的内容
                while ((oneLine = bufferedReader.readLine()) != null) {
                    sBuilder.append(oneLine+"\n");
                }
            } catch (IOException i) {
                i.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //将文件内容打印到控制台中
            System.out.println(sBuilder.toString());
            //向客户端返回处理结果
            printClientInfo(filePath+"文件读取成功");
        } else {
            //向客户端返回处理结果
            printClientInfo("Not Found File："+filePath);
        }//readClientFile--end
    }

    /**
     * 获取当前时间
     */
    private void getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = simpleDateFormat.format(new Date());
        //向客户端返回结果
        printClientInfo(currentTime);
    }//getCurrentTime--end

    /**
     * 不包含该指令
     */
    private void errorOrder() {
        //向客户端返回结果
        printClientInfo("没有该指令");
    }//errorOrder--end

    /**
     * 返回给客户端的信息
     * @param clientValue 返回给客户端的信息
     */
    private void printClientInfo(String clientValue) {
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bWriter = null;

        try {
            //获取客户端输出流对象
            outputStream = this.clientSocket.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);
            bWriter = new BufferedWriter(outputStreamWriter);
            //向客户端输出内容
            bWriter.write(clientValue);

            bWriter.flush();
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (bWriter != null) {
                try {
                    bWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }//printClientInfo--end
}
