package com.vrseen;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * 自动创建数据库和表
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.vrseen.vrstore.db");
        addUser(schema);
        addDownLoadInfo(schema);
        DaoGenerator dg = new DaoGenerator();
        dg.generateAll(schema, "./app/src/main/java");
    }

    private static void addUser(Schema schema) {
        Entity user = schema.addEntity("UserInfo");
        user.addStringProperty("phone").primaryKey();
        user.addStringProperty("pwd");
    }

    private static void addDownLoadInfo(Schema schema) {
        Entity user = schema.addEntity("DownInfo");
        user.addStringProperty("phone").primaryKey();
        user.addStringProperty("pwd");
    }
}
