package com.red_social.controller.dao.implement;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Scanner;

import com.red_social.controller.tda.list.LinkedList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

// Clase AdapterDao
public class AdapterDao<T> implements InterfazDao<T> {
    private Class<T> clazz;
    protected Gson g;
    public String URL = "media/";

    public AdapterDao(Class<T> clazz) {
        this.clazz = clazz;
        this.g = new Gson();
    }

    public void persist(T object) throws Exception {
        LinkedList<T> list = listAll();
        list.add(object);

        String info;
        try {
            info = g.toJson(list.toArray());
        } catch (Exception e) {
            throw new Exception("Error al convertir a JSON: " + e.getMessage(), e);
        }
        saveFile(info);
    }

    public void merge(Integer index, T object) throws Exception {
        LinkedList<T> list = listAll();
        if (index < 0 || index >= list.size()) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }
        list.set(index, object);

        String info;
        try {
            info = g.toJson(list.toArray());
        } catch (Exception e) {
            throw new Exception("Error al convertir a JSON: " + e.getMessage(), e);
        }
        saveFile(info);
    }

    public LinkedList<T> listAll() throws Exception {
        LinkedList<T> list = new LinkedList<>();
        try {
            String data = readFile();

            // Obtén el tipo correcto para la deserialización
            Type listType = new TypeToken<T[]>() {}.getType();
            T[] arrayObjects = g.fromJson(data, listType);

            for (T obj : arrayObjects) {
                if (obj != null) {
                    list.add(obj);
                }
            }

        } catch (Exception e) {
            throw new Exception("Error al convertir a JSON: " + e.getMessage(), e);
        }
        return list;
    }

    public T get(Integer id) throws Exception {
        LinkedList<T> list = listAll();
        if (id <= 0 || id > list.size()) {
            throw new IndexOutOfBoundsException("ID fuera de rango: " + id);
        }
        return list.get(id - 1);
    }

    private String readFile() throws Exception {
        File file = new File(URL + clazz.getSimpleName() + ".json");
        if (!file.exists()) {
            return "[]"; 
        }

        try (Scanner in = new Scanner(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            while (in.hasNext()) {
                sb.append(in.next());
            }
            return sb.toString();
        }
    }

    private void saveFile(String info) throws Exception {
        File dir = new File(URL);
        if (!dir.exists()) {
            dir.mkdirs(); 
        }

        File file = new File(URL + clazz.getSimpleName() + ".json");
        try (FileWriter f = new FileWriter(file)) {
            f.write(info);
            f.flush();
        }
    }
}
