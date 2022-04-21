package com.com.serializable;

import java.io.*;

/**
 * @author admin
 * @create 2022-04-15 15:04
 * @
 */
public class StudentSeri {

    public static void main(String[] args) {
        Student stu = new Student(1, 20, "张三", "南岸区");
        try {
            ObjectOutputStream os = new ObjectOutputStream(
                    new FileOutputStream("/OutPutFile"));
            os.writeObject(stu);
            os.close();
        } catch (
                FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (
                IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
